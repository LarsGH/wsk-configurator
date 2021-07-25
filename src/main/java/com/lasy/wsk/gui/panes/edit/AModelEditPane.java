package com.lasy.wsk.gui.panes.edit;

import java.util.ArrayList;
import java.util.List;

import com.lasy.wsk.app.model.IGtModel;
import com.lasy.wsk.gui.panes.AWskPane;
import com.lasy.wsk.gui.panes.edit.util.AttributeInputContainer;
import com.lasy.wsk.gui.util.GuiIcon;
import com.lasy.wsk.gui.util.GuiUtil;
import com.lasy.wsk.util.Check;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Adds standard buttons to the pane.
 * @author larss
 *
 * @param <TModelType> the model type
 */
public abstract class AModelEditPane<TModelType extends IGtModel> extends AWskPane
{
  
  private TModelType model;
  private List<AttributeInputContainer<TModelType, ?, ?>> attributeContainers = new ArrayList<>();

  /**
   * Creates a new pane to create / edit models.
   * @param mainScene the main scene
   * @param header the pane header
   * @param model the model to edit (may be {@code null} to create a new model)
   */
  public AModelEditPane(Scene mainScene, String header, TModelType model)
  {
    super(mainScene, header);
    this.model = model;
    
    doCreateAttributeInputContainers();
  }
  
  @Override
  public void init()
  {
    super.init();
    initAttributeContainerValues(getModel());
  }
  
  /**
   * Initializes the attribute container values.
   * @param model the model to fetch the initial values.
   */
  private void initAttributeContainerValues(TModelType model)
  {
    for(AttributeInputContainer<TModelType, ?, ?> attributeContainer :  attributeContainers)
    {
      attributeContainer.initValueFromModel(model);
    }
  }

  private HBox createSaveCancelButtonBox()
  {
    Button saveBtn = GuiUtil.createIconButtonWithText(GuiIcon.SAVE, "Speichern", "Speichern");
    saveBtn.setOnAction(e -> {
      boolean saveSuccessful = handleSave();
      if (saveSuccessful)
      {
        goToOverviewPane();
      }
    });

    Button cancelBtn = GuiUtil.createIconButtonWithText(GuiIcon.CANCEL, "Abbrechen", "Abbrechen");
    cancelBtn.setOnAction(e -> {
      goToOverviewPane();
    });

    HBox box = new HBox(GuiUtil.DEFAULT_SPACING, saveBtn, cancelBtn);
    box.setAlignment(Pos.CENTER_RIGHT);
    return box;
  }
  
  /**
   * Returns the model
   * @return the model
   */
  public TModelType getModel()
  {
    return this.model;
  }
  
  /**
   * Save the model.
   * @return {@code true}, if saving was successful
   */
  private boolean handleSave()
  {
    if (hasInvalidUserInput())
    {
      showInvalidInputAlert();
      return false;
    }
    else
    {
      if (getModel() == null)
      {
        doHandleCreateNewModel();
      }
      else
      {
        doHandleUpdateModel(getModel());
      }
      return true;
    }
  }

  private boolean hasInvalidUserInput()
  {
    boolean hasInvalidInput = false;
    for(AttributeInputContainer<TModelType, ?, ?> container : attributeContainers)
    {
      // also generates error label content if invalid!
      if(!container.isValid())
      {
        hasInvalidInput = true;
      }
    }
    return hasInvalidInput;
  }

  private void showInvalidInputAlert()
  {
    // invalid input / please check the input
    Alert alert = GuiUtil.createOkAlert(AlertType.WARNING, "Fehlerhafte Eingabe", "Bitte überprüfen Sie die Eingaben");
    alert.show();
  }
  
  @Override
  protected Node createBottomContent()
  {
    return createSaveCancelButtonBox();
  }
  
  @Override
  protected Node createCenterContent()
  {
    VBox box = new VBox(GuiUtil.DEFAULT_SPACING);
    box.getChildren().addAll(attributeContainers);
    box.setAlignment(Pos.TOP_LEFT);
    return box;
  }
  
  /**
   * Switches to the overview pane.
   */
  protected abstract void goToOverviewPane();
  
  /**
   * Initializes the input attribute containers. </p>
   * Every container <b>needs to be added</b> using {@code AModelEditPane#addAttributeInputContainer(AttributeInputContainer)}.
   * 
   * @return input attribute containers
   */
  protected abstract void doCreateAttributeInputContainers();
  
  /**
   * Adds the container.
   * @param container attribute input container
   */
  protected void addAttributeInputContainer(AttributeInputContainer<TModelType, ?, ?> container)
  {
    Check.notNull(container, "container");
    this.attributeContainers.add(container);
  }
  
  /**
   * First updates the model attributes with the current user input. Then update using the CRUD service.
   * 
   * @param model the model to update
   */
  protected abstract void doHandleUpdateModel(TModelType model);

  /**
   * Create new model using the CRUD service.
   */
  protected abstract void doHandleCreateNewModel();

}
