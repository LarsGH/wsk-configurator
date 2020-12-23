package com.lasy.dwbk.gui.panes.edit.impl;

import java.util.Optional;

import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.app.model.impl.LayerModelBuilder;
import com.lasy.dwbk.app.service.impl.LayerCrudService;
import com.lasy.dwbk.db.util.DbPasswordModifier;
import com.lasy.dwbk.gui.panes.edit.AModelEditPane;
import com.lasy.dwbk.gui.panes.edit.util.AttributeInputContainer;
import com.lasy.dwbk.gui.panes.edit.util.BboxComboBox;
import com.lasy.dwbk.gui.panes.overview.impl.LayerOverviewPane;
import com.lasy.dwbk.gui.util.AttributeInputValidator;
import com.lasy.dwbk.gui.util.PatternTextField;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LayerEditPane extends AModelEditPane<LayerModel>
{
  
  /**
   * Creates the fully initialized layer pane.
   * @param mainScene main scene
   * @param layer the layer (may be {@code null} to create new layers).
   * @return fully initialized layer pane
   */
  public static LayerEditPane create(Scene mainScene, LayerModel layer)
  {
    return createInitializedPane(new LayerEditPane(mainScene, layer));
  }
  
  private AttributeInputContainer<LayerModel, TextField, String> attrName;
  private AttributeInputContainer<LayerModel, TextField, String> attrDescription;
  private AttributeInputContainer<LayerModel, TextField, String> attrUri;
  private AttributeInputContainer<LayerModel, CheckBox, Boolean> attrStoreLocal;
  private AttributeInputContainer<LayerModel, BboxComboBox, Integer> attrBboxId;
  private AttributeInputContainer<LayerModel, TextField, String> attrUser;
  private AttributeInputContainer<LayerModel, TextField, String> attrPw;
  
  /**
   * Layer create / edit GUI.
   * @param mainScene main scene
   * @param layer the layer (may be {@code null} to create new layers).
   */
  private LayerEditPane(Scene mainScene, LayerModel layer)
  {
    super(mainScene, "Layer Verwalten", layer);
  }

  @Override
  protected void doHandleUpdateModel(LayerModel model)
  {
    model.setName(attrName.getConfiguredValue());
    model.setDescription(attrDescription.getConfiguredValue());
    model.setUri(attrUri.getConfiguredValue());
    model.setStoreLocal(attrStoreLocal.getConfiguredValue());
    model.setBboxId(attrBboxId.getConfiguredValue());
    model.setUser(attrUser.getConfiguredValue());
    model.setPw(DbPasswordModifier.toDbValue(attrPw.getConfiguredValue()));
    
    layerService().update(model);
  }

  private LayerCrudService layerService()
  {
    return DwbkServiceProvider.getInstance().getLayerService();
  }

  @Override
  protected void doHandleCreateNewModel()
  {
    LayerModelBuilder builder = LayerModel.builder(attrName.getConfiguredValue());
    builder.withDescription(attrDescription.getConfiguredValue());
    builder.withUri(attrUri.getConfiguredValue());
    builder.withStoreLocal(attrStoreLocal.getConfiguredValue());
    builder.withBboxId(attrBboxId.getConfiguredValue());
    builder.withUser(attrUser.getConfiguredValue());
    builder.withPassword(DbPasswordModifier.toDbValue(attrPw.getConfiguredValue()));
    
    layerService().create(builder);
  }

  @Override
  protected void goToOverviewPane()
  {
    LayerOverviewPane pane = LayerOverviewPane.create(getMainScene());
    goToPane(pane);
  }

  @Override
  protected void doCreateAttributeInputContainers()
  {
    this.attrName = createAttrName();
    addAttributeInputContainer(attrName);
    
    this.attrDescription = createAttrDescription();
    addAttributeInputContainer(attrDescription);
    
    this.attrUri = createAttrUri();
    addAttributeInputContainer(attrUri);
    
    this.attrStoreLocal = createAttrStoreLocal();
    addAttributeInputContainer(attrStoreLocal);
    
    this.attrBboxId = createAttrBbox();
    addAttributeInputContainer(attrBboxId);
    
    this.attrUser = createAttrUser();
    addAttributeInputContainer(attrUser);
    
    this.attrPw = createAttrPw();
    addAttributeInputContainer(attrPw);
  }
  
  private AttributeInputContainer<LayerModel, TextField, String> createAttrPw()
  {
    return AttributeInputContainer.<LayerModel, TextField, String>builer("Service Benutzername")
      .withGuiElement(new PasswordField())
      .withGuiValueInitialization((txtField, layer) -> {
        txtField.setText(layer.getPw().orElse(null));
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .withInfoAlertMessage("Das Login-Passwort zur Layer-Abfrage.")
      .build();
  }
  
  private AttributeInputContainer<LayerModel, TextField, String> createAttrUser()
  {
    return AttributeInputContainer.<LayerModel, TextField, String>builer("Service Benutzername")
      .withGuiElement(PatternTextField.createAcceptAllTextField())
      .withGuiValueInitialization((txtField, layer) -> {
        txtField.setText(layer.getUser().orElse(null));
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .withInfoAlertMessage("Der Login-Benutzername zur Layer-Abfrage.")
      .build();
  }
  
  // TODO: Future work: Compare configured bbox with layer bbox from URI!
  // <LatLonBoundingBox minx="5.72499" miny="50.1506" maxx="9.53154" maxy="52.602"/>
  private AttributeInputContainer<LayerModel, BboxComboBox, Integer> createAttrBbox()
  {
    return AttributeInputContainer.<LayerModel, BboxComboBox, Integer>builer("Layer-Begrenzung")
      .withGuiElement(new BboxComboBox())
      .withGuiValueInitialization((comboBox, layer) -> {
        comboBox.setSelectedBboxById(layer.getBboxId().orElse(null));
      })
      .withGuiElementToModelAttributeFunc(BboxComboBox::getSelectedBboxId)
      .withDependingContainerValidator(attrStoreLocal, (storeLocalObj, bboxId) -> {
        Boolean storeLocal = storeLocalObj == null
          ? false
          : (Boolean) storeLocalObj;
        if (storeLocal && bboxId == null)
        {
          return Optional.of("Es muss eine Boundingbox angegeben werden, wenn der Layer lokal gespeichert wird!");
        }
        return Optional.empty();
      })
      .withInfoAlertMessage("Die Auswahl einer Boundingbox macht insbesondere Sinn um die Datenmenge zu begrenzen "
        + "wenn der Layer lokal gespeichert werden soll.")
      .build();
  }
  
  private AttributeInputContainer<LayerModel, CheckBox, Boolean> createAttrStoreLocal()
  {
    return AttributeInputContainer.<LayerModel, CheckBox, Boolean>builer("Layer lokal speichern")
      .withGuiElement(new CheckBox("Lokal speichern?"))
      .withGuiValueInitialization((cb, layer) -> {
        cb.setSelected(layer.isStoreLocal());
      })
      .withGuiElementToModelAttributeFunc(CheckBox::isSelected)
      .withInfoAlertMessage("Wenn aktiviert, werden die Daten des Layers lokal gespeichert und sind somit auch offline verfügbar!")
      .build();
  }

  private AttributeInputContainer<LayerModel, TextField, String> createAttrUri()
  {
    return AttributeInputContainer.<LayerModel, TextField, String>builer("Layer-URI")
      .withGuiElement(PatternTextField.createAcceptAllTextField())
      .withGuiValueInitialization((txtField, layer) -> {
        txtField.setText(layer.getUri());
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .withInfoAlertMessage("Die URI wird verwendet um einen WMS- oder WFS-Service anzufragen. "
        + "Für WMS-Services muss ein GetMap-Request angegeben werden."
        + "Für WFS-Services muss ein GetFeature-Request angegeben werden.")
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      .withInputValidationError(AttributeInputValidator.createLayerServiceFunction())
      .build();
  }

  private AttributeInputContainer<LayerModel, TextField, String> createAttrDescription()
  {
    return AttributeInputContainer.<LayerModel, TextField, String>builer("Layer-Beschreibung")
      .withGuiElement(PatternTextField.createAcceptAllTextField())
      .withGuiValueInitialization((txtField, layer) -> {
        txtField.setText(layer.getDescription().orElse(null));
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .build();
  }

  private AttributeInputContainer<LayerModel, TextField, String> createAttrName()
  {
    return AttributeInputContainer.<LayerModel, TextField, String>builer("Layer-Name")
      .withGuiElement(PatternTextField.createAcceptAllTextField())
      .withGuiValueInitialization((txtField, layer) -> {
        txtField.setText(layer.getName());
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      .withInfoAlertMessage("Der Layername wird in der Client Benutzeroberfläche verwendet.")
      .build();
  }

}
