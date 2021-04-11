package com.lasy.wsk.validation;

/**
 * Validates user input. 
 * @author larss
 */
public interface IUserInputValidator
{

  /**
   * Returns if the input is valid.
   * @param input the user input
   * @return {@code true} if the input is valid.
   */
  boolean isValid(String input);
  
  /**
   * Replaces (parts of) the input if necessary.
   * @param input the user input
   * @return the replaced input.
   */
  String replaceInput(String input);
}
