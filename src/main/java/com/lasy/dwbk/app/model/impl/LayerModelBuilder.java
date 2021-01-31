package com.lasy.dwbk.app.model.impl;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

import com.lasy.dwbk.app.model.IGtModelBuilder;
import com.lasy.dwbk.db.tables.IDwbkTable;
import com.lasy.dwbk.db.tables.impl.LayerTable;
import com.lasy.dwbk.util.Check;

public class LayerModelBuilder implements IGtModelBuilder<LayerModel>
{
  private static final IDwbkTable TABLE = new LayerTable();
  
  private String name;
  private String description;
  private String uri;
  private boolean storeLocal;
  private String metersPerPixel;
  private boolean isVisible;
  private Integer bboxId;
  private String user;
  private String pw;

  protected LayerModelBuilder(String name)
  {
    this.name = Check.trimmedNotEmpty(name, "name");
    this.storeLocal = false;
    this.isVisible = false;
  }
  
  @Override
  public LayerModel build()
  {
    SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(getTable().getSimpleFeatureType());
    SimpleFeature feature = featureBuilder.buildFeature(GENERATE_ID);
    LayerModel layer = new LayerModel(feature);
    
    layer.setName(this.name);
    layer.setDescription(this.description);
    layer.setUri(this.uri);
    layer.setStoreLocal(this.storeLocal);
    layer.setMetersPerPixelText(this.metersPerPixel);
    layer.setVisible(this.isVisible);
    layer.setBboxId(this.bboxId);
    layer.setUser(this.user);
    layer.setPw(this.pw);
    
    return layer;
  }
  
  /**
   * Sets the description
   * @param description description
   * @return builder
   */
  public LayerModelBuilder withDescription(String description)
  {
    this.description = description;
    return this;
  }
  
  /**
   * Sets the URI.
   * @param uri URI
   * @return builder
   */
  public LayerModelBuilder withUri(String uri)
  {
    this.uri = Check.trimmedNotEmpty(uri, "uri");
    return this;
  }
  
  /**
   * Sets the value for store local.
   * @param storeLocal {@code true} if data should be stored in the GPKG.
   * @return builder
   */
  public LayerModelBuilder withStoreLocal(boolean storeLocal)
  {
    this.storeLocal = storeLocal;
    return this;
  }
  
  /**
   * Sets the value for valid meters per pixels. Values must be separated by semicolon (';')!
   * @param metersPerPixel the meters per pixel (used to build the tile matrix pyramid).
   * @return builder
   */
  public LayerModelBuilder withMetersPerPixel(String metersPerPixel)
  {
    this.metersPerPixel = Check.notNull(metersPerPixel, "metersPerPixel");
    return this;
  }
  
  /**
   * Sets the value for the default visibility enabled status.
   * @param isVisible {@code true} if layer should be visible by default.
   * @return builder
   */
  public LayerModelBuilder withDefaultVisible(boolean isVisible)
  {
    this.isVisible = isVisible;
    return this;
  }
  
  /**
   * Sets boundingbox reference ID.
   * @param bboxId ID of the boundingbox entry to reference
   * @return builder
   */
  public LayerModelBuilder withBboxId(Integer bboxId)
  {
    this.bboxId = bboxId;
    return this;
  }
  
  /**
   * Sets the username.
   * @param user username (for logins)
   * @return builder
   */
  public LayerModelBuilder withUser(String user)
  {
    this.user = user;
    return this;
  }
  
  /**
   * Sets the password.
   * @param pw password (for logins)
   * @return builder
   */
  public LayerModelBuilder withPassword(String pw)
  {
    this.pw = pw;
    return this;
  }
  
  @Override
  public IDwbkTable getTable()
  {
    return TABLE;
  }

}
