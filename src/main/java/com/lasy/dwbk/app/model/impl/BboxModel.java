package com.lasy.dwbk.app.model.impl;

import org.opengis.feature.simple.SimpleFeature;

import com.lasy.dwbk.app.model.AGtModel;
import com.lasy.dwbk.db.tables.impl.BboxTable;
import com.lasy.dwbk.db.util.DbBoolean;
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
  
  public int getEpsg()
  {
    Integer val = DbRowAccess.getMandatoryValue(getFeature(), BboxTable.EPSG, Integer.class);
    return val;
  }

  public void setEpsg(int epsg)
  {
    this.getFeature().setAttribute(BboxTable.EPSG, epsg);
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
    this.getFeature().setAttribute(BboxTable.COL_MAX_LAT, maxLat);
  }
  
  public boolean isMapBoundary()
  {
    String isMapBoundary = DbRowAccess.getMandatoryValue(getFeature(), BboxTable.COL_IS_MAP_BOUNDARY, String.class);
    return DbBoolean.fromDbValue(isMapBoundary);
  }

  public void setMapBoundary(boolean isMapBoundary)
  {
    this.getFeature().setAttribute(BboxTable.COL_IS_MAP_BOUNDARY, DbBoolean.toDbValue(isMapBoundary));
  }
  
  private static final String MODEL_NAME = "Boundingbox";
  
  @Override
  protected String getModelName()
  {
    return MODEL_NAME;
  }

}
