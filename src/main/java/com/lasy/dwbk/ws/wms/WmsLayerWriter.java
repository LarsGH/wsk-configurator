package com.lasy.dwbk.ws.wms;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import javax.imageio.ImageIO;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geopkg.GeoPackage;
import org.geotools.geopkg.Tile;
import org.geotools.geopkg.TileEntry;
import org.geotools.geopkg.TileMatrix;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.ows.wms.request.GetMapRequest;
import org.geotools.ows.wms.response.GetMapResponse;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.app.error.DwbkFrameworkException;
import com.lasy.dwbk.app.logging.DwbkLog;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.db.util.DbScriptUtil;
import com.lasy.dwbk.util.BboxUtil;
import com.lasy.dwbk.util.Check;
import com.lasy.dwbk.ws.QueryParameters;

// TODO: Abstract class ALayerWriter for WMS / WFS impls?
/**
 * Writes the layer content to the geopackage.
 * @author larss
 *
 */
public class WmsLayerWriter
{
  
  /**
   * Creates a new WMS layer writer
   * @param layer the layer to write
   * @return layer writer
   */
  public static WmsLayerWriter createForLayer(LayerModel layer)
  {
    return new WmsLayerWriter(layer);
  }

  private final LayerModel layer;
  
  private final ExecutorService threadPool;
  private final LinkedBlockingQueue<Future<WmsTileResponse>> responseQueue;
  
  private WmsLayerWriter(LayerModel layer)
  {
    this.layer = Check.notNull(layer, "layer");
    
    // TODO: thread-pool / response-queue size konfigurierbar settings (json)
    this.threadPool = Executors.newFixedThreadPool(10);
    this.responseQueue = new LinkedBlockingQueue<>(10);
  }
  
  // TODO: nach write -> layer.updateLastDownloadDate und crud.update!
  /**
   * Creates the local layer in the geopackage.
   * @return {@code true} if the layer was written successfully
   */
  public void write()
  {
    GeoPackage gpkg = DwbkFramework.getInstance().getDwbkGeoPackage().getGtGeoPackage();
    deleteCurrentTileEntryIfPresent(gpkg);

    List<TileMatrixParams> tileMatrixParams = TileMatrixParams.createForLayer(this.layer);
    checkValidTileCount(tileMatrixParams);
    
    TileEntry tileEntry = createTileEntry(tileMatrixParams, gpkg);

    // Single thread to write tiles
    threadPool.execute(createTileWriterThread(tileEntry, gpkg));
    
    queryAllTilesAndWriteToTileEntry(tileMatrixParams, tileEntry, gpkg);
    
    threadPool.shutdown();
    DwbkLog.log(Level.INFO, "Alle Tiles für Layer '%s' geschrieben.", this.layer.getName());
  }

  /**
   * Validates that there is at least one tile to write.
   * @param tileMatrixParams the matrix parameters to write
   */
  private void checkValidTileCount(List<TileMatrixParams> tileMatrixParams)
  {
    int totalTileCount = tileMatrixParams.stream()
    .mapToInt(matrixParam -> matrixParam.getTotalTileCount())
    .sum();
    
    if(totalTileCount < 1)
    {
      String msg = String.format("No Tiles to write for layer: %s", this.layer.getName());
      throw new IllegalStateException(msg);
    }
    
    DwbkLog.log(Level.INFO, "Gesamtanzahl der zu schreibenden Tiles: %s", totalTileCount);
  }

  private void queryAllTilesAndWriteToTileEntry(List<TileMatrixParams> tileMatrixParams, TileEntry tileEntry, GeoPackage gpkg)
  {
    QueryParameters queryParameters = QueryParameters.fromLayerUri(this.layer.getUri());
    WebMapServer wms = createWebMapServer(queryParameters);
    
    for(TileMatrixParams tileMatrixParam : tileMatrixParams)
    {
      queryMatrixTilesAndWriteToTileEntry(wms, tileMatrixParam, queryParameters.getParams(), tileEntry, gpkg);
    }
  }
  
