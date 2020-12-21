package com.lasy.dwbk.validation.impl;

import com.lasy.dwbk.validation.AUserInputValidator;

/**
 * Validates Coordinates.
 * @author larss
 *
 */
public class CoordinateValidator extends AUserInputValidator
{
  /**
   * Coordinate pattern. Allows positive / negative numbers with an optional decimal point.
   */
  private static final String COORD_PATTERN = "^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$";
  
  /**
   * Allows positive / negative numbers with an optional decimal point.
   */
  public CoordinateValidator()
  {
    super(COORD_PATTERN);
  }

  @Override
  public String replaceInput(String input)
  {
    if(input != null)
    {
      input = input.trim();
      
      // always use point as a decimal separator!
      input = input.replace(",", ".");
      
      // coordinates should not start with a point!
      if(input.startsWith("."))
      {
        input = "0" + input; 
      }
    }
    return input;
  }
  
  
}
