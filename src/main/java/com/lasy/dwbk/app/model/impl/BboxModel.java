package com.lasy.dwbk.app.model.impl;

import org.opengis.feature.simple.SimpleFeature;

import com.lasy.dwbk.app.model.AGtModel;
import com.lasy.dwbk.db.tables.BboxTable;
import com.lasy.dwbk.db.util.DbRowAccess;
import com.lasy.dwbk.util.Check;

/**
 * Entity for a row from {@link BboxTable}.
 * @author larss
 */
public class BboxModel extends AGtModel
{
  
  /**
   * Creates a new builder.
   * @return builder
   */
  public static BboxModelBuilder builder(String name)
  {
    Check.trimmedNotEmpty(name, "name");
    return new BboxModelBuilder(name);
  }
  
  public BboxModel(SimpleFeature feature)
  {
    super(feature);
  }

  public String getMinLon()
  {
    String val = DbRowAccess.getMandatoryValue(getFeature(), BboxTable.COL_MIN_LON, String.class);
    return val;
  }

  public void setMinLon(String minLon)
  {
    this.getFeature().setAttribute(BboxTable.COL_MIN_LON, minLon);
  }

  public String getMinLat()
  {
    String val = DbRowAccess.getMandatoryValue(getFeature(), BboxTable.COL_MIN_LAT, String.class);
    return val;
  }

  public void setMinLat(String minLat)
  {
    this.getFeature().setAttribute(BboxTable.COL_MIN_LAT, minLat);
  }

  public String getMaxLon()
  {
    String val = DbRowAccess.getMandatoryValue(getFeature(), BboxTable.COL_MAX_LON, String.class);
    return val;
  }

  public void setMaxLon(String maxLon)
  {
    this.getFeature().setAttribute(BboxTable.COL_MAX_LON, maxLon);
  }

  public String getMaxLat()
  {
    String val = DbRowAccess.getMandatoryValue(getFeature(), BboxTable.COL_MAX_LAT, String.class);
    return val;
  }

  public void setMaxLat(String maxLat)
  {
    this.getFeature().setAttribute(BboxTable.COL_MAX_LON, maxLat);
  }
  
  @Override
  public String toString()
  {
    String description = getDescription().isPresent()
      ? " (" + getDescription().get() + ") "
      : "";
    
    return String.format("BBox-%s: %s%s [lowerLeft (%s | %s) | upperRight (%s | %s)]",
      getId(),
      getName(),
      description,
      getMinLon(),
      getMinLat(),
      getMaxLon(),
      getMaxLat());
  }

}
