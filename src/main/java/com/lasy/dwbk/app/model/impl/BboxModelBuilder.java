package com.lasy.dwbk.app.model.impl;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

import com.lasy.dwbk.app.model.IGtModelBuilder;
import com.lasy.dwbk.db.tables.IDwbkTable;
import com.lasy.dwbk.db.tables.impl.BboxTable;
import com.lasy.dwbk.db.util.DbBoolean;
import com.lasy.dwbk.util.Check;

public class BboxModelBuilder implements IGtModelBuilder<BboxModel>
{
  private static final IDwbkTable TABLE = new BboxTable();
  
  private String name;
  private String description;
  
  private int epsg;
  private String minLon;
  private String minLat;
  private String maxLon;
  private String maxLat;
  
  private boolean isMapBoundary;

  
  protected BboxModelBuilder(String name)
  {
    this.name = Check.trimmedNotEmpty(name, "name");
    this.isMapBoundary = false;
  }
  
  @Override
  public BboxModel build()
  {
    SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(getTable().getSimpleFeatureType());
    
    featureBuilder.set(BboxTable.COL_NAME, this.name);
    featureBuilder.set(BboxTable.COL_DESCRIPTION, this.description);
    
    featureBuilder.set(BboxTable.EPSG, this.epsg);
    featureBuilder.set(BboxTable.COL_MIN_LON, this.minLon);
    featureBuilder.set(BboxTable.COL_MIN_LAT, this.minLat);
    featureBuilder.set(BboxTable.COL_MAX_LON, this.maxLon);
    featureBuilder.set(BboxTable.COL_MAX_LAT, this.maxLat);
    featureBuilder.set(BboxTable.COL_IS_MAP_BOUNDARY, DbBoolean.toDbValue(this.isMapBoundary));
    
    SimpleFeature feature = featureBuilder.buildFeature(GENERATE_ID);
    
    return new BboxModel(feature);
  }
  
  /**
   * Sets the description.
   * @param description description
   * @return builder
   */
  public BboxModelBuilder withDescription(String description)
  {
    this.description = Check.trimmedNotEmpty(description, "desciption");
    return this;
  }
  
  /**
   * Sets the EPSG code.
   * @param epsg EPSG code
   * @return builder
   */
  public BboxModelBuilder withEpsg(int epsg)
  {
    this.epsg = epsg;
    return this;
  }
  
  /**
   * Sets the minimum longitude.
   * @param minLon minimum longitude
   * @return builder
   */
  public BboxModelBuilder withMinLon(String minLon)
  {
    this.minLon = Check.validCoordinate(minLon, "minLon");
    return this;
  }
  
  /**
   * Sets the minimum latitude.
   * @param minLat minimum latitude
   * @return builder
   */
  public BboxModelBuilder withMinLat(String minLat)
  {
    this.minLat = Check.validCoordinate(minLat, "minLat");
    return this;
  }
  
  /**
   * Sets the maximum longitude.
   * @param maxLon maximum longitude
   * @return builder
   */
  public BboxModelBuilder withMaxLon(String maxLon)
  {
    this.maxLon = Check.validCoordinate(maxLon, "maxLon");
    return this;
  }
  
  /**
   * Sets the maximum latitude.
   * @param maxLat maximum latitude
   * @return builder
   */
  public BboxModelBuilder withMaxLat(String maxLat)
  {
    this.maxLat = Check.validCoordinate(maxLat, "maxLat");
    return this;
  }
  
  /**
   * Sets the value for the map boundary status (app map extent).
   * @param isMapBoundary {@code true} if the bbox should be used as the map extent (in the app).
   * @return builder
   */
  public BboxModelBuilder withMapBoundary(boolean isMapBoundary)
  {
    this.isMapBoundary = isMapBoundary;
    return this;
  }
  
  @Override
  public IDwbkTable getTable()
  {
    return TABLE;
  }

}
