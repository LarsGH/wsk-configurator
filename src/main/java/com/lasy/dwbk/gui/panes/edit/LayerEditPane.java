package com.lasy.dwbk.gui.panes.edit;

import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.app.model.impl.LayerModelBuilder;
import com.lasy.dwbk.app.service.impl.LayerCrudService;
import com.lasy.dwbk.gui.panes.overview.LayerOverviewPane;
import com.lasy.dwbk.gui.util.AttributeInputValidator;
import com.lasy.dwbk.gui.util.PatternTextField;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
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
  private AttributeInputContainer<LayerModel, BboxSelectionContainer, Integer> attrBboxId;
  
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
  }
  
  private AttributeInputContainer<LayerModel, BboxSelectionContainer, Integer> createAttrBbox()
  {
    return AttributeInputContainer.<LayerModel, BboxSelectionContainer, Integer>builer("Layer-Begrenzung")
      .withGuiElement(new BboxSelectionContainer())
      .withGuiValueInitialization((bboxContainer, layer) -> {
        bboxContainer.setSelectedBboxById(layer.getBboxId().orElse(null));
      })
      .withGuiElementToModelAttributeFunc(BboxSelectionContainer::getSelectedBboxId)
      .withInfoAlertMessage("Diese Funktion macht insbesondere Sinn um die Datenmenge zu begrenzen, "
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
      .withGuiElement(PatternTextField.createLayerUriTextField())
      .withGuiValueInitialization((txtField, layer) -> {
        txtField.setText(layer.getUri());
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      // TODO: URI Logik bestimmen - hier anpassen
      .withInfoAlertMessage("Über die URI muss eine getCapabilities-Abfrage auf dem Dienst möglich sein!")
      // TODO: weiterer Validator für URI Format! - abstimmen mit info alert
      .withInputValidationError(AttributeInputValidator.MANDATORY_STRING)
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
      .withGuiElement(PatternTextField.createTextOnlyTextField())
      .withGuiValueInitialization((txtField, layer) -> {
        txtField.setText(layer.getName());
      })
      .withGuiElementToModelAttributeFunc(TextField::getText)
      .withInputValidationError(AttributeInputValidator.MANDATORY_STRING)
      .build();
  }

}
