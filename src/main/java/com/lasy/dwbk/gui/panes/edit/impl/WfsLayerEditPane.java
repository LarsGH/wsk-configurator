package com.lasy.dwbk.gui.panes.edit.impl;

import java.util.Optional;

import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.app.model.impl.LayerModelBuilder;
import com.lasy.dwbk.gui.panes.edit.util.AttributeInputContainer;
import com.lasy.dwbk.gui.panes.edit.util.AttributeInputContainerBuilder;
import com.lasy.dwbk.gui.panes.edit.util.WfsStyleGeometryComboBox;
import com.lasy.dwbk.gui.util.AttributeInputValidator;
import com.lasy.dwbk.gui.util.PatternTextField;
import com.lasy.dwbk.util.Is;
import com.lasy.dwbk.ws.wfs.WfsConfig;
import com.lasy.dwbk.ws.wfs.style.WfsStyleConfig;

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
  private AttributeInputContainer<LayerModel, WfsStyleGeometryComboBox, String> attrStyleGeometry;
  private AttributeInputContainer<LayerModel, TextField, String> attrStyleLineColor;
  private AttributeInputContainer<LayerModel, TextField, String> attrStyleFillColor;
  
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
    
    String configuredStyleGeom = attrStyleGeometry.getConfiguredValue();
    if(!Is.nullOrTrimmedEmpty(configuredStyleGeom))
    {
      WfsStyleConfig wfsStyleConfig = new WfsStyleConfig(
        configuredStyleGeom, 
        attrStyleLineColor.getConfiguredValue(), 
        attrStyleFillColor.getConfiguredValue());
      config.setStyleConfig(wfsStyleConfig);
    }
    
    return config;
  }
  
  @Override
  protected void doCreateAttributeInputContainers()
  {
    super.doCreateAttributeInputContainers();
    
    this.attrRequestEpsg = createAttrRequestEpsg();
    addAttributeInputContainer(attrRequestEpsg);
    
    this.attrStyleGeometry = createAttrStyleGeometry();
    addAttributeInputContainer(attrStyleGeometry);
    
    this.attrStyleLineColor = createAttrStyleLineColor();
    addAttributeInputContainer(attrStyleLineColor);
    
    this.attrStyleFillColor = createAttrStyleFillColor();
    addAttributeInputContainer(attrStyleFillColor);
  }
  
  private AttributeInputContainer<LayerModel, WfsStyleGeometryComboBox, String> createAttrStyleGeometry()
  {
    return AttributeInputContainer.<LayerModel, WfsStyleGeometryComboBox, String>builer("Layer Geometrie (Style)")
      .withGuiElement(new WfsStyleGeometryComboBox())
      .withGuiValueInitializationIfModelNotNull((comboBox, layer) -> {
        getWfsStyleConfig(layer).ifPresent(styleConfig -> {          
          comboBox.setSelectedWfsStyleGeom(styleConfig.getGeomType());
        });
      })
      .withGuiElementToModelAttributeFunc(WfsStyleGeometryComboBox::getSelectedWfsStyleGeomConfigValue)
      .withInfoAlertMessage("Gibt die Geometrie des Layers an. Linien können keine Füllung enthalten. "
        + "Polygone und Punkte (Darstellung als Kreise) werden mit einer Linie umrandet und können farblich gefüllt werden.")
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      .build();
  }
  
  private Optional<WfsStyleConfig> getWfsStyleConfig(LayerModel layer)
  {
    WfsConfig wfsConfig = layer.getWfsConfig();
    if(wfsConfig != null)
    {
      WfsStyleConfig styleConfig = wfsConfig.getStyleConfig();
      return Optional.ofNullable(styleConfig);
    }
    return Optional.empty();
  }
  
  private AttributeInputContainer<LayerModel, TextField, String> createAttrStyleLineColor()
  {    
    return createRgbaAttribute("Linien-Farbe (RGBA)",
      createRgbaInfo("Farbe der Linie. Polygone und Punkte (Darstellung als Kreis) werden jeweils von einer Linie umrandet."))
      .withGuiValueInitializationIfModelNotNull((txtField, layer) -> {
        getWfsStyleConfig(layer).ifPresent(styleConfig -> {
          txtField.setText(styleConfig.getLineColor());
        });
      })
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      .build();
  }
  
  private AttributeInputContainer<LayerModel, TextField, String> createAttrStyleFillColor()
  {    
    return createRgbaAttribute("Füll-Farbe (RGBA)",
      createRgbaInfo("Füll-Farbe für die Geometrie. Default transparent. "
        + "Polygone und Punkte (Darstellung als Kreis) werden jeweils von einer Linie umrandet. "))
      .withGuiValueInitializationIfModelNotNull((txtField, layer) -> {
        getWfsStyleConfig(layer).ifPresent(styleConfig -> {
          txtField.setText(styleConfig.getFillColor());
        });
      })
      .build();
  }
  
  private String createRgbaInfo(String msg)
  {
    return String.join(System.lineSeparator(), msg, 
      "Die RGBA-Werte werden mit Semikolons getrennt angegeben. "
      + "Angabe Rot, Grün und Blau: 0-255, Alpha: 0-100 (0 -> transparent)");
  }
  
  private AttributeInputContainerBuilder<LayerModel, TextField, String> createRgbaAttribute(String label, String infoMsg)
  {
    return AttributeInputContainer.<LayerModel, TextField, String>builer(label)
      .withGuiElement(PatternTextField.createAcceptAllTextField())
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .withInputValidationError(AttributeInputValidator.createRgbaValidationFunction())
      .withInfoAlertMessage(infoMsg);
  }

  private AttributeInputContainer<LayerModel, TextField, Integer> createAttrRequestEpsg()
  {
    return AttributeInputContainer.<LayerModel, TextField, Integer>builer("Service EPSG-Code")
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
