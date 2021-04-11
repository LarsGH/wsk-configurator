package com.lasy.wsk.ws.wms;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
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
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.lasy.wsk.app.WskFramework;
import com.lasy.wsk.app.error.ErrorModule;
import com.lasy.wsk.app.error.WskFrameworkException;
import com.lasy.wsk.app.logging.WskLog;
import com.lasy.wsk.app.model.impl.LayerModel;
import com.lasy.wsk.db.util.DbScriptUtil;
import com.lasy.wsk.util.BboxUtil;
import com.lasy.wsk.util.Check;
import com.lasy.wsk.ws.ILayerWriter;

/**
 * Writes the layer content to the geopackage.
 * @author larss
 *
 */
public class WmsLayerWriter implements ILayerWriter
{
  private final LayerModel layer;
  
  private final ExecutorService threadPool;
  private final LinkedBlockingQueue<Future<WmsTileResponse>> responseQueue;
  
  /** Exception in case something goes wrong while trying to query / write tiles. */
  private WskFrameworkException writerException;
  
  public WmsLayerWriter(LayerModel layer)
  {
    this.layer = Check.notNull(layer, "layer");
    
    // configured WMS request threads + writer thread
    int poolSize = WskFramework.getInstance().getSettings().getWmsMaxThreads() + 1;
    this.threadPool = Executors.newFixedThreadPool(poolSize);
    this.responseQueue = new LinkedBlockingQueue<>(5);
  }
  
  @Override
  public void write()
  {
    try
    {
      GeoPackage gpkg = WskFramework.getInstance().getWskGeoPackage().getGtGeoPackage();
      DbScriptUtil.deleteLocalWmsLayerContentIfPresent(this.layer);
      
      List<TileMatrixParams> tileMatrixParams = TileMatrixParams.createForLayer(this.layer);
      checkValidTileCount(tileMatrixParams);
      
      TileEntry tileEntry = createTileEntry(tileMatrixParams, gpkg);
      
      // Single thread to write tiles
      this.threadPool.execute(createTileWriterThread(tileEntry, gpkg));
      
      queryAllTilesAndWriteToTileEntry(tileMatrixParams, tileEntry, gpkg);
      
      WskLog.log(Level.INFO, "Alle Tiles für Layer '%s' geschrieben.", this.layer.getName());
    }
    catch (Exception e)
    {
      if(this.writerException != null)
      {
        throw this.writerException;
      }
      throw ErrorModule.createFrameworkException(e, t -> WskFrameworkException
        .failForReason(t, "Unbekannter Fehler beim Schreiben der WMS-Kacheln."));
    }
    finally
    {
      this.threadPool.shutdown();
    }
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
      throw WskFrameworkException.failForReason(new IllegalStateException(), 
        "Für den Layer ('%s') konnten keine Kacheln berechnet werden.%n", this.layer.getName());
    }
    
