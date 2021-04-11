package com.lasy.wsk.gui.util;

import com.lasy.wsk.util.Check;
import com.lasy.wsk.validation.IUserInputValidator;
import com.lasy.wsk.validation.impl.AcceptAllValidator;
import com.lasy.wsk.validation.impl.CoordinateValidator;
import com.lasy.wsk.validation.impl.IntegersOnlyValidator;
import com.lasy.wsk.validation.impl.IntegersSeparatedBySemicolonValidator;
import com.lasy.wsk.validation.impl.TextOnlyValidator;

import javafx.scene.control.TextField;

/**
 * Textfield which only allows pattern based input.
 * @author larss
 *
 */
public class PatternTextField extends TextField
{
  
  /**
   * Returns a textfield which allows coordinates only.
   * @return textfield
   */
  public static PatternTextField createCoordinateTextField()
  {
    return new PatternTextField(new CoordinateValidator());
  }
  
  /**
   * Returns a textfield which allows integers only.
   * @return textfield
   */
  public static PatternTextField createIntegersOnlyTextField()
  {
    return new PatternTextField(new IntegersOnlyValidator());
  }
  
  /**
   * Returns a textfield with allows a list of integers separated by semicolons.
   * @return textfield
   */
  public static PatternTextField createNumbersSeparatedBySemicolonsTextField()
  {
    return new PatternTextField(new IntegersSeparatedBySemicolonValidator());
  }
  
  /**
   * Returns a textfield which allows text only.
   * @return textfield
   */
  public static PatternTextField createTextOnlyTextField()
  {
    return new PatternTextField(new TextOnlyValidator());
  }
  
  /**
   * Returns a textfield which allows everything.
   * @return textfield
   */
  public static PatternTextField createAcceptAllTextField()
  {
    return new PatternTextField(new AcceptAllValidator());
  }
  
  private IUserInputValidator validator;
  
  private PatternTextField(IUserInputValidator validator)
  {
    super();
    this.validator = Check.notNull(validator, "validator");
    
    addAllowedPatternListener();
  }

  private void addAllowedPatternListener()
  {
    textProperty().addListener((observable, oldValue, newValue) -> {
      newValue = validator.replaceInput(newValue);
      if(validator.isValid(newValue))
      {
        setText(newValue);
      }
      else
      {
        setText(oldValue);
      }
    });
  }
}