  // TODO: WmsQueryParameters als Wrapper mit getter für QueryParameters?!
  private void queryMatrixTilesAndWriteToTileEntry(WebMapServer wms, TileMatrixParams tileMatrixParam, Map<String, String> queryParams, TileEntry tileEntry,
    GeoPackage gpkg)
  {
    final ReferencedEnvelope matrixBbox = tileMatrixParam.getMatrixBbox();
    final double bboxMinLon = matrixBbox.getMinX();
    final double bboxMaxLat = matrixBbox.getMaxY();
    final int tileWidthInMeters = tileMatrixParam.getTileWidthInMeters();
    final int tileHeightInMeters = tileMatrixParam.getTileHeightInMeters();
    final int currentZoom = tileMatrixParam.getZoomLevel();
    
    int tilesAddedCount = 0;
    
    // Matrix format (Col, Row):
    // (0,0) | (1,0)
    // (0,1) | (1,1)
    
    LocalDateTime start = LocalDateTime.now();
    
    // Multiple threads to query tiles 
    // Latitude -> row
    for (int row = 0; row < tileMatrixParam.getMatrixHeight(); row++ )
    {
      final double currentMaxLat = bboxMaxLat - (row * tileHeightInMeters);
      final double currentMinLat = currentMaxLat - tileHeightInMeters;

      // Longitude -> col
      for (int col = 0; col < tileMatrixParam.getMatrixWidth(); col++ )
      {
        final double currentMinLon = bboxMinLon + (col * tileWidthInMeters);
        final double currentMaxLon = currentMinLon + tileWidthInMeters;

        try
        {
          GetMapRequest request = createBasicRequest(wms, queryParams);
          request.setDimensions(tileMatrixParam.getTileWidthInPixels(), tileMatrixParam.getTileHeightInPixels());
          request.setBBox(String.format("%s,%s,%s,%s", currentMinLon, currentMinLat, currentMaxLon, currentMaxLat));
          final int currentRow = row;
          final int currentCol = col;
          Future<WmsTileResponse> futureResponse = threadPool.submit(() -> {
            DwbkLog.log(Level.FINER, "Sending request: %n%s", request.getFinalURL());
            GetMapResponse response = wms.issueRequest(request);
            return new WmsTileResponse(response, currentZoom, currentRow, currentCol);
          });
          responseQueue.put(futureResponse);
          
          tilesAddedCount++ ;
          if (tilesAddedCount % 100 == 0)
          {
            DwbkLog.log(Level.INFO, "%s/%s Tiles für Zoomstufe %s gespeichert.", tilesAddedCount, tileMatrixParam.getTotalTileCount(), currentZoom);
          }
        }
        catch (Exception e)
        {
          throw DwbkFrameworkException.failForReason(e, "Fehler beim Schreiben der Layer-Kacheln für den Layer '%s'!", this.layer.getName());
        }
      }
    }
    
    LocalDateTime end = LocalDateTime.now();
    long seconds = Duration.between(start, end).getSeconds();
    
    DwbkLog.log(Level.INFO, "Alle Tiles (%s) für Zoomstufe %s gespeichert. Dauer: %s", 
      tileMatrixParam.getTotalTileCount(), 
      currentZoom,
      seconds);
  }

  private Runnable createTileWriterThread(TileEntry tileEntry, GeoPackage gpkg)
  {
    return () -> {
      try
      {
        while (!threadPool.isShutdown() 
          ||  (threadPool.isShutdown() && !responseQueue.isEmpty()))
        {
          Future<WmsTileResponse> futureResponse = responseQueue.take();
          WmsTileResponse response = futureResponse.get();
          InputStream is = response.getResponse().getInputStream();
          try
          {
            Tile tile = new Tile(response.getZoom(), response.getCol(), response.getRow(), is.readAllBytes());
            gpkg.add(tileEntry, tile);
            
//            DwbkLog.log(Level.INFO, "Tile geschrieben: [z:%s, r:%s, c:%s]", 
//              response.getZoom(), response.getRow(), response.getCol());
          }
          finally
          {
            is.close();
          }
        }
      }
      catch (Exception e)
      {
        threadPool.shutdownNow();
        throw DwbkFrameworkException.failForReason(e, "Beim Schreiben der Tiles ist ein unerwarteter Fehler aufgetreten!");
      }
    };
  }

  /**
   * Creates the request with the basic parameters.
   * @param wms the GT WMS
   * @param queryParams the layer URI query parameters
   * @return request
   */
  private GetMapRequest createBasicRequest(WebMapServer wms, Map<String, String> queryParams)
  {
    GetMapRequest request = wms.createGetMapRequest();
    String version = queryParams.get(WmsQueryConst.GetMap.VERSION);
    request.setVersion(version);
    
    String layerName = queryParams.get(WmsQueryConst.GetMap.LAYERS);
    String styleName = queryParams.get(WmsQueryConst.GetMap.STYLES);
    request.addLayer(layerName, styleName);
    
    String format = queryParams.get(WmsQueryConst.GetMap.FORMAT);
    request.setFormat(format);
    request.setSRS("EPSG:3857");
    String transparentStr = queryParams.getOrDefault(WmsQueryConst.GetMap.TRANSPARENT, "true");
    boolean transparent = Boolean.valueOf(transparentStr);
    request.setTransparent(transparent);
    return request;
  }

