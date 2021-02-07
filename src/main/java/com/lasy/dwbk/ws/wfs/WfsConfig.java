package com.lasy.dwbk.ws.wfs;

import java.util.Objects;

import com.lasy.dwbk.util.IDwbkJson;

/**
 * WFS request and display configuration.
 * @author larss
 *
 */
public class WfsConfig implements IDwbkJson
{
  
  private String typeNames = null;

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