    WskLog.log(Level.INFO, "Gesamtanzahl der zu schreibenden Tiles: %s", totalTileCount);
  }

  private void queryAllTilesAndWriteToTileEntry(List<TileMatrixParams> tileMatrixParams, TileEntry tileEntry, GeoPackage gpkg)
  {
    WebMapServer wms = createWebMapServer();
    WmsConfig wmsConfig = this.layer.getWmsConfig();
    
    for(TileMatrixParams tileMatrixParam : tileMatrixParams)
    {
      queryMatrixTilesAndWriteToTileEntry(wms, tileMatrixParam, wmsConfig, tileEntry, gpkg);
    }
  }
  
  private void queryMatrixTilesAndWriteToTileEntry(
    WebMapServer wms, 
    TileMatrixParams tileMatrixParam, 
    WmsConfig wmsConfig, 
    TileEntry tileEntry,
    GeoPackage gpkg)
  {
    final ReferencedEnvelope matrixBbox = tileMatrixParam.getMatrixBbox();
    final double bboxMinLon = matrixBbox.getMinX();
    final double bboxMaxLat = matrixBbox.getMaxY();
    final int tileWidthInMeters = tileMatrixParam.getTileWidthInMeters();
    final int tileHeightInMeters = tileMatrixParam.getTileHeightInMeters();
    final int currentZoom = tileMatrixParam.getZoomLevel();
    
    int requestEpsg = wmsConfig.getRequestEpsg();
    final CoordinateReferenceSystem transformCrs = requestEpsg != BboxUtil.EPSG_3857
      ? BboxUtil.getCrsForEpsgCode(requestEpsg)
      : null;
    
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
          GetMapRequest request = createBasicRequest(wms, wmsConfig);
          request.setDimensions(tileMatrixParam.getTileWidthInPixels(), tileMatrixParam.getTileHeightInPixels());
          
          if(transformCrs != null)
          {
            // Transformation necessary!
            ReferencedEnvelope envelope = new ReferencedEnvelope(
              currentMinLon, 
              currentMaxLon, 
              currentMinLat, 
              currentMaxLat, 
              matrixBbox.getCoordinateReferenceSystem());
            BoundingBox bbox = envelope.toBounds(transformCrs);
            request.setBBox(bbox);
          }
          else
          {
            request.setBBox(String.format("%s,%s,%s,%s", currentMinLon, currentMinLat, currentMaxLon, currentMaxLat));
          }
          
          final int currentRow = row;
          final int currentCol = col;
          
          if(isFirstRequest(currentZoom, currentRow, currentCol))
          {
            checkForValidRequestAndResponse(wms, currentZoom, request, currentRow, currentCol);
          }
          
          Future<WmsTileResponse> futureResponse = threadPool.submit(() -> {
            return createWmsTileResponse(wms, currentZoom, request, currentRow, currentCol);
          });
          responseQueue.put(futureResponse);
          
          tilesAddedCount++ ;
          if (tilesAddedCount % 100 == 0)
          {
            WskLog.log(Level.INFO, "%s/%s Tiles für Zoomstufe %s gespeichert.", tilesAddedCount, tileMatrixParam.getTotalTileCount(), currentZoom);
          }
        } 
        catch (Exception e)
        {
          throw ErrorModule.createFrameworkException(e, t -> WskFrameworkException
            .failForReason(t, "Fehler beim Schreiben der Layer-Kacheln für den Layer '%s'!", this.layer.getName()));
        }
      }
    }
    
    LocalDateTime end = LocalDateTime.now();
    long seconds = Duration.between(start, end).getSeconds();
    
    WskLog.log(Level.INFO, "Alle Tiles (%s) für Zoomstufe %s gespeichert. Dauer: %s", 
      tileMatrixParam.getTotalTileCount(), 
      currentZoom,
      seconds);
  }

  private boolean isFirstRequest(int zoom, int row, int col)
  {
    return zoom == 0
      && row == 0
      && col == 0;
  }

  private void checkForValidRequestAndResponse(WebMapServer wms, int currentZoom, GetMapRequest request, int currentRow, int currentCol)
  {
    try
    {
      // if no exception is thrown, request & response are valid
      WmsTileResponse response = createWmsTileResponse(wms, currentZoom, request, currentRow, currentCol);
      response.getResponse();
    }
    catch (Exception e)
    {
      throw ErrorModule.createFrameworkException(e, t -> WskFrameworkException
        .failForReason(t, "Request / Response für WMS GetMap ist fehlerhaft. URL: %s", request.getFinalURL()));
    }
  }

  private WmsTileResponse createWmsTileResponse(WebMapServer wms, final int currentZoom, GetMapRequest request, final int currentRow, final int currentCol)
  {
    try
    {
      WskLog.log(Level.FINER, "Sending request: %n%s", request.getFinalURL());
      GetMapResponse response = wms.issueRequest(request);
      return new WmsTileResponse(response, currentZoom, currentRow, currentCol, request.getFinalURL());
    }
    catch (Exception e)
    {
      this.setWriterException(e);
      return null;
    }
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
            
            WskLog.log(Level.FINEST, "Tile geschrieben: [z:%s, r:%s, c:%s]", 
              response.getZoom(), response.getRow(), response.getCol());
          }
          finally
          {
            is.close();
          }
        }
      }
      catch (Exception e)
      {
        this.setWriterException(e);
      }
    };
  }

  private void setWriterException(Exception e)
  {
    this.writerException = ErrorModule.createFrameworkException(e, t -> WskFrameworkException
      .failForReason(t, "Fehler beim Speichern von WMS-Kacheln."));
    Thread.currentThread().interrupt();
  }

  /**
   * Creates the request with the basic parameters.
   * @param wms the GT WMS
   * @param wmsConfig the layer URI query parameters
   * @return request
   */
  private GetMapRequest createBasicRequest(WebMapServer wms, WmsConfig wmsConfig)
  {
    GetMapRequest request = wms.createGetMapRequest();
    request.setVersion(this.layer.getWebServiceVersion());
    request.addLayer(wmsConfig.getLayer(), wmsConfig.getStyles());
    
    request.setFormat(wmsConfig.getFormat());
    request.setTransparent(wmsConfig.isTransparent());
    
    String epsg = BboxUtil.getEpsgStringForCode(wmsConfig.getRequestEpsg());
    request.setSRS(epsg);
    return request;
  }

  /**
   * Creates the GT WMS from the layer URI query parameters.
   * @return GT WMS
   */
  private WebMapServer createWebMapServer()
  {
    try
    {
      String getCapabilitiesRequest = this.layer.getRequest();
      URL url = createUrl(getCapabilitiesRequest);
      WebMapServer wms = new WebMapServer(url);
      
      WmsConfig config = this.layer.getWmsConfig();
      int requestEpsg = config.getRequestEpsg();
      
      Set<String> supportedSrs = wms.getCapabilities().getLayer().getSrs();
      if(!supportedSrs.contains(BboxUtil.getEpsgStringForCode(requestEpsg)))
      {
        throw WskFrameworkException.failForReason(new IllegalStateException(), 
          "Service unterstützt das angegebene Referenzsystem (EPSG:%s) nicht! %n"
          + "GetCapabilities: %s", requestEpsg, getCapabilitiesRequest);
      }
      
      wms.getCapabilities().getRequest().getGetMap().setGet(createUrl(this.layer.getBaseRequest()));
      
      return wms;
    } 
    catch (Exception e)
    {
      throw ErrorModule.createFrameworkException(e, t -> WskFrameworkException
        .failForReason(t, "GT WebMapServer konnte nicht erstellt werden oder wird nicht unterstützt!"));
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
      WskLog.log(Level.INFO, "Neue Tile-Tabelle '%s' angelegt.", this.layer.getLocalName());
      return tileEntry;
    }
    catch (IOException e)
    {
      throw WskFrameworkException.failForReason(e, "Fehler beim Erstellen des lokalen Layers '%s'", this.layer.getName());
    }
  }
  
  /**
   * Writes the response to a file
   * @param response the WMS response
   * @deprecated writing to files should not be used!
   */
  @SuppressWarnings("unused")
  @Deprecated
  private void writeResponseToFile(GetMapResponse response) throws IOException
  {
    BufferedImage image = ImageIO.read(response.getInputStream());

    File imgFile = new File(com.lasy.wsk.app.WskEnvironment.getConfigDirectory(), "tiles/test.png");
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
