package com.lasy.dwbk.gui.panes.edit.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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
public class AttributeInputContainerBuilder<TModel extends IGtModel, TGuiElement extends Node, TModelAttribute extends Object> 
{

  protected String attributeDisplayName;
  protected TGuiElement guiElement;
  protected BiConsumer<TGuiElement, TModel> initializeGuiValueConsumer;
  protected Function<TGuiElement, TModelAttribute> containerToModelAttributeFunction;
  protected List<Function<TModelAttribute, Optional<String>>> inputValidationFunctions;
  protected Map<AttributeInputContainer<TModel, ? extends Node, ? extends Object>, 
    BiFunction<Object, TModelAttribute, Optional<String>>> dependingContainerValidationFunctions;
  protected String infoAlertMessage;
  
  /**
   * Builder.
   * @param attributeDisplayName the attribute display name
   */
  protected AttributeInputContainerBuilder(String attributeDisplayName)
  {
    this.attributeDisplayName = Check.trimmedNotEmpty(attributeDisplayName, "attributeDisplayName");
    this.inputValidationFunctions = new ArrayList<>();
    this.dependingContainerValidationFunctions = new HashMap<>();
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
   * Sets the Consumer to set the initial GUI value from a model (model may be null!).
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
   * Sets the Consumer to set the initial GUI value from a model (if present).
   * @param initializeGuiValueConsumer consumer
   * @return builder
   */
  public AttributeInputContainerBuilder<TModel, TGuiElement, TModelAttribute> withGuiValueInitializationIfModelNotNull(
    BiConsumer<TGuiElement, TModel> initializeGuiValueConsumer)
  {
    Check.notNull(initializeGuiValueConsumer, "initializeGuiValueConsumer");

    this.initializeGuiValueConsumer = (guiElement, model) -> {
      if (model != null)
      {
        initializeGuiValueConsumer.accept(guiElement, model);
      }
    };
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

  /**
   * Adds a depending container validation. 
   * The optional message is generated depending on the values of this container and the value of other container.
   * 
   * @param <TAnotherModelAttribute> the attribute type of the other container
   * @param container the other container
   * @param validationFunction generates an optional message depending on the values of this container and the value of other container
   * @return builder
   */
  public AttributeInputContainerBuilder<TModel, TGuiElement, TModelAttribute> withDependingContainerValidator(
    AttributeInputContainer<TModel, ? extends Node, ? extends Object> container, 
    BiFunction<Object, TModelAttribute, Optional<String>> validationFunction)
  {
    this.dependingContainerValidationFunctions.put(container, validationFunction);
    return this;
  }
  
}
