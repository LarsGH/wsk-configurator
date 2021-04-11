package com.lasy.wsk.validation.impl;

import com.lasy.wsk.validation.AUserInputValidator;

/**
 * Validates that just integers separated by semicolons are allowed.
 * @author larss
 *
 */
public class IntegersSeparatedBySemicolonValidator extends AUserInputValidator
{
  /**
   * Pattern for integers separated by semicolon.
   */
  private final static String INTS_SEPARATED_BY_SEMICOLON_PATTERN = "([0-9]+;?)*";
  
  /**
   * Allows text input only. Numbers are not allowed!
   */
  public IntegersSeparatedBySemicolonValidator()
  {
    super(INTS_SEPARATED_BY_SEMICOLON_PATTERN);
  }

}