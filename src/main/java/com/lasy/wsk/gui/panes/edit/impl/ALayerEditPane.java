package com.lasy.wsk.gui.panes.edit.impl;

import java.util.Objects;
import java.util.Optional;

import com.lasy.wsk.app.WskServiceProvider;
import com.lasy.wsk.app.model.impl.LayerModel;
import com.lasy.wsk.app.model.impl.LayerModelBuilder;
import com.lasy.wsk.app.service.impl.LayerCrudService;
import com.lasy.wsk.db.util.DbScriptUtil;
import com.lasy.wsk.gui.panes.edit.AModelEditPane;
import com.lasy.wsk.gui.panes.edit.util.AttributeInputContainer;
import com.lasy.wsk.gui.panes.edit.util.BboxComboBox;
import com.lasy.wsk.gui.panes.overview.impl.LayerOverviewPane;
import com.lasy.wsk.gui.util.AttributeInputValidator;
import com.lasy.wsk.gui.util.PatternTextField;
import com.lasy.wsk.ws.EWebServiceType;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public abstract class ALayerEditPane extends AModelEditPane<LayerModel>
{
  
  protected AttributeInputContainer<LayerModel, TextField, String> attrName;
  protected AttributeInputContainer<LayerModel, TextField, String> attrDescription;
  protected AttributeInputContainer<LayerModel, TextField, String> attrRequest;
  protected AttributeInputContainer<LayerModel, CheckBox, Boolean> attrStoreLocal;
  protected AttributeInputContainer<LayerModel, CheckBox, Boolean> attrIsVisible;
  protected AttributeInputContainer<LayerModel, BboxComboBox, Integer> attrBboxId;
  protected AttributeInputContainer<LayerModel, TextField, String> attrServiceLayerName;
//  private AttributeInputContainer<LayerModel, TextField, String> attrUser;
//  private AttributeInputContainer<LayerModel, TextField, String> attrPw;
  
  /**
   * Layer create / edit GUI.
   * @param mainScene main scene
   * @param title the title
   * @param layer the layer (may be {@code null} to create new layers).
   */
  protected ALayerEditPane(Scene mainScene, String title, LayerModel layer)
  {
    super(mainScene, title, layer);
  }

  @Override
  protected void doHandleUpdateModel(LayerModel layer)
  {
    layer.setName(attrName.getConfiguredValue());
    layer.setDescription(attrDescription.getConfiguredValue());
    layer.setRequest(attrRequest.getConfiguredValue());
    layer.setVisible(attrIsVisible.getConfiguredValue());
    layer.setBboxId(attrBboxId.getConfiguredValue());
//    layer.setUser(attrUser.getConfiguredValue());
//    getConfiguredPw().ifPresent(layer::setPw);
    
    doHandleServiceSpecificUpdateModel(layer);
    
    boolean storeLocal = attrStoreLocal.getConfiguredValue();
    handleStoreLocalSelection(layer, storeLocal);
    
    layerService().update(layer);
  }
  
  /**
   * Add service specific configuration.
   * @param layer
   */
  protected abstract void doHandleServiceSpecificUpdateModel(LayerModel layer);

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
        DbScriptUtil.deleteLocalWmsLayerContentIfPresent(layer);
      }
      
      storeLocal = !deleteLocalData;
    }
    layer.setStoreLocal(storeLocal);
  }
  
  private Alert createDeleteLocalDataAlert()
  {
    // are you sure you want to delete all locally saved data?
    String deleteMsg = "Sollen alle lokal gespeicherten Daten wirklich gelöscht werden?";
    Alert alert = new Alert(AlertType.CONFIRMATION, deleteMsg, ButtonType.YES, ButtonType.NO);
    // delete local data
    alert.setTitle("Lokale Daten löschen");
    alert.setHeaderText(null);
    return alert;
  }

  private LayerCrudService layerService()
  {
    return WskServiceProvider.getInstance().getLayerService();
  }

  @Override
  protected void doHandleCreateNewModel()
  {
    LayerModelBuilder builder = LayerModel.builder(attrName.getConfiguredValue());
    builder.withDescription(attrDescription.getConfiguredValue());
    builder.withRequest(attrRequest.getConfiguredValue());
    builder.withStoreLocal(attrStoreLocal.getConfiguredValue());
    builder.withDefaultVisible(attrIsVisible.getConfiguredValue());
    builder.withBboxId(attrBboxId.getConfiguredValue());
    
    doHandleServiceSpecificNewModel(builder);
//    builder.withUser(attrUser.getConfiguredValue());
//    getConfiguredPw().ifPresent(builder::withPassword);
    
    layerService().create(builder);
  }
  
  /**
   * Add service specific configuration.
   * @param builder
   */
  protected abstract void doHandleServiceSpecificNewModel(LayerModelBuilder builder);

