package com.lasy.wsk.ws.wfs.style;

public enum EWfsStyleGeometry {
  POINT("Punkt"),
  LINE("Linie"),
  POLYGON("Polygon");
  
  private final String label;
  
  private EWfsStyleGeometry(String lbl)
  {
    this.label = lbl;
  }
  
  /**
   * Returns the label.
   * @return label
   */
  public String getLabel()
  {
    return this.label;
  }
  
  /**
   * Returns the style geometry for a configured value (ignoring case).
   * @return style geometry
   */
  public static EWfsStyleGeometry forConfiguredValue(String configuredValue)
  {
    try {
      return EWfsStyleGeometry.valueOf(configuredValue.toUpperCase());
    }
    catch (Exception e) {
      // Value was null or not supported!
      return null;
    }
  }
  
}
