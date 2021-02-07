package com.lasy.dwbk.gui.panes.edit.impl;

import java.util.function.BiConsumer;

import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.model.impl.BboxModel;
import com.lasy.dwbk.app.model.impl.BboxModelBuilder;
import com.lasy.dwbk.app.service.impl.BboxCrudService;
import com.lasy.dwbk.gui.panes.edit.AModelEditPane;
import com.lasy.dwbk.gui.panes.edit.util.AttributeInputContainer;
import com.lasy.dwbk.gui.panes.overview.impl.BboxOverviewPane;
import com.lasy.dwbk.gui.util.AttributeInputValidator;
import com.lasy.dwbk.gui.util.PatternTextField;
import com.lasy.dwbk.util.BboxUtil;
import com.lasy.dwbk.util.Is;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class BboxEditPane extends AModelEditPane<BboxModel>
{
  
  /**
   * Creates the fully initialized bbox pane.
   * @param mainScene main scene
   * @param bbox the bbox (may be {@code null} to create new bboxes).
   * @return fully initialized bbox pane
   */
  public static BboxEditPane create(Scene mainScene, BboxModel bbox)
  {
    return createInitializedPane(new BboxEditPane(mainScene, bbox));
  }
  
  private AttributeInputContainer<BboxModel, TextField, String> attrName;
  private AttributeInputContainer<BboxModel, TextField, String> attrDescription;
  private AttributeInputContainer<BboxModel, TextField, Integer> attrEpsg;
  private AttributeInputContainer<BboxModel, CheckBox, Boolean> attrIsMapBoundary;
  
  private AttributeInputContainer<BboxModel, TextField, String> attrMinLon;
  private AttributeInputContainer<BboxModel, TextField, String> attrMinLat;
  private AttributeInputContainer<BboxModel, TextField, String> attrMaxLon;
  private AttributeInputContainer<BboxModel, TextField, String> attrMaxLat;
  
  /**
   * Bbox create / edit GUI.
   * @param mainScene main scene
   * @param bbox the bbox (may be {@code null} to create new bboxes).
   */
  private BboxEditPane(Scene mainScene, BboxModel bbox)
  {
    super(mainScene, "Boundingbox Verwalten", bbox);
  }

  @Override
  protected void doHandleUpdateModel(BboxModel model)
  {
    model.setName(attrName.getConfiguredValue());
    model.setDescription(attrDescription.getConfiguredValue());
    model.setEpsg(attrEpsg.getConfiguredValue());
    model.setMinLon(attrMinLon.getConfiguredValue());
    model.setMinLat(attrMinLat.getConfiguredValue());
    model.setMaxLon(attrMaxLon.getConfiguredValue());
    model.setMaxLat(attrMaxLat.getConfiguredValue());
    model.setMapBoundary(attrIsMapBoundary.getConfiguredValue());
    
    bBoxService().update(model);
  }

  private BboxCrudService bBoxService()
  {
    return DwbkServiceProvider.getInstance().getBboxService();
  }

  @Override
  protected void doHandleCreateNewModel()
  {
    BboxModelBuilder builder = BboxModel.builder(attrName.getConfiguredValue());
    builder.withDescription(attrDescription.getConfiguredValue());
    builder.withEpsg(attrEpsg.getConfiguredValue());
    builder.withMinLon(attrMinLon.getConfiguredValue());
    builder.withMinLat(attrMinLat.getConfiguredValue());
    builder.withMaxLon(attrMaxLon.getConfiguredValue());
    builder.withMaxLat(attrMaxLat.getConfiguredValue());
    builder.withMapBoundary(attrIsMapBoundary.getConfiguredValue());
    
    bBoxService().create(builder);
  }

  @Override
  protected void goToOverviewPane()
  {
    BboxOverviewPane pane = BboxOverviewPane.create(getMainScene());
    goToPane(pane);
  }

  @Override
  protected void doCreateAttributeInputContainers()
  {
    this.attrName = createAttrName();
    addAttributeInputContainer(attrName);
    
    this.attrDescription = createAttrDescription();
    addAttributeInputContainer(attrDescription);
    
    this.attrEpsg = createAttrEpsg();
    addAttributeInputContainer(attrEpsg);
    
    this.attrIsMapBoundary = createAttrIsMapBoundary();
    addAttributeInputContainer(attrIsMapBoundary);
    
    this.attrMinLon = createCoordinateAttrInputContainer(
      "Minimale Länge", 
      (txtField, bbox) -> txtField.setText(bbox.getMinLon()),
      "West-Ost Wert der 'unteren linken Ecke' des Ausschnitts.");
    addAttributeInputContainer(attrMinLon);
    
    this.attrMinLat = createCoordinateAttrInputContainer(
      "Minimale Breite", 
      (txtField, bbox) -> txtField.setText(bbox.getMinLat()),
      "Nord-Süd Wert der 'unteren linken Ecke' des Ausschnitts.");
    addAttributeInputContainer(attrMinLat);
    
    this.attrMaxLon = createCoordinateAttrInputContainer(
      "Maximale Länge", 
      (txtField, bbox) -> txtField.setText(bbox.getMaxLon()),
      "West-Ost Wert der 'oberen rechten Ecke' des Ausschnitts.");
    addAttributeInputContainer(attrMaxLon);
    
    this.attrMaxLat = createCoordinateAttrInputContainer(
      "Maximale Breite", 
      (txtField, bbox) -> txtField.setText(bbox.getMaxLat()),
      "Nord-Süd Wert der 'oberen rechten Ecke' des Ausschnitts.");
    addAttributeInputContainer(attrMaxLat);
  }
  
  private AttributeInputContainer<BboxModel, CheckBox, Boolean> createAttrIsMapBoundary()
  {
    return AttributeInputContainer.<BboxModel, CheckBox, Boolean>builer("Standard Kartenbegrenzung")
      .withGuiElement(new CheckBox("Als Kartenbegrenzung verwenden?"))
      .withGuiValueInitialization((cb, bbox) -> {
        boolean isMapBoundary = bbox != null
          ? bbox.isMapBoundary()
          : false;
        cb.setSelected(isMapBoundary);
      })
      .withGuiElementToModelAttributeFunc(CheckBox::isSelected)
      .withInfoAlertMessage("Wenn aktiviert, wird dieser Ausschnitt als Standardbegrenzung für die Karte in der App verwendet. "
        + "Es muss genau eine Standard-Kartenbegrenzung vorhanden sein!")
      .build();
  }
  
  private AttributeInputContainer<BboxModel, TextField, String> createCoordinateAttrInputContainer(
    String displayValue,
    BiConsumer<TextField, BboxModel> initializeGuiValueConsumer,
    String info)
  {
    return AttributeInputContainer.<BboxModel, TextField, String>builer(displayValue)
      .withGuiElement(PatternTextField.createCoordinateTextField())
      .withGuiValueInitializationIfModelNotNull(initializeGuiValueConsumer)
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .withInfoAlertMessage(info)
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      .build();
  }
  
  private AttributeInputContainer<BboxModel, TextField, Integer> createAttrEpsg()
  {
    return AttributeInputContainer.<BboxModel, TextField, Integer>builer("EPSG-Code")
      .withGuiElement(PatternTextField.createIntegersOnlyTextField())
      .withGuiValueInitialization((txtField, bbox) -> {
        int epsg = bbox != null
          ? bbox.getEpsg()
          : BboxUtil.EPSG_4326;
        txtField.setText(String.valueOf(epsg));
      })
      .withGuiElementToModelAttributeFunc(txtField -> {
        String txt = txtField.getText();
        return Is.nullOrTrimmedEmpty(txt)
          ? null
          : Integer.valueOf(txt);
      })
      .withInfoAlertMessage("Eindeutiger Schlüssel für das Koordinatensystem.")
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      .build();
  }

  private AttributeInputContainer<BboxModel, TextField, String> createAttrDescription()
  {
    return AttributeInputContainer.<BboxModel, TextField, String>builer("Boundingbox-Beschreibung")
      .withGuiElement(PatternTextField.createAcceptAllTextField())
      .withGuiValueInitializationIfModelNotNull((txtField, bbox) -> {
        txtField.setText(bbox.getDescription().orElse(null));
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .build();
  }

  private AttributeInputContainer<BboxModel, TextField, String> createAttrName()
  {
    return AttributeInputContainer.<BboxModel, TextField, String>builer("Boundingbox-Name")
      .withGuiElement(PatternTextField.createTextOnlyTextField())
      .withGuiValueInitializationIfModelNotNull((txtField, bbox) -> {
        txtField.setText(bbox.getName());
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      .withInfoAlertMessage("Der Name wird bei der Layer-Konfiguration angezeigt.")
      .build();
  }

}
