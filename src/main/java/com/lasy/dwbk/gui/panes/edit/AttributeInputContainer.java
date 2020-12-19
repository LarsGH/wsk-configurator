package com.lasy.dwbk.gui.panes.edit;

import java.util.Optional;

import com.lasy.dwbk.app.model.IGtModel;
import com.lasy.dwbk.gui.util.GuiIcon;
import com.lasy.dwbk.gui.util.GuiUtil;
import com.lasy.dwbk.util.Check;
import com.lasy.dwbk.util.Is;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Container for user input attributes. The container is used to set the model values.
 * @author larss
 *
 * @param <TModel> the model type
 * @param <TGuiElement> GUI element type (e.g. TextField)
 * @param <TModelAttribute> expected model attribute type
 */
public class AttributeInputContainer<TModel extends IGtModel, TGuiElement extends Node, TModelAttribute> 
  extends BorderPane
{
  
  private final AttributeInputContainerBuilder<TModel, TGuiElement, TModelAttribute> builder;
  private Label errorLbl;
  
  /**
   * Constructor.
   * 
   * @param attributeDisplayName attribute display name
   * @param guiElement the GUI element (Node)
   * @param containerToModelAttributeFunction function to get the model attribute from the GUI element 
   * @param inputValidationFunction function to determine if the current value is valid
   */
  protected AttributeInputContainer(AttributeInputContainerBuilder<TModel, TGuiElement, TModelAttribute> builder)
  {
    this.builder = Check.notNull(builder, "builder");
    
    initContainer();
  }
  
  public static<TModel extends IGtModel, TGuiElement extends Node, TModelAttribute> 
    AttributeInputContainerBuilder<TModel, TGuiElement, TModelAttribute> 
    builer(String attributeDisplayName)
  {
    return new AttributeInputContainerBuilder<TModel, TGuiElement, TModelAttribute>(attributeDisplayName);
  }

  private void initContainer()
  {
    HBox nameAndInfoBox = createNameAndOptionalInfoBox();
    setTop(nameAndInfoBox);
    
    TGuiElement guiElement = builder.guiElement;
    setCenter(guiElement);
    setAlignment(guiElement, Pos.CENTER_LEFT);
    
    this.errorLbl = GuiUtil.createBoldLabel(null);
    errorLbl.setTextFill(Color.RED);
    setBottom(errorLbl);
  }
  
  private HBox createNameAndOptionalInfoBox()
  {
    Label nameLbl = GuiUtil.createBoldLabel(builder.attributeDisplayName);
    HBox box = new HBox(GuiUtil.DEFAULT_SPACING, nameLbl);
    box.setAlignment(Pos.CENTER_LEFT);
    
    String infoAlertMessage = builder.infoAlertMessage;
    if(!Is.nullOrTrimmedEmpty(infoAlertMessage))
    {
      Button btn = createInfoButton(infoAlertMessage);
      box.getChildren().add(btn);
    }
    
    return box;
  }

  private Button createInfoButton(String infoAlertMessage)
  {
    Button btn = GuiUtil.createIconButton(GuiIcon.INFO, "Attribut-Hinweis");
    btn.setOnAction(e -> {
      Alert alert = GuiUtil.createOkAlert(AlertType.INFORMATION, "Attribut-Hinweis", infoAlertMessage);
      alert.show();
    });
    return btn;
  }

  /**
   * Returns the configured value (as expected by the model).
   * @return configured value
   */
  public TModelAttribute getConfiguredValue()
  {
    return builder.containerToModelAttributeFunction.apply(builder.guiElement);
  }
  
  /**
   * Validates the current configuration.
   * If the configuration is invalid an error message will be set on the error label.
   */
  public void validate()
  {
    TModelAttribute configuredValue = getConfiguredValue();
    
    String error = builder.inputValidationFunctions.stream()
      .map(f -> f.apply(configuredValue))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .filter(err -> !Is.nullOrTrimmedEmpty(err))
      .findFirst()
      .orElse(null);
    
    errorLbl.setText(error);
  }
  
  /**
   * Returns if the current configuration is valid.
   * @return {@code true} if the configuration is valid
   */
  public boolean isValid()
  {
    validate();
    String error = errorLbl.getText();
    return Is.nullOrTrimmedEmpty(error);
  }
  
  /**
   * Initializes the GUI element value from the provided model.
   * @param model the model used to get the value
   */
  public void initValueFromModel(TModel model)
  {
    if(model != null)
    {
      builder.initializeGuiValueConsumer.accept(builder.guiElement, model);
    }
  }
  
  @Override
  public String toString()
  {
    String err = getErrorRepresentation();
    return String.format("%s[%s]%s", 
      getClass().getSimpleName(), 
      builder.attributeDisplayName,
      err);
  }

  private String getErrorRepresentation()
  {
    String error = errorLbl.getText();
    if(Is.nullOrTrimmedEmpty(error))
    {
      return "";
    }
    return String.format(" - Err: ", error);
  }

}
