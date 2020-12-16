package com.lasy.dwbk.gui.panes;

import com.lasy.dwbk.app.model.IGtModel;
import com.lasy.dwbk.gui.util.GuiUtil;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.HBox;

/**
 * Adds standard buttons to the pane.
 * @author larss
 *
 * @param <TModelType> the model type
 */
public abstract class AModelEditPane<TModelType extends IGtModel> extends ADwbkPane
{
  
  private TModelType model;

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
    
    setBottom(createSaveCancelButtonBox());
  }
  
  private HBox createSaveCancelButtonBox()
  {
    
    Button saveBtn = GuiUtil.createTextButton("Speichern");
    saveBtn.setOnAction(e -> {
      handleSave();
      goToMainPane();
    });
    
    Button cancelBtn = GuiUtil.createTextButton("Abbrechen");
    cancelBtn.setOnAction(e -> {
      goToMainPane();
    });
    
    return new HBox(saveBtn, cancelBtn);
  }
  
  /**
   * Save the model.
   */
  protected void handleSave()
  {
    try
    {
      validateUserInput();
    }
    catch (IllegalArgumentException e)
    {
      showInvalidInputAlert();
    }
    
    if(getModel() == null)
    {
      doHandleCreateNewModel();
    }
    else
    {
      doHandleUpdateModel(getModel());
    }
  }

  private void showInvalidInputAlert()
  {
    Alert alert = new Alert(
      AlertType.WARNING, 
      "Bitte überprüfen Sie die Eingaben", 
      ButtonType.YES, ButtonType.NO);
    alert.setTitle("Invalide Eingaben");
    alert.setHeaderText(null);
    alert.show();
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
  
  

  /**
   * Returns the model
   * @return the model
   */
  public TModelType getModel()
  {
    return this.model;
  }
  
  /**
   * Validates the user input.
   * @throws IllegalArgumentException if the user input is not valid
   */
  abstract void validateUserInput() throws IllegalArgumentException;

}
