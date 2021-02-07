package com.lasy.dwbk.ws.wms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.geotools.geometry.jts.ReferencedEnvelope;

import com.lasy.dwbk.app.error.DwbkFrameworkException;
import com.lasy.dwbk.app.logging.DwbkLog;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.util.BboxUtil;
import com.lasy.dwbk.util.Check;

/**
 * Parameters to create the tile matrix and tiles.
 * @author larss
 *
 */
public final class TileMatrixParams
{
  /** The default tile length in pixels. */
  public static final int DEFAULT_TILE_LENGTH = 500;
  
  /** 
   * The default tolerance in pixels.
   * A tile side in pixels must be <= ({@link DEFAULT_TILE_LENGTH} + {@link DEFAULT_TOLERANCE})
   */
  public static final int DEFAULT_TOLERANCE = 100;
  
  /**
   * Creates the tile matrix parameters for each configured pixel size of the layer.
   * @param layer the layer
   * @return list of matrix parameters (sorted by zoomlevel)
   */
  public static List<TileMatrixParams> createForLayer(LayerModel layer)
  {
    Check.notNull(layer, "layer");
    List<TileMatrixParams> matrixParams = new ArrayList<>();
    try
    {
      final ReferencedEnvelope matrixBbox = BboxUtil.getFittingBboxEnvelopeInEpsg3857(layer.getBbox(), DEFAULT_TILE_LENGTH);
      final int bboxWidthInMeters = (int) matrixBbox.getWidth();
      final int bboxHeightInMeters= (int) matrixBbox.getHeight();
      
      WmsConfig wmsConfig = layer.getWmsConfig();      
      for(Map.Entry<Integer, Integer> metersPerPixelPerZoom : wmsConfig.getMetersPerPixelPerZoomLevel().entrySet())
      {
        final int zoomLevel = metersPerPixelPerZoom.getKey();
        final int metersPerPixel = metersPerPixelPerZoom.getValue();
        final int tileWidth = getBestTileSizeInPixels(bboxWidthInMeters, metersPerPixel);
        final int tileHeight = getBestTileSizeInPixels(bboxHeightInMeters, metersPerPixel);
        
        final int matrixWidth = bboxWidthInMeters / (tileWidth * metersPerPixel);
        final int matrixHeight = bboxHeightInMeters / (tileHeight * metersPerPixel);
        
        final TileMatrixParams matrixParam = new TileMatrixParams(zoomLevel, matrixBbox, matrixWidth, matrixHeight, tileWidth, tileHeight, metersPerPixel);
        matrixParams.add(matrixParam);
        
        DwbkLog.log(Level.INFO, "TileMatrix erstellt für '%s': %n%s", layer.getName(), matrixParam);
      }
    } catch (Exception e)
    {
      throw DwbkFrameworkException.failForReason(e, "Tilematrix konnte nicht erstellt werden.");
    }
    return matrixParams;
  }  
  
  private static int getBestTileSizeInPixels(int bboxWidthInMeters, int metersPerPixel)
  {
    int maxPixels = DEFAULT_TILE_LENGTH + DEFAULT_TOLERANCE;
    // by default choose tile size as big as possible
    for(int i = maxPixels; i > (maxPixels / 2); i-=10)
    {
      int currentWidthInMeters = i * metersPerPixel;
      if(bboxWidthInMeters % currentWidthInMeters == 0)
      {
        return i;
      }
    }
    
    String msg = String.format("Cannot determine best tile length for parameters: [bbox width in meters: %s, meters per pixel: %s, max tile size: %s]", 
      bboxWidthInMeters, metersPerPixel, maxPixels);
    throw new IllegalArgumentException(msg);
  }
  
  private final int zoomLevel;
  private final ReferencedEnvelope matrixBbox;
  private final int matrixWidth;
  private final int matrixHeight;
  private final int tileWidth;
  private final int tileHeight;
  private final int metersPerPixel;

  private final int tileCount;
  private int tileWidthInMeters;
  private int tileHeightInMeters;

  
  public TileMatrixParams(int zoomLevel, ReferencedEnvelope matrixBbox, 
    int matrixWidth, int matrixHeight, int tileWidth, int tileHeight, int metersPerPixel)
  {
    this.zoomLevel = zoomLevel;
    this.matrixBbox = matrixBbox;
    this.matrixWidth = matrixWidth;
    this.matrixHeight = matrixHeight;
    this.tileWidth = tileWidth;
    this.tileHeight = tileHeight;
    this.metersPerPixel = metersPerPixel;
    
    // calculated fields
    this.tileCount = matrixWidth * matrixHeight;
    this.tileWidthInMeters = metersPerPixel * tileWidth;
    this.tileHeightInMeters = metersPerPixel * tileHeight;
  }
  
  /**
   * Returns the zoom level.
   * @return zoom level
   */
  public int getZoomLevel()
  {
    return zoomLevel;
  }
  
  /**
   * Returns the matrix bbox (in default EPSG:3857).
   * @return matrix bbox
   */
  public ReferencedEnvelope getMatrixBbox()
  {
    return matrixBbox;
  }
  
  /**
   * Returns the number tiles used for the width.
   * @return matrix width
   */
  public int getMatrixWidth()
  {
    return matrixWidth;
  }
  
  /**
   * Returns the number of tiles used for the height.
   * @return matrix height
   */
  public int getMatrixHeight()
  {
    return matrixHeight;
  }
  
  /**
   * Returns the total tile count for the matrix.
   * @return total tile count
   */
  public int getTotalTileCount()
  {
    return tileCount;
  }
  
  /**
   * Returns the tile width in pixels.
   * @return tile width
   */
  public int getTileWidthInPixels()
  {
    return tileWidth;
  }
  
  /**
   * Returns the tile height in pixels.
   * @return tile height
   */
  public int getTileHeightInPixels()
  {
    return tileHeight;
  }
  
  /**
   * Returns the resolution of a tile in meters per pixel.
   * @return resolution
   */
  public int getMetersPerPixel()
  {
    return metersPerPixel;
  }
  
  /**
   * Returns the width of a single tile in meters. <b>Always assuming we use a metric CRS!</b>.
   * @return  width of a single tile in meters.
   */
  public int getTileWidthInMeters()
  {
    return this.tileWidthInMeters;
  }
  
  /**
   * Returns the height of a single tile in meters. <b>Always assuming we use a metric CRS!</b>.
   * @return  height of a single tile in meters.
   */
  public int getTileHeightInMeters()
  {
    return this.tileHeightInMeters;
  }
  
  @Override
  public String toString()
  {
    return String.join(System.lineSeparator(), "TileMatrixParams: [",
      "Boundingbox: " + matrixBbox,
      "zoom: " + zoomLevel,
      "matrix-width: " + matrixWidth,
      "matrix-height: " + matrixHeight,
      "meters per pixel: " + metersPerPixel,
      "tile-width: " + tileWidth,
      "tile-height: " + tileHeight,
      "]");
  }

}
