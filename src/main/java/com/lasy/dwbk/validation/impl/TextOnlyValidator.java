package com.lasy.dwbk.validation.impl;

import com.lasy.dwbk.validation.AUserInputValidator;

/**
 * Validates that numbers are not allowed.
 * @author larss
 *
 */
public class TextOnlyValidator extends AUserInputValidator
{
  /**
   * Pattern for text only. "\D" is a non digit!
   */
  private final static String TEXT_ONLY_PATTERN = "\\D*";
  
  /**
   * Allows text input only. Numbers are not allowed!
   */
  public TextOnlyValidator()
  {
    super(TEXT_ONLY_PATTERN);
  }
}
