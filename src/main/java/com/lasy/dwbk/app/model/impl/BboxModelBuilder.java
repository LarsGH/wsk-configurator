package com.lasy.dwbk.app.model.impl;

import java.math.BigDecimal;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.lasy.dwbk.app.model.IGtModelBuilder;
import com.lasy.dwbk.db.tables.BboxTable;
import com.lasy.dwbk.db.tables.IDwbkTable;
import com.lasy.dwbk.db.util.DwbkBigDecimal;

public class BboxModelBuilder implements IGtModelBuilder<BboxModel>
{
  private static final IDwbkTable TABLE = new BboxTable();
  
  private String name;
  private String description;
  private BigDecimal minLon;
  private BigDecimal minLat;
  private BigDecimal maxLon;
  private BigDecimal maxLat;
  
//  private Point lowerLeft;
//  private Point upperRight;

  protected BboxModelBuilder(String name)
  {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
    this.name = name;
  }
  
  @Override
  public BboxModel build()
  {
    SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(getTable().getSimpleFeatureType());
    
    featureBuilder.set(BboxTable.COL_NAME, this.name);
    featureBuilder.set(BboxTable.COL_DESCRIPTION, this.description);
//    featureBuilder.set(BboxTable.COL_LOWER_LEFT, this.lowerLeft);
//    featureBuilder.set(BboxTable.COL_UPPER_RIGHT, this.upperRight);
    
    featureBuilder.set(BboxTable.COL_MIN_LON, this.minLon);
    featureBuilder.set(BboxTable.COL_MIN_LAT, this.minLat);
    featureBuilder.set(BboxTable.COL_MAX_LON, this.maxLon);
    featureBuilder.set(BboxTable.COL_MAX_LAT, this.maxLat);
    
    SimpleFeature feature = featureBuilder.buildFeature(GENERATE_ID);
    
    return new BboxModel(feature);
  }
  
  /**
   * Sets the description
   * @param description description
   * @return builder
   */
  public BboxModelBuilder withDescription(String description)
  {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(description));
    this.description = description;
    return this;
  }
  
  /**
   * Sets the minimum longitude.
   * @param minLon minimum longitude
   * @return builder
   */
  public BboxModelBuilder withMinLon(String minLon)
  {
    this.minLon = DwbkBigDecimal.create(minLon);
    return this;
  }
  
  /**
   * Sets the minimum latitude.
   * @param minLat minimum latitude
   * @return builder
   */
  public BboxModelBuilder withMinLat(String minLat)
  {
    this.minLat = DwbkBigDecimal.create(minLat);
    return this;
  }
  
  /**
   * Sets the maximum longitude.
   * @param maxLon maximum longitude
   * @return builder
   */
  public BboxModelBuilder withMaxLon(String maxLon)
  {
    this.maxLon = DwbkBigDecimal.create(maxLon);
    return this;
  }
  
  /**
   * Sets the maximum latitude.
   * @param maxLat maximum latitude
   * @return builder
   */
  public BboxModelBuilder withMaxLat(String maxLat)
  {
    this.maxLat = DwbkBigDecimal.create(maxLat);
    return this;
  }
  
//  /**
//   * Sets the lower left point.
//   * @param lon longitude
//   * @param lat latitude
//   * @return builder
//   */
//  public BboxModelBuilder withLowerLeft(double lon, double lat)
//  {
//    this.lowerLeft = newPoint(lon, lat);
//    return this;
//  }
//  
//  /**
//   * Sets the upper right point.
//   * @param lon longitude
//   * @param lat latitude
//   * @return builder
//   */
//  public BboxModelBuilder withUpperRight(double lon, double lat)
//  {
//    this.upperRight = newPoint(lon, lat);
//    return this;
//  }
//
//  private Point newPoint(double lon, double lat)
//  {
//    Coordinate c = new Coordinate(lon, lat);
//    Point p = new GeometryFactory()
//      .createPoint(c);
//    return p;
//  }
  
  @Override
  public IDwbkTable getTable()
  {
    return TABLE;
  }

}
