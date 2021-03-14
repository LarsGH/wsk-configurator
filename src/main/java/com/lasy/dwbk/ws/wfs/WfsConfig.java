package com.lasy.dwbk.ws.wfs;

import java.util.Objects;

import com.lasy.dwbk.util.BboxUtil;
import com.lasy.dwbk.util.IDwbkJson;
import com.lasy.dwbk.ws.wfs.style.WfsStyleConfig;

/**
 * WFS request and display configuration.
 * @author larss
 *
 */
public class WfsConfig implements IDwbkJson
{
  /** Default EPSG for WMS tile requests. */
  public static final int DEFAULT_REQUEST_EPSG = BboxUtil.EPSG_3857;
  
  private String typeNames = null;
  private int requestEpsg = DEFAULT_REQUEST_EPSG;
  private WfsStyleConfig styleConfig = null;

  /**
   * Returns the layers to display on map. Value is a comma-separated list of layer names. (mandatory request parameter).
   * @return layers
   */
  public String getTypeNames()
  {
    return this.typeNames;
  }
  
  /**
   * Sets the layer (request parameter).
   * @param layer
   */
  public void setTypeNames(String layer)
  {
    this.typeNames = layer;
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
  
  /**
   * Returns the style config.
   * @return style config
   */
  public WfsStyleConfig getStyleConfig()
  {
    return styleConfig;
  }

  /**
   * Sets the style config.
   * @param styleConfig style config
   */
  public void setStyleConfig(WfsStyleConfig styleConfig)
  {
    this.styleConfig = styleConfig;
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
    WfsConfig other = (WfsConfig) obj;
    return Objects.equals(typeNames, other.typeNames);
  }
  
  @Override
  public String toString()
  {
    return asJson();
  }
  
}
