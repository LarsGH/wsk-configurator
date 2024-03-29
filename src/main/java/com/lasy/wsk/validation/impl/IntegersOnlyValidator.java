package com.lasy.wsk.validation.impl;

import com.lasy.wsk.validation.AUserInputValidator;

/**
 * Validates that just numbers are allowed.
 * @author larss
 *
 */
public class IntegersOnlyValidator extends AUserInputValidator
{
  /**
   * Pattern for numbers only.
   */
  private final static String NUMBERS_ONLY_PATTERN = "\\d*";
  
  /**
   * Allows text input only. Numbers are not allowed!
   */
  public IntegersOnlyValidator()
  {
    super(NUMBERS_ONLY_PATTERN);
  }

}
