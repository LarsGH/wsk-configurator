package com.lasy.dwbk.gui.panes.edit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.lasy.dwbk.app.model.IGtModel;
import com.lasy.dwbk.util.Check;

import javafx.scene.Node;

/**
 * Builder for {@link AttributeInputContainer}.
 * @author larss
 *
 * @param <TModel> the model type
 * @param <TGuiElement> GUI element type (e.g. TextField)
 * @param <TModelAttribute> expected model attribute type
 */
public class AttributeInputContainerBuilder<TModel extends IGtModel, TGuiElement extends Node, TModelAttribute> 
{

  protected String attributeDisplayName;
  protected TGuiElement guiElement;
  protected BiConsumer<TGuiElement, TModel> initializeGuiValueConsumer;
  protected Function<TGuiElement, TModelAttribute> containerToModelAttributeFunction;
  protected List<Function<TModelAttribute, Optional<String>>> inputValidationFunctions;
  protected String infoAlertMessage;
  
  /**
   * Builder.
   * @param attributeDisplayName the attribute display name
   */
  protected AttributeInputContainerBuilder(String attributeDisplayName)
  {
    this.attributeDisplayName = Check.trimmedNotEmpty(attributeDisplayName, "attributeDisplayName");
    this.inputValidationFunctions = new ArrayList<>();
  }
  
  public AttributeInputContainer<TModel, TGuiElement, TModelAttribute> build()
  {
    Check.notNull(guiElement, "guiElement");
    Check.notNull(initializeGuiValueConsumer, "initializeGuiValueConsumer");
    Check.notNull(containerToModelAttributeFunction, "containerToModelAttributeFunction");
    
    return new AttributeInputContainer<TModel, TGuiElement, TModelAttribute>(this);
  }
  
  /**
   * Sets the GUI Element used to configure the attribute value (e.g. TextField).
   * @param guiElement GUI Element
   * @return builder
   */
  public AttributeInputContainerBuilder<TModel, TGuiElement, TModelAttribute> withGuiElement(TGuiElement guiElement)
  {
    this.guiElement = Check.notNull(guiElement, "guiElement");
    return this;
  }
  
  /**
   * Sets the Consumer to set the initial GUI value from a model (if present).
   * @param initializeGuiValueConsumer consumer
   * @return builder
   */
  public AttributeInputContainerBuilder<TModel, TGuiElement, TModelAttribute> withGuiValueInitialization(
    BiConsumer<TGuiElement, TModel> initializeGuiValueConsumer)
  {
    this.initializeGuiValueConsumer = Check.notNull(initializeGuiValueConsumer, "initializeGuiValueConsumer");
    return this;
  }
  
  /**
   * Sets the Function to transform the current container value to the expected model attribute.
   * @param containerToModelAttributeFunction function
   * @return builder
   */
  public AttributeInputContainerBuilder<TModel, TGuiElement, TModelAttribute> withGuiElementToModelAttributeFunc(
    Function<TGuiElement, TModelAttribute> containerToModelAttributeFunction)
  {
    this.containerToModelAttributeFunction = Check.notNull(containerToModelAttributeFunction, "containerToModelAttributeFunction");
    return this;
  }
  
  /**
   * Sets the Function to validate the current container value. The function returns an error String.
   * @param inputValidationFunction function
   * @return builder
   */
  public AttributeInputContainerBuilder<TModel, TGuiElement, TModelAttribute> withInputValidationError(
    Function<TModelAttribute, Optional<String>> inputValidationFunction)
  {
    Check.notNull(inputValidationFunction, "inputValidationFunction");
    this.inputValidationFunctions.add(inputValidationFunction);
    return this;
  }
  
  /**
   * Sets the the message to get more information about the attribute (via info alert).
   * @param infoAlertMessage message
   * @return builder
   */
  public AttributeInputContainerBuilder<TModel, TGuiElement, TModelAttribute> withInfoAlertMessage(
    String infoAlertMessage)
  {
    this.infoAlertMessage = Check.trimmedNotEmpty(infoAlertMessage, infoAlertMessage);
    return this;
  }
}
