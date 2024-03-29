package com.lasy.wsk.gui.panes.edit.impl;

import java.util.Optional;

import com.lasy.wsk.app.model.impl.LayerModel;
import com.lasy.wsk.app.model.impl.LayerModelBuilder;
import com.lasy.wsk.gui.panes.edit.util.AttributeInputContainer;
import com.lasy.wsk.gui.util.AttributeInputValidator;
import com.lasy.wsk.gui.util.PatternTextField;
import com.lasy.wsk.util.Is;
import com.lasy.wsk.ws.wms.WmsConfig;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class WmsLayerEditPane extends ALayerEditPane
{

  /**
   * Creates the fully initialized layer pane.
   * @param mainScene main scene
   * @param layer the layer (may be {@code null} to create new layers).
   * @return fully initialized layer pane
   */
  public static WmsLayerEditPane create(Scene mainScene, LayerModel layer)
  {
    return createInitializedPane(new WmsLayerEditPane(mainScene, layer));
  }
  
  private AttributeInputContainer<LayerModel, TextField, String> attrMetersPerPixel;
  private AttributeInputContainer<LayerModel, TextField, String> attrFormat;
  private AttributeInputContainer<LayerModel, TextField, Integer> attrRequestEpsg;
  private AttributeInputContainer<LayerModel, TextField, String> attrStyles;
  private AttributeInputContainer<LayerModel, CheckBox, Boolean> attrTransparency;
  
  /**
   * Layer create / edit GUI.
   * @param mainScene main scene
   * @param layer the layer (may be {@code null} to create new layers).
   */
  private WmsLayerEditPane(Scene mainScene, LayerModel layer)
  {
    // WMS layer administration
    super(mainScene, "WMS Layer verwalten", layer);
  }
  
  @Override
  protected void doHandleServiceSpecificUpdateModel(LayerModel layer)
  {
    WmsConfig config = createWmsConfigWithConfiguredValues();
    layer.setWmsConfig(config);
  }

  @Override
  protected void doHandleServiceSpecificNewModel(LayerModelBuilder builder)
  {
    WmsConfig config = createWmsConfigWithConfiguredValues();
    builder.withWmsConfig(config);
  }
  
  private WmsConfig createWmsConfigWithConfiguredValues()
  {
    WmsConfig config = new WmsConfig();
    config.setLayer(attrServiceLayerName.getConfiguredValue());
    config.setMetersPerPixel(attrMetersPerPixel.getConfiguredValue());
    config.setFormat(attrFormat.getConfiguredValue());
    config.setRequestEpsg(attrRequestEpsg.getConfiguredValue());
    config.setStyles(attrStyles.getConfiguredValue());
    config.setTransparent(attrTransparency.getConfiguredValue());
    
    return config;
  }
  
  @Override
  protected void doCreateAttributeInputContainers()
  {
    super.doCreateAttributeInputContainers();
    
    this.attrStyles = createAttrStyles();
    addAttributeInputContainer(attrStyles);
    
    this.attrFormat = createAttrFormat();
    addAttributeInputContainer(attrFormat);
    
    this.attrRequestEpsg = createAttrRequestEpsg();
    addAttributeInputContainer(attrRequestEpsg);
    
    this.attrTransparency = createAttrTransparency();
    addAttributeInputContainer(attrTransparency);
    
    this.attrMetersPerPixel = createAttrMetersPerPixel();
    addAttributeInputContainer(attrMetersPerPixel);
  }
  
  private AttributeInputContainer<LayerModel, TextField, String> createAttrMetersPerPixel()
  {
    // resolution per zoom level (when saved locally)
    return AttributeInputContainer.<LayerModel, TextField, String>builer("Auflösung pro Zoomstufe (bei lokaler Speicherung)")
      .withGuiElement(PatternTextField.createNumbersSeparatedBySemicolonsTextField())
      .withGuiValueInitializationIfModelNotNull((txtField, layer) -> {
        txtField.setText(layer.getWmsConfig().getMetersPerPixelAsText());
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      // resolution per pixel in meters. may have multiple values delimited by ';'
      .withInfoAlertMessage("Die Auflösung pro Pixel in Meter. Kann mehrere Werte getrennt durch ';' enthalten.")
      .withDependingContainerValidator(attrStoreLocal, (storeLocalObj, metersPerPixel) -> {
        Boolean storeLocal = storeLocalObj == null
          ? false
          : (Boolean) storeLocalObj;
        if (storeLocal && Is.nullOrTrimmedEmpty(metersPerPixel))
        {
          // at least one resolution must be provieded if the layer is saved locally
          return Optional.of("Es muss mindestens eine Auflösung angegeben werden, wenn der Layer lokal gespeichert wird!");
        }
        return Optional.empty();
      })
      .withInputValidationError(AttributeInputValidator.createDivisorInputFunction(1000))
      .build();
  }
  
  private AttributeInputContainer<LayerModel, TextField, String> createAttrFormat()
  {
    // image format
    return AttributeInputContainer.<LayerModel, TextField, String>builer("GetMap-Request Bild-Format")
      .withGuiElement(PatternTextField.createAcceptAllTextField())
      .withGuiValueInitialization((txtField, layer) -> {
        String format = layer != null
          ? layer.getWmsConfig().getFormat()
          : WmsConfig.DEFAULT_FORMAT;
        txtField.setText(format);
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      // format for the image tiles. supported formats are defined by the service. see GetCapabilities-request.
      .withInfoAlertMessage("Das Bild-Format für die Bild-Kacheln. Unterstützte Formate werden vom Service definiert. "
        + "Siehe GetCapabilities-Request.")
      .build();
  }
  
  private AttributeInputContainer<LayerModel, TextField, String> createAttrStyles()
  {
    return AttributeInputContainer.<LayerModel, TextField, String>builer("GetMap-Request Layer-Style")
      .withGuiElement(PatternTextField.createAcceptAllTextField())
      .withGuiValueInitializationIfModelNotNull((txtField, layer) -> {
        txtField.setText(layer.getWmsConfig().getStyles());
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      // style to use. supported formats are defined by the service. see GetCapabilities-request.
      .withInfoAlertMessage("Der zu verwendende Style. Unterstützte Styles werden vom Service definiert. "
        + "Siehe GetCapabilities-Request.")
      .build();
  }
  
  private AttributeInputContainer<LayerModel, TextField, Integer> createAttrRequestEpsg()
  {
    return AttributeInputContainer.<LayerModel, TextField, Integer>builer("Service EPSG-Code")
      .withGuiElement(PatternTextField.createIntegersOnlyTextField())
      .withGuiValueInitialization((txtField, layer) -> {
        int epsg = layer != null
          ? layer.getWmsConfig().getRequestEpsg()
          : WmsConfig.DEFAULT_REQUEST_EPSG;
        txtField.setText(String.valueOf(epsg));
      })
      .withGuiElementToModelAttributeFunc(txtField -> {
        String txt = txtField.getText();
        return Is.nullOrTrimmedEmpty(txt)
          ? null
          : Integer.valueOf(txt);
      })
      // unique key for the coordinate system. supported formats are defined by the service. see GetCapabilities-request.
      .withInfoAlertMessage("Eindeutiger Schlüssel für das Koordinatensystem. "
        + "Unterstützte EPSG-Codes werden vom Service definiert. "
        + "Siehe GetCapabilities-Request.")
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      .build();
  }
  
  private AttributeInputContainer<LayerModel, CheckBox, Boolean> createAttrTransparency()
  {
    return AttributeInputContainer.<LayerModel, CheckBox, Boolean>builer("GetMap-Request Transparenz")
      // use transparency?
      .withGuiElement(new CheckBox("Transparenz verwenden?"))
      .withGuiValueInitialization((cb, layer) -> {
        boolean transparent = layer != null
          ? layer.getWmsConfig().isTransparent()
          : true;
        cb.setSelected(transparent);
      })
      .withGuiElementToModelAttributeFunc(CheckBox::isSelected)
      // if active, transparency is used for image tiles. transparency is not supported by every service and depends on the image format!
      .withInfoAlertMessage("Wenn aktiviert, werden Bild-Kacheln transparent angefragt. "
        + "Transparenz wird nicht von jedem Service angeboten und ist vom Bild-Format abhängig!")
      .build();
  }

}
