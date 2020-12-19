package com.lasy.dwbk.app.model.impl;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

import com.lasy.dwbk.app.model.IGtModelBuilder;
import com.lasy.dwbk.db.tables.IDwbkTable;
import com.lasy.dwbk.db.tables.LayerTable;
import com.lasy.dwbk.db.util.DbBoolean;
import com.lasy.dwbk.util.Check;

public class LayerModelBuilder implements IGtModelBuilder<LayerModel>
{
  private static final IDwbkTable TABLE = new LayerTable();
  
  private String name;
  private String description;
  private String uri;
  private boolean storeLocal;
  private boolean isSaved;
  private Integer bboxId;
  private String user;
  private String pw;

  protected LayerModelBuilder(String name)
  {
    this.name = Check.trimmedNotEmpty(name, "name");
    this.storeLocal = false;
    this.isSaved = false;
  }
  
  @Override
  public LayerModel build()
  {
    SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(getTable().getSimpleFeatureType());
    
    featureBuilder.set(LayerTable.COL_NAME, this.name);
    featureBuilder.set(LayerTable.COL_DESCRIPTION, this.description);
    featureBuilder.set(LayerTable.COL_URI, this.uri);
    featureBuilder.set(LayerTable.COL_STORE_LOCAL, DbBoolean.toDbValue(this.storeLocal));
    featureBuilder.set(LayerTable.COL_IS_SAVED, DbBoolean.toDbValue(this.isSaved));
    featureBuilder.set(LayerTable.COL_BBOX_ID, this.bboxId);
    featureBuilder.set(LayerTable.COL_USER, this.user);
    featureBuilder.set(LayerTable.COL_PW, this.pw);
    
    SimpleFeature feature = featureBuilder.buildFeature(GENERATE_ID);
    
    return new LayerModel(feature);
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
   * Sets the value for the saved status (necessary if layer is stored locally).
   * @param isSaved {@code true} if layer is saved.
   * @return builder
   */
  public LayerModelBuilder withIsSaved(boolean isSaved)
  {
    this.isSaved = isSaved;
    return this;
  }
  
  /**
   * Sets boundingbox reference ID.
   * @param bboxId ID of the boundingbox entry to reference
   * @return builder
   */
  public LayerModelBuilder withBboxId(int bboxId)
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
