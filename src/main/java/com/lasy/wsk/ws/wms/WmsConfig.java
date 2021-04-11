package com.lasy.wsk.ws.wms;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.lasy.wsk.util.BboxUtil;
import com.lasy.wsk.util.Check;
import com.lasy.wsk.util.IWskJson;

/**
 * WMS request and display configuration.
 * @author larss
 *
 */
public class WmsConfig implements IWskJson
{
  /** Default format for WMS tile requests. */
  public static final String DEFAULT_FORMAT = "image/png";
  
  /** Default EPSG for WMS tile requests. */
  public static final int DEFAULT_REQUEST_EPSG = BboxUtil.EPSG_3857;
  
  /** Default transparency flag for WMS tile requests. */
  public static final boolean DEFAULT_TRANSPARENCY = true;
  
  private String layer = null;
  private String styles = null;
  private String format = DEFAULT_FORMAT;
  private boolean isTransparent = DEFAULT_TRANSPARENCY;
  private int[] metersPerPixel = null;
  private int requestEpsg = DEFAULT_REQUEST_EPSG;

  /**
   * Returns the layers to display on map. Value is a comma-separated list of layer names. (mandatory request parameter).
   * @return layers
   */
  public String getLayer()
  {
    return this.layer;
  }
  
  /**
   * Sets the layer (request parameter).
   * @param layer
   */
  public void setLayer(String layer)
  {
    this.layer = layer;
  }

  /**
   * Returns the styles in which layers are rendered (mandatory request parameter).
   * Value is a comma-separated list of style names, or empty if default styling is required.
   * Style names may be empty in the list, to use default layer styling. (mandatory)
   * @return styles
   */
  public String getStyles()
  {
    return this.styles;
  }
  
  /**
   * Sets the styles.
   * @param styles
   */
  public void setStyles(String styles)
  {
    this.styles = styles;
  }

  /**
   * Returns the format for the map output (mandatory request parameter).
   * See WMS output formats for supported values: 
   * https://docs.geoserver.org/stable/en/user/services/wms/outputformats.html#wms-output-formats
   * @return format
   */
  public String getFormat()
  {
    return this.format ;
  }
  
  /**
   * Sets the format.
   * @param format
   */
  public void setFormat(String format)
  {
    this.format = format;
  }

  /**
   * Returns whether the map background should be transparent. Default is {@code true}.
   * @return {@code true} if transparency should be used.
   */
  public boolean isTransparent()
  {
    return this.isTransparent;
  }
  
  /**
   * Sets the transparency.
   * @param isTransparent
   */
  public void setTransparent(boolean isTransparent)
  {
    this.isTransparent = isTransparent;
  }
  
  /**
   * Returns the meters per pixel. Values are automatically sorted increasing.
   * <b>Value indices represent the zoom level!</b>
   * @return meters per pixel
   */
  public int[] getMetersPerPixel()
  {
    return this.metersPerPixel;
  }
  
  /**
   * Returns the meters per pixel values as semicolon separated string.
   * @return meters per pixel values as semicolon separated string
   */
  public String getMetersPerPixelAsText()
  {
    int[] metersPerPixel = getMetersPerPixel();
    if(metersPerPixel != null)
    {
      String str = IntStream.of(metersPerPixel)
        .mapToObj(val -> String.valueOf(val))
        .collect(Collectors.joining(";"));
      return str;
    }
    return null;
  }
  
  /**
   * Sets the meters per pixel value.
   * @param metersPerPixel meters per pixel as semicolon separated String
   */
  public void setMetersPerPixel(String metersPerPixel)
  {
    this.metersPerPixel = Check.numbersSeparatedBySemicolons(metersPerPixel, "metersPerPixel");
  }
  
  /**
   * Returns a map that links the zoom level to the meters per pixel value for that zoom.
   * @return Map: zoom level -> meters per pixel
   */
  public Map<Integer, Integer> getMetersPerPixelPerZoomLevel()
  {
    int[] metersPerPixel = getMetersPerPixel();
    if(metersPerPixel == null)
    {
      return Collections.emptyMap();
    }
    
    int numVals = metersPerPixel.length;
    Map<Integer, Integer> metersPerPixelPerZoomLevel = new LinkedHashMap<>(numVals);
    for(int zoom = 0; zoom < numVals; zoom++)
    {
      int idx = numVals - (zoom+1);
      int metersPerPixelVal = metersPerPixel[idx];
      metersPerPixelPerZoomLevel.put(zoom, metersPerPixelVal);
    }
    
    return metersPerPixelPerZoomLevel;
  }
  
  /**
   * Returns the request EPSG (default 3857).
   * @return request EPSG
   */
  public int getRequestEpsg()
  {
    return this.requestEpsg;
  }
  
  /**
   * Sets the request EPSG code.
   * @param epsg
   */
  public void setRequestEpsg(int epsg)
  {
    this.requestEpsg = epsg;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((format == null) ? 0 : format.hashCode());
    result = prime * result + (isTransparent ? 1231 : 1237);
    result = prime * result + ((layer == null) ? 0 : layer.hashCode());
    result = prime * result + Arrays.hashCode(metersPerPixel);
    result = prime * result + requestEpsg;
    result = prime * result + ((styles == null) ? 0 : styles.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    WmsConfig other = (WmsConfig) obj;
    return Objects.equals(format, other.format)
      && isTransparent == other.isTransparent
      && Objects.equals(layer, other.layer)
      && Arrays.equals(metersPerPixel, other.metersPerPixel)
      && requestEpsg == other.requestEpsg
      && Objects.equals(styles, other.styles);
  }

  @Override
  public String toString()
  {
    return asJson();
  }
  
}
