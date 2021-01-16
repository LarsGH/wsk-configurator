package com.lasy.dwbk.gui.util;

import com.lasy.dwbk.util.Check;
import com.lasy.dwbk.validation.IUserInputValidator;
import com.lasy.dwbk.validation.impl.AcceptAllValidator;
import com.lasy.dwbk.validation.impl.CoordinateValidator;
import com.lasy.dwbk.validation.impl.IntegersOnlyValidator;
import com.lasy.dwbk.validation.impl.IntegersSeparatedBySemicolonValidator;
import com.lasy.dwbk.validation.impl.TextOnlyValidator;

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
