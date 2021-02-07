package com.lasy.dwbk.gui.panes.edit.impl;

import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.app.model.impl.LayerModelBuilder;
import com.lasy.dwbk.ws.wfs.WfsConfig;

import javafx.scene.Scene;

public class WfsLayerEditPane extends ALayerEditPane
{

  /**
   * Creates the fully initialized layer pane.
   * @param mainScene main scene
   * @param layer the layer (may be {@code null} to create new layers).
   * @return fully initialized layer pane
   */
  public static WfsLayerEditPane create(Scene mainScene, LayerModel layer)
  {
    return createInitializedPane(new WfsLayerEditPane(mainScene, layer));
  }
  
  /**
   * Layer create / edit GUI.
   * @param mainScene main scene
   * @param layer the layer (may be {@code null} to create new layers).
   */
  private WfsLayerEditPane(Scene mainScene, LayerModel layer)
  {
    super(mainScene, "WFS Layer verwalten", layer);
  }
  
  @Override
  protected void doHandleServiceSpecificUpdateModel(LayerModel layer)
  {
    WfsConfig config = createWmsConfigWithConfiguredValues();
    layer.setWfsConfig(config);
  }

  @Override
  protected void doHandleServiceSpecificNewModel(LayerModelBuilder builder)
  {
    WfsConfig config = createWmsConfigWithConfiguredValues();
    builder.withWfsConfig(config);
  }
  
  private WfsConfig createWmsConfigWithConfiguredValues()
  {
    WfsConfig config = new WfsConfig();
    config.setTypeNames(attrServiceLayerName.getConfiguredValue());
    
    return config;
  }
  
  @Override
  protected void doCreateAttributeInputContainers()
  {
    super.doCreateAttributeInputContainers();
  }

}