//  private Optional<String> getConfiguredPw()
//  {
//    if(WskFramework.getInstance().getSettings().isSavePasswordAllowed())
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
    
    this.attrBboxId = createAttrBbox();
    addAttributeInputContainer(attrBboxId);
    
    this.attrIsVisible = createAttrIsVisible();
    addAttributeInputContainer(attrIsVisible);
    
    this.attrRequest = createAttrUri();
    addAttributeInputContainer(attrRequest);
    
    this.attrStoreLocal = createAttrStoreLocal();
    addAttributeInputContainer(attrStoreLocal);
    
    this.attrServiceLayerName = createAttrServiceLayerName();
    addAttributeInputContainer(attrServiceLayerName);
    // authentication is not supported (yet)
    
//    this.attrUser = createAttrUser();
//    addAttributeInputContainer(attrUser);
//    
//    if(WskFramework.getInstance().getSettings().isSavePasswordAllowed())
//    {
//      this.attrPw = createAttrPw();
//      addAttributeInputContainer(attrPw);
//    }
  }
  
  private AttributeInputContainer<LayerModel, TextField, String> createAttrServiceLayerName()
  {
    return AttributeInputContainer.<LayerModel, TextField, String>builer("Service Layer-Name")
      .withGuiElement(PatternTextField.createAcceptAllTextField())
      .withGuiValueInitializationIfModelNotNull((txtField, layer) -> {
        if(Objects.equals(layer.getWebServiceType(), EWebServiceType.WMS))
        {
          txtField.setText(layer.getWmsConfig().getLayer());
        }
        else if(Objects.equals(layer.getWebServiceType(), EWebServiceType.WFS))
        {
          txtField.setText(layer.getWfsConfig().getTypeNames());
        }
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      // name (inclusive namespace) of the layer like defined by the service. the name can be copied from the GetCapabilities-request
      .withInfoAlertMessage("Der Name (inklusive Namespace) des Layers wie im Service definiert. "
        + "Der Name kann aus dem GetCapabilities-Request kopiert werden.")
      .build();
  }

  @SuppressWarnings("unused")
  @Deprecated
  private AttributeInputContainer<LayerModel, TextField, String> createAttrPw()
  {
    return AttributeInputContainer.<LayerModel, TextField, String>builer("Service Passwort")
      .withGuiElement(new PasswordField())
      .withGuiValueInitializationIfModelNotNull((txtField, layer) -> {
        txtField.setText(layer.getPw().orElse(null));
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      // PW for layer request
      .withInfoAlertMessage("Das Passwort zur Layer-Abfrage.")
      .build();
  }
  
  @SuppressWarnings("unused")
  @Deprecated
  private AttributeInputContainer<LayerModel, TextField, String> createAttrUser()
  {
    return AttributeInputContainer.<LayerModel, TextField, String>builer("Service Benutzername")
      .withGuiElement(PatternTextField.createAcceptAllTextField())
      .withGuiValueInitializationIfModelNotNull((txtField, layer) -> {
        txtField.setText(layer.getUser().orElse(null));
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      // user for layer request
      .withInfoAlertMessage("Der Benutzername zur Layer-Abfrage.")
      .build();
  }
  
  private AttributeInputContainer<LayerModel, BboxComboBox, Integer> createAttrBbox()
  {
    return AttributeInputContainer.<LayerModel, BboxComboBox, Integer>builer("Layer-Begrenzung")
      .withGuiElement(new BboxComboBox())
      .withGuiValueInitializationIfModelNotNull((comboBox, layer) -> {
        comboBox.setSelectedBboxById(layer.getBboxId());
      })
      .withGuiElementToModelAttributeFunc(BboxComboBox::getSelectedBboxId)
      // selection of a boundingbox is especially useful to reduce memory when layer is stored locally
      .withInfoAlertMessage("Die Auswahl einer Boundingbox macht insbesondere Sinn um die Datenmenge zu begrenzen "
        + "wenn der Layer lokal gespeichert werden soll.")
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      .build();
  }
  
  private AttributeInputContainer<LayerModel, CheckBox, Boolean> createAttrIsVisible()
  {
    // initial visibility
    return AttributeInputContainer.<LayerModel, CheckBox, Boolean>builer("Initiale Layer-Sichtbarkeit")
      .withGuiElement(new CheckBox("Initial sichtbar?"))
      .withGuiValueInitialization((cb, layer) -> {
        boolean visible = layer != null
          ? layer.isVisible()
          : false;
        cb.setSelected(visible);
      })
      .withGuiElementToModelAttributeFunc(CheckBox::isSelected)
      // if activated, the layer is initially visible in the app
      .withInfoAlertMessage("Wenn aktiviert, ist der Layer in der App initial sichtbar geschaltet!")
      .build();
  }
  
  private AttributeInputContainer<LayerModel, CheckBox, Boolean> createAttrStoreLocal()
  {
    // save locally
    return AttributeInputContainer.<LayerModel, CheckBox, Boolean>builer("Layer lokal speichern")
      .withGuiElement(new CheckBox("Lokal speichern?"))
      .withGuiValueInitialization((cb, layer) -> {
        boolean storeLocal = layer != null
          ? layer.isStoreLocal()
          : false;
        cb.setSelected(storeLocal);
      })
      .withGuiElementToModelAttributeFunc(CheckBox::isSelected)
      // if activated, the layer is stored locally and therefore available offline
      .withInfoAlertMessage("Wenn aktiviert, werden die Daten des Layers lokal gespeichert und sind somit auch offline verfügbar!")
      .build();
  }

  private AttributeInputContainer<LayerModel, TextField, String> createAttrUri()
  {
    return AttributeInputContainer.<LayerModel, TextField, String>builer("GetCapabilities-Request")
      .withGuiElement(PatternTextField.createAcceptAllTextField())
      .withGuiValueInitializationIfModelNotNull((txtField, layer) -> {
        txtField.setText(layer.getRequest());
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      // the GetCapabilities request for the service. The request can be tested in the browser
      .withInfoAlertMessage("Der GetCapabilities-Request für den anzufragenden Service. "
        + "Der Request kann im Browser getestet werden.")
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      .withInputValidationError(AttributeInputValidator.createLayerServiceFunction())
      .build();
  }

  private AttributeInputContainer<LayerModel, TextField, String> createAttrDescription()
  {
    // layer description
    return AttributeInputContainer.<LayerModel, TextField, String>builer("Layer-Beschreibung")
      .withGuiElement(PatternTextField.createAcceptAllTextField())
      .withGuiValueInitializationIfModelNotNull((txtField, layer) -> {
        txtField.setText(layer.getDescription().orElse(null));
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .build();
  }

  private AttributeInputContainer<LayerModel, TextField, String> createAttrName()
  {
    return AttributeInputContainer.<LayerModel, TextField, String>builer("Layer-Name")
      .withGuiElement(PatternTextField.createAcceptAllTextField())
      .withGuiValueInitializationIfModelNotNull((txtField, layer) -> {
        txtField.setText(layer.getName());
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .withInputValidationError(AttributeInputValidator.createMandatoryInputFunction())
      // the layer name is used in the client user interface
      .withInfoAlertMessage("Der Layername wird in der Client Benutzeroberfläche verwendet.")
      .build();
  }

}
