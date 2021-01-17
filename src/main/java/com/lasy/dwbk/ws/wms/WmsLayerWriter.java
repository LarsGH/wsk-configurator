package com.lasy.dwbk.ws.wms;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
  
  private WmsLayerWriter(LayerModel layer)
  {
    this.layer = Check.notNull(layer, "layer");
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

    queryAllTilesAndWriteToTileEntry(tileMatrixParams, tileEntry, gpkg);
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
  
  // TODO: mit pyramide groesserem bereich testen! 1;5

  // TODO: bei request + schreiben mit mehreren Threads arbeiten?!
  // TODO: WmsQueryParameters als Wrapper mit getter für QueryParameters?!
  private void queryMatrixTilesAndWriteToTileEntry(WebMapServer wms, TileMatrixParams tileMatrixParam, Map<String, String> queryParams, TileEntry tileEntry,
    GeoPackage gpkg)
  {
    GetMapRequest request = createBasicRequest(wms, queryParams);
    request.setDimensions(tileMatrixParam.getTileWidthInPixels(), tileMatrixParam.getTileHeightInPixels());

    ReferencedEnvelope matrixBbox = tileMatrixParam.getMatrixBbox();
    double bboxMinLon = matrixBbox.getMinX();
    double bboxMaxLat = matrixBbox.getMaxY();
    int tileWidthInMeters = tileMatrixParam.getTileWidthInMeters();
    int tileHeightInMeters = tileMatrixParam.getTileHeightInMeters();
    int tilesAddedCount = 0;

    // Matrix format (Y(col),X(row)):
    // (0,0) | (1,0)
    // (0,1) | (1,1)
    
    // Latitude -> row
    for (int row = 0; row < tileMatrixParam.getMatrixHeight(); row++ )
    {
      double currentMaxLat = bboxMaxLat - (row * tileHeightInMeters);
      double currentMinLat = currentMaxLat - tileHeightInMeters;

      // Longitude -> col
      for (int col = 0; col < tileMatrixParam.getMatrixWidth(); col++ )
      {
        double currentMinLon = bboxMinLon + (col * tileWidthInMeters);
        double currentMaxLon = currentMinLon + tileWidthInMeters;

        request.setBBox(String.format("%s,%s,%s,%s", currentMinLon, currentMinLat, currentMaxLon, currentMaxLat));

        try
        {
          DwbkLog.log(Level.FINEST, "Sending request: %n%s", request.getFinalURL());

          GetMapResponse response = wms.issueRequest(request);
          InputStream is = response.getInputStream();
          try
          {
            Tile tile = new Tile(tileMatrixParam.getZoomLevel(), col, row, is.readAllBytes());
            gpkg.add(tileEntry, tile);
            tilesAddedCount++ ;
            if (tilesAddedCount % 100 == 0)
            {
              DwbkLog.log(Level.INFO, "%s/%s Tiles für Zoomstufe %s gespeichert.", tilesAddedCount, tileMatrixParam.getTotalTileCount(),
                tileMatrixParam.getZoomLevel());
            }
          }
          finally
          {
            is.close();
          }
        }
        catch (Exception e)
        {
          throw DwbkFrameworkException.failForReason(e, "Fehler beim Schreiben der Layer-Kacheln für den Layer '%s'!", this.layer.getName());
        }
      }
    }
    DwbkLog.log(Level.INFO, "Alle Tiles (%s) für Zoomstufe %s gespeichert.", tilesAddedCount, tileMatrixParam.getZoomLevel());
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
