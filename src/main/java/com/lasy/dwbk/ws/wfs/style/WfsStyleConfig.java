package com.lasy.dwbk.ws.wfs.style;

import com.lasy.dwbk.util.IDwbkJson;

/**
 * Style configuration for WFS layers.
 * 
 * @author larss
 *
 */
public class WfsStyleConfig implements IDwbkJson
{
  
  private String geomType;
  private String lineColor;
  private String fillColor;
  
  public WfsStyleConfig(String geomType, String lineColor, String fillColor)
  {
    this.geomType = geomType;
    this.lineColor = lineColor;
    this.fillColor = fillColor;
  }

  public EWfsStyleGeometry getGeomType()
  {
    return EWfsStyleGeometry.forConfiguredValue(this.geomType);
  }
  
  public void setGeomType(String geomType)
  {
    this.geomType = geomType;
  }
  
  public String getLineColor()
  {
    return lineColor;
  }
  
  public void setLineColor(String lineColor)
  {
    this.lineColor = lineColor;
  }
  
  public String getFillColor()
  {
    return fillColor;
  }
  
  public void setFillColor(String fillColor)
  {
    this.fillColor = fillColor;
  }
  
  @Override
  public String toString()
  {
    return asJson();
  }

}