  /**
   * Creates the GT WMS from the layer URI query parameters.
   * @param queryParameters layer URI query parameters
   * @return GT WMS
   */
  private WebMapServer createWebMapServer(QueryParameters queryParameters)
  {
    try
    {
      URL url = createUrl(queryParameters.getCapablitiesRequest());
      WebMapServer wms = new WebMapServer(url);
      
      Set<String> supportedSrs = wms.getCapabilities().getLayer().getSrs();
      if(!supportedSrs.contains("EPSG:" + BboxUtil.EPSG_3857))
      {
        throw new IllegalStateException("Service is not supported because it does not support EPSG:3857");
      }
      
      wms.getCapabilities().getRequest().getGetMap().setGet(createUrl(queryParameters.getBaseRequest()));
      
      return wms;
    } 
    catch (Exception e)
    {
      throw DwbkFrameworkException.failForReason(e, "GT WebMapServer konnte nicht erstellt werden oder wird nicht unterstützt!", e);
    }
  }
  
  private URL createUrl(String url)
  {
    try
    {
      return new URL(url);
    } catch (Exception e)
    {
      String msg = String.format("Cannot create URL from '%s'", url);
      throw new IllegalStateException(msg, e);
    }
  }

  /**
   * Creates the new tile table with the matrices for each configured zoom level.
   * @param tileMatrixParams matrix parameters (for different zoom levels)
   * @param gpkg the geopackage
   * @return the created GT tile entry
   */
  private TileEntry createTileEntry(List<TileMatrixParams> tileMatrixParams, GeoPackage gpkg)
  {
    ReferencedEnvelope matrixBbox = tileMatrixParams.stream()
      .findFirst()
      .orElseThrow(() -> new IllegalStateException("No Matrix Params found!"))
      .getMatrixBbox();
    
    TileEntry tileEntry = new TileEntry();
    tileEntry.setSrid(BboxUtil.EPSG_3857);
    tileEntry.setTableName(this.layer.getLocalName());
    tileEntry.setBounds(matrixBbox);
    
    for(TileMatrixParams param : tileMatrixParams)
    {
      TileMatrix tileMatrix = new TileMatrix(
        param.getZoomLevel(), 
        param.getMatrixWidth(), 
        param.getMatrixHeight(), 
        param.getTileWidthInPixels(), 
        param.getTileHeightInPixels(), 
        (double) param.getMetersPerPixel(), 
        (double) param.getMetersPerPixel());
      
      tileEntry.getTileMatricies().add(tileMatrix);
    }
    
    try
    {
      gpkg.create(tileEntry);
      DwbkLog.log(Level.INFO, "Neue Tile-Tabelle '%s' angelegt.", this.layer.getLocalName());
      return tileEntry;
    }
    catch (IOException e)
    {
      throw DwbkFrameworkException.failForReason(e, "Fehler beim Erstellen des lokalen Layers '%s'", this.layer.getName());
    }
  }

  /**
   * Deletes the current layer content if it exists.
   * @param gpkg the geopackage
   */
  private void deleteCurrentTileEntryIfPresent(GeoPackage gpkg)
  {
    try
    {
      TileEntry tileEntry = gpkg.tile(this.layer.getLocalName());
      if(tileEntry != null)
      {
        DbScriptUtil.deleteGeneratedTable(gpkg, this.layer.getLocalName());
        DwbkLog.log(Level.INFO, "Offline Content von Layer '%s' wurde erfolgreich gelöscht.", this.layer.getName());
      }
    }
    catch (Exception e)
    {
      DwbkFrameworkException.failForReason(e, "Aktueller offline Content von Layer '%s' konnte nicht gelöscht werden!", this.layer.getName());
    }
  }
  
  /**
   * Writes the response to a file
   * @param response the WMS response
   * @deprecated writing to files should not be used!
   */
  @Deprecated
  private void writeResponseToFile(GetMapResponse response) throws IOException
  {
    BufferedImage image = ImageIO.read(response.getInputStream());

    File imgFile = new File(com.lasy.dwbk.app.DwbkEnvironment.getConfigDirectory(), "tiles/test.png");
    createFileIfNecessary(imgFile);
    boolean fileWritten = ImageIO.write(image, "PNG", imgFile);

    System.out.println("file written? " + fileWritten);
  }

  private void createFileIfNecessary(File imgFile) throws IOException
  {
    if (imgFile.exists())
    {
      imgFile.delete();
    }
    imgFile.getParentFile().mkdirs();
    imgFile.createNewFile();
  }
  
  
}
