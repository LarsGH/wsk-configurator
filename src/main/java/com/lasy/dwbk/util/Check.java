package com.lasy.dwbk.util;

import com.lasy.dwbk.validation.impl.CoordinateValidator;

/**
 * Check preconditions. Throws {@link IllegalArgumentException} if conditions are not met.
 * @author larss
 *
 */
public class Check
{
  /**
   * Checks if the String is not null and trimmed not empty.
   * @param str String to check
   * @param attrName attribute name
   * @return the input string
   */
  public static String trimmedNotEmpty(String str, String attrName)
  {
    if(Is.nullOrTrimmedEmpty(str))
    {
      String msg = String.format("'%s' is null or (trimmed) empty!", attrName);
      throw new IllegalArgumentException(msg);
    }
    return str;
  }
  
  /**
   * Checks if a value is null.
   * @param <TType> the type
   * @param val the object to check
   * @param attrName attribute name
   * @return the input object
   */
  public static <TType> TType notNull(TType val, String attrName)
  {
    if(val == null)
    {
      String msg = String.format("'%s' is null!", attrName);
      throw new IllegalArgumentException(msg);
    }
    return val;
  }
  
  /**
   * Checks if a coordinate is valid.
   * @param coord the input coordinate
   * @param attrName attribute name
   * @return the coordinate
   */
  public static String validCoordinate(String coord, String attrName)
  {
    CoordinateValidator validator = new CoordinateValidator();
    if(!validator.isValid(coord))
    {
      String msg = String.format("'%s' is not a valid coordinate!", attrName);
      throw new IllegalArgumentException(msg);
    }
    return coord.trim();
  }
}
