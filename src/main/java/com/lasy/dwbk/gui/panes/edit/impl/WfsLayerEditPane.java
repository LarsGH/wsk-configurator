package com.lasy.dwbk.gui.panes.edit.impl;

import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.app.model.impl.LayerModelBuilder;
import com.lasy.dwbk.gui.panes.edit.util.AttributeInputContainer;
import com.lasy.dwbk.gui.util.AttributeInputValidator;
import com.lasy.dwbk.gui.util.PatternTextField;
import com.lasy.dwbk.util.Is;
import com.lasy.dwbk.ws.wfs.WfsConfig;

import javafx.scene.Scene;
import javafx.scene.control.TextField;

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
  
  private AttributeInputContainer<LayerModel, TextField, Integer> attrRequestEpsg;
  
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
    WfsConfig config = createWfsConfigWithConfiguredValues();
    layer.setWfsConfig(config);
  }

  @Override
  protected void doHandleServiceSpecificNewModel(LayerModelBuilder builder)
  {
    WfsConfig config = createWfsConfigWithConfiguredValues();
    builder.withWfsConfig(config);
  }
  
  private WfsConfig createWfsConfigWithConfiguredValues()
  {
    WfsConfig config = new WfsConfig();
    config.setTypeNames(attrServiceLayerName.getConfiguredValue());
    config.setRequestEpsg(attrRequestEpsg.getConfiguredValue());
    
    return config;
  }
  
  @Override
  protected void doCreateAttributeInputContainers()
  {
    super.doCreateAttributeInputContainers();
    
    this.attrRequestEpsg = createAttrRequestEpsg();
    addAttributeInputContainer(attrRequestEpsg);
  }
  
  private AttributeInputContainer<LayerModel, TextField, Integer> createAttrRequestEpsg()
  {
    return AttributeInputContainer.<LayerModel, TextField, Integer>builer("GetFeature-Request EPSG-Code")
      .withGuiElement(PatternTextField.createIntegersOnlyTextField())
      .withGuiValueInitialization((txtField, layer) -> {
        int epsg = layer != null
          ? layer.getWfsConfig().getRequestEpsg()
          : WfsConfig.DEFAULT_REQUEST_EPSG;
        txtField.setText(String.valueOf(epsg));
      })
      .withGuiElementToModelAttributeFunc(txtField -> {
        String txt = txtField.getText();
        return Is.nullOrTrimmedEmpty(txt)
          ? null
          : Integer.valueOf(txt);
      })
      .withInfoAlertMessage("Eindeutiger Schlüssel für das Koordinatensystem. "
        + "Unterstützte EPSG-Codes werden vom Service definiert. "
        + "Siehe GetCapabilities-Request.")
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      .build();
  }

}
