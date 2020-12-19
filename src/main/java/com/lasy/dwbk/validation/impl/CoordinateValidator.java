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
  private static final String COORD_PATTERN = "^[-]?\\d+(\\.\\d+)?";
  
  /**
   * Allows positive / negative numbers with an optional decimal point.
   */
  public CoordinateValidator()
  {
    super(COORD_PATTERN);
  }
}
