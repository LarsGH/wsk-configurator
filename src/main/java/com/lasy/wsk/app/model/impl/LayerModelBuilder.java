package com.lasy.wsk.app.model.impl;

import java.util.Objects;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

import com.lasy.wsk.app.model.IGtModelBuilder;
import com.lasy.wsk.db.tables.IWskTable;
import com.lasy.wsk.db.tables.impl.LayerTable;
import com.lasy.wsk.util.Check;
import com.lasy.wsk.ws.EWebServiceType;
import com.lasy.wsk.ws.wfs.WfsConfig;
import com.lasy.wsk.ws.wms.WmsConfig;

public class LayerModelBuilder implements IGtModelBuilder<LayerModel>
{
  private static final IWskTable TABLE = new LayerTable();

  private String name;
  private String description;
  private String request;
  private boolean storeLocal;
  private boolean isVisible;
  private Integer bboxId;
  private String user;
  private String pw;

  private WmsConfig wmsConfig;
  private WfsConfig wfsConfig;

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
    layer.setRequest(this.request);
    layer.setStoreLocal(this.storeLocal);
    layer.setVisible(this.isVisible);
    layer.setBboxId(this.bboxId);
    layer.setUser(this.user);
    layer.setPw(this.pw);

    EWebServiceType webServiceType = layer.getWebServiceType();
    if (Objects.equals(EWebServiceType.WMS, webServiceType))
    {
      layer.setWmsConfig(this.wmsConfig);
    }
    else if (Objects.equals(EWebServiceType.WFS, webServiceType))
    {
      layer.setWfsConfig(this.wfsConfig); 
    }
    else
    {
      String msg = String.format("No configuration defined for service type: '%s'", webServiceType);
      throw new IllegalStateException(msg);
    }

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
   * Sets the request.
   * @param request GET_CAPABILITIES request
   * @return builder
   */
  public LayerModelBuilder withRequest(String request)
  {
    this.request = Check.trimmedNotEmpty(request, "request");
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

  /**
   * Sets the WMS configuration.
   * @param config WMS configuration
   * @return builder
   */
  public LayerModelBuilder withWmsConfig(WmsConfig config)
  {
    this.wmsConfig = config;
    return this;
  }
  
  /**
   * Sets the WFS configuration.
   * @param config WFS configuration
   * @return builder
   */
  public LayerModelBuilder withWfsConfig(WfsConfig config)
  {
    this.wfsConfig = config;
    return this;
  }

  @Override
  public IWskTable getTable()
  {
    return TABLE;
  }

}
