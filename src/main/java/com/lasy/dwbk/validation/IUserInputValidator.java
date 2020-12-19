package com.lasy.dwbk.validation;

/**
 * Validates user input. 
 * @author larss
 */
@FunctionalInterface
public interface IUserInputValidator
{

  /**
   * Returns if the input is valid.
   * @param input the user input
   * @return {@code true} if the input is valid.
   */
  boolean isValid(String input);
}
