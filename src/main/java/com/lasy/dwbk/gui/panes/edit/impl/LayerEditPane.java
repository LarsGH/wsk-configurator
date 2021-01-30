package com.lasy.dwbk.gui.panes.edit.impl;

import java.util.Optional;

import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.app.model.impl.LayerModelBuilder;
import com.lasy.dwbk.app.service.impl.LayerCrudService;
import com.lasy.dwbk.db.util.DbScriptUtil;
import com.lasy.dwbk.gui.panes.edit.AModelEditPane;
import com.lasy.dwbk.gui.panes.edit.util.AttributeInputContainer;
import com.lasy.dwbk.gui.panes.edit.util.BboxComboBox;
import com.lasy.dwbk.gui.panes.overview.impl.LayerOverviewPane;
import com.lasy.dwbk.gui.util.AttributeInputValidator;
import com.lasy.dwbk.gui.util.PatternTextField;
import com.lasy.dwbk.util.Is;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
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
  private AttributeInputContainer<LayerModel, TextField, String> attrMetersPerPixel;
  private AttributeInputContainer<LayerModel, CheckBox, Boolean> attrStoreLocal;
  private AttributeInputContainer<LayerModel, CheckBox, Boolean> attrIsVisible;
  private AttributeInputContainer<LayerModel, BboxComboBox, Integer> attrBboxId;
//  private AttributeInputContainer<LayerModel, TextField, String> attrUser;
//  private AttributeInputContainer<LayerModel, TextField, String> attrPw;
  
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
  protected void doHandleUpdateModel(LayerModel layer)
  {
    layer.setName(attrName.getConfiguredValue());
    layer.setDescription(attrDescription.getConfiguredValue());
    layer.setUri(attrUri.getConfiguredValue());
    layer.setMetersPerPixelText(attrMetersPerPixel.getConfiguredValue());
    layer.setVisible(attrIsVisible.getConfiguredValue());
    layer.setBboxId(attrBboxId.getConfiguredValue());
//    layer.setUser(attrUser.getConfiguredValue());
//    getConfiguredPw().ifPresent(layer::setPw);
    
    boolean storeLocal = attrStoreLocal.getConfiguredValue();
    handleStoreLocalSelection(layer, storeLocal);
    
    layerService().update(layer);
  }

  private void handleStoreLocalSelection(LayerModel layer, boolean storeLocal)
  {
    if(!storeLocal
      && layer.getLastDownloadDate().isPresent())
    {
      Alert alert = createDeleteLocalDataAlert();
      Optional<ButtonType> result = alert.showAndWait();

      boolean deleteLocalData = result.isPresent() 
        && result.get() == ButtonType.YES;
      
      if(deleteLocalData)
      {
        DbScriptUtil.deleteLocalLayerContentIfPresent(layer);
      }
      
      storeLocal = !deleteLocalData;
    }
    layer.setStoreLocal(storeLocal);
  }
  
  private Alert createDeleteLocalDataAlert()
  {
    String deleteMsg = "Sollen alle lokal gespeicherten Daten wirklich gelöscht werden?";
    Alert alert = new Alert(AlertType.CONFIRMATION, deleteMsg, ButtonType.YES, ButtonType.NO);
    alert.setTitle("Lokale Daten löschen");
    alert.setHeaderText(null);
    return alert;
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
    builder.withMetersPerPixel(attrMetersPerPixel.getConfiguredValue());
    builder.withStoreLocal(attrStoreLocal.getConfiguredValue());
    builder.withDefaultVisible(attrIsVisible.getConfiguredValue());
    builder.withBboxId(attrBboxId.getConfiguredValue());
//    builder.withUser(attrUser.getConfiguredValue());
//    getConfiguredPw().ifPresent(builder::withPassword);
    
    layerService().create(builder);
  }
  
//  private Optional<String> getConfiguredPw()
//  {
//    if(DwbkFramework.getInstance().getSettings().isSavePasswordAllowed())
//    {
//      Optional.ofNullable(DbPasswordModifier.toDbValue(attrPw.getConfiguredValue()));
//    }
//    return Optional.empty();
//  }

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
    
    this.attrMetersPerPixel = createAttrMetersPerPixel();
    addAttributeInputContainer(attrMetersPerPixel);
    
    this.attrIsVisible = createAttrIsVisible();
    addAttributeInputContainer(attrIsVisible);
    
    this.attrBboxId = createAttrBbox();
    addAttributeInputContainer(attrBboxId);
    
    // authentication is not supported (yet)
    
//    this.attrUser = createAttrUser();
//    addAttributeInputContainer(attrUser);
//    
//    if(DwbkFramework.getInstance().getSettings().isSavePasswordAllowed())
//    {
//      this.attrPw = createAttrPw();
//      addAttributeInputContainer(attrPw);
//    }
  }
  
  private AttributeInputContainer<LayerModel, TextField, String> createAttrMetersPerPixel()
  {
    return AttributeInputContainer.<LayerModel, TextField, String>builer("Auflösung pro Zoomstufe")
      .withGuiElement(PatternTextField.createNumbersSeparatedBySemicolonsTextField())
      .withGuiValueInitialization((txtField, layer) -> {
        txtField.setText(layer.getMetersPerPixelText());
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .withInfoAlertMessage("Die Auflösung pro Pixel. Kann mehrere Werte (absteigend) getrennt durch ';' enthalten. "
        + "Die Werte müssen Teiler von 500 sein (500 % <Auflösung> = 0). Die Liste wird beim Speichern automatisch geordnet.")
      .withDependingContainerValidator(attrStoreLocal, (storeLocalObj, metersPerPixel) -> {
        Boolean storeLocal = storeLocalObj == null
          ? false
          : (Boolean) storeLocalObj;
        if (storeLocal && Is.nullOrTrimmedEmpty(metersPerPixel))
        {
          return Optional.of("Es muss mindestens eine Auflösung angegeben werden, wenn der Layer lokal gespeichert wird!");
        }
        return Optional.empty();
      })
      .withInputValidationError(AttributeInputValidator.createDivisorInputFunction(500))
      .build();
  }

  @SuppressWarnings("unused")
  @Deprecated
  private AttributeInputContainer<LayerModel, TextField, String> createAttrPw()
  {
    return AttributeInputContainer.<LayerModel, TextField, String>builer("Service Passwort")
      .withGuiElement(new PasswordField())
      .withGuiValueInitialization((txtField, layer) -> {
        txtField.setText(layer.getPw().orElse(null));
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .withInfoAlertMessage("Das Passwort zur Layer-Abfrage.")
      .build();
  }
  
  @SuppressWarnings("unused")
  @Deprecated
  private AttributeInputContainer<LayerModel, TextField, String> createAttrUser()
  {
    return AttributeInputContainer.<LayerModel, TextField, String>builer("Service Benutzername")
      .withGuiElement(PatternTextField.createAcceptAllTextField())
      .withGuiValueInitialization((txtField, layer) -> {
        txtField.setText(layer.getUser().orElse(null));
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .withInfoAlertMessage("Der Benutzername zur Layer-Abfrage.")
      .build();
  }
  
  private AttributeInputContainer<LayerModel, BboxComboBox, Integer> createAttrBbox()
  {
    return AttributeInputContainer.<LayerModel, BboxComboBox, Integer>builer("Layer-Begrenzung")
      .withGuiElement(new BboxComboBox())
      .withGuiValueInitialization((comboBox, layer) -> {
        comboBox.setSelectedBboxById(layer.getBboxId());
      })
      .withGuiElementToModelAttributeFunc(BboxComboBox::getSelectedBboxId)
      .withInfoAlertMessage("Die Auswahl einer Boundingbox macht insbesondere Sinn um die Datenmenge zu begrenzen "
        + "wenn der Layer lokal gespeichert werden soll.")
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      .build();
  }
  
  private AttributeInputContainer<LayerModel, CheckBox, Boolean> createAttrIsVisible()
  {
    return AttributeInputContainer.<LayerModel, CheckBox, Boolean>builer("Initiale Layer-Sichtbarkeit")
      .withGuiElement(new CheckBox("Initial sichtbar?"))
      .withGuiValueInitialization((cb, layer) -> {
        cb.setSelected(layer.isVisible());
      })
      .withGuiElementToModelAttributeFunc(CheckBox::isSelected)
      .withInfoAlertMessage("Wenn aktiviert, ist der Layer in der App initial sichtbar geschaltet!")
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
