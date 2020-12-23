package com.lasy.dwbk.gui.panes.edit.util;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import com.lasy.dwbk.app.model.IGtModel;
import com.lasy.dwbk.gui.panes.ADwbkMarginPane;
import com.lasy.dwbk.gui.util.GuiIcon;
import com.lasy.dwbk.gui.util.GuiUtil;
import com.lasy.dwbk.util.Check;
import com.lasy.dwbk.util.Is;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
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
  extends ADwbkMarginPane
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
    setTopWithMargin(nameAndInfoBox, createInsets());
    
    TGuiElement guiElement = builder.guiElement;
    setCenterWithMargin(guiElement, createInsets());
    setAlignment(guiElement, Pos.CENTER_LEFT);
    
    this.errorLbl = createErrorLabel();
    
    setBorder(createBorder());
  }
  
  private void setErrorVisible(boolean isVisible)
  {
    if(isVisible)
    {
      setBottomWithMargin(errorLbl, createInsets());
    }
    else
    {
      setBottom(null);
    }
  }
  
  private Insets createInsets()
  {
    return new Insets(4);
  }

  private Border createBorder()
  {
    CornerRadii radii = new CornerRadii(10);
    BorderWidths width = new BorderWidths(2);
    BorderStroke borderStroke = new BorderStroke(Color.DARKGREY, BorderStrokeStyle.SOLID, radii, width);
    Border border = new Border(borderStroke);
    return border;
  }
  
  private Label createErrorLabel()
  {
    Label lbl = GuiUtil.createBoldLabel(null);
    lbl.setTextFill(Color.RED);
    return lbl;
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
      box.getChildren().add(0, btn);
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
  private void validate()
  {
    setError();
    setErrorVisible(hasError());
  }

  private void setError()
  {
    TModelAttribute configuredValue = getConfiguredValue();
    
    String error = builder.inputValidationFunctions.stream()
      .map(f -> f.apply(configuredValue))
      .filter(Optional::isPresent)
      .map(Optional::get)
      .filter(err -> !Is.nullOrTrimmedEmpty(err))
      .findFirst()
      .orElse(null);
    
    if(Is.nullOrTrimmedEmpty(error))
    {
      for(Map.Entry<AttributeInputContainer<TModel,? extends Node,? extends Object>,BiFunction<Object,TModelAttribute,Optional<String>>> entry : builder.dependingContainerValidationFunctions.entrySet())
      {
        AttributeInputContainer<? extends IGtModel, ? extends Node, ? extends Object> anotherContainer = entry.getKey();
        Object anotherContainerValue = anotherContainer.getConfiguredValue();
        
        BiFunction<Object, TModelAttribute, Optional<String>> errorFunction = entry.getValue();
        error = errorFunction.apply(anotherContainerValue, getConfiguredValue()).orElse(null);
      }
    }
    
    errorLbl.setText(error);
  }
  
  private boolean hasError()
  {
    String error = errorLbl.getText();
    return !Is.nullOrTrimmedEmpty(error);
  }
  
  /**
   * Returns if the current configuration is valid.
   * @return {@code true} if the configuration is valid
   */
  public boolean isValid()
  {
    validate();
    return !hasError();
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
