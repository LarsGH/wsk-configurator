package com.lasy.dwbk.db.tables;

import java.math.BigDecimal;

import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Defines the table for layer definitions.
 * @author larss
 */
public class BboxTable implements IDwbkTable
{
  public static final String TABLE_NAME = "tm_config_bbox";

  public static final String COL_NAME = "name";
  
  public static final String COL_DESCRIPTION = "description";
  
  public static final String COL_MIN_LON = "min_lon";
  public static final String COL_MIN_LAT = "min_lat";
  
  public static final String COL_MAX_LON = "max_lon";
  public static final String COL_MAX_LAT = "max_lat";
  
//  public static final String COL_LOWER_LEFT = "lower_left";
//  public static final String COL_UPPER_RIGHT= "upper_right";
  
  @Override
  public String getTableName()
  {
    return TABLE_NAME;
  }

  @Override
  public SimpleFeatureType getSimpleFeatureType()
  {
    SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
    builder.setName(TABLE_NAME);
    builder.setCRS(DefaultGeographicCRS.WGS84);
    
    builder.nillable(false);
    builder.add(COL_NAME, String.class);
    
    builder.add(COL_DESCRIPTION, String.class);
    
//    builder.add(COL_LOWER_LEFT, Point.class);
//    builder.add(COL_UPPER_RIGHT, Point.class);
    
    builder.nillable(false);
    builder.add(COL_MIN_LON, BigDecimal.class);
    
    builder.nillable(false);
    builder.add(COL_MIN_LAT, BigDecimal.class);
    
    builder.nillable(false);
    builder.add(COL_MAX_LON, BigDecimal.class);
    
    builder.nillable(false);
    builder.add(COL_MAX_LAT, BigDecimal.class);
    
    return builder.buildFeatureType();
  }

}
