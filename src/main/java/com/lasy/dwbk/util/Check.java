package com.lasy.dwbk.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

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
  
  /** The maximum number of decimal digits for coordinates. */
  private static final int MAX_COORD_PRECISION = 6;
  
  /**
   * Checks if a coordinate is valid.
   * @param coord the input coordinate
   * @param attrName attribute name
   * @return the coordinate
   */
  public static String validCoordinate(String coord, String attrName)
  {
    try
    {
      BigDecimal num = new BigDecimal(coord);
      num = num.setScale(MAX_COORD_PRECISION, RoundingMode.HALF_UP);
      return num.toPlainString();
    }
    catch (Exception e)
    {
      String msg = String.format("'%s' is not a valid coordinate! Was: %s", attrName, coord);
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Checks if the string contains integers separated by semicolons. 
   * @param intString string of integers separated by semicolons
   * @param attrName attribute name
   * @return the string with integers separated by semicolons without duplicates and in decreasing order.
   */
  public static int[] numbersSeparatedBySemicolons(String intString, String attrName)
  {
    if(Is.nullOrTrimmedEmpty(intString))
    {
      return null;
    }
    try
    {
      return Stream.of(intString.split(";"))
        .mapToInt(str -> Integer.valueOf(str))
        .distinct()
        .sorted()
        .toArray();
    } catch (Exception e)
    {
      String msg = String.format("'%s' is not a valid list of numbers separated by ';'. Was: %s", attrName, intString);
      throw new IllegalArgumentException(msg);
    }
  }

  /**
   * Checks if the value is a valid RGB value.
   * @param val the RGB value
   * @return {@code true} if the value is a valid RGB value
   */
  public static int validRgbValue(int val)
  {
    if(!isValidRgbValue(val))
    {
      String msg = String.format("%s is not a valid RGB value (0-255).", val);
      throw new IllegalArgumentException(msg);
    }
    return val;
  }
  
  /**
   * Checks if the value is a valid alpha value.
   * @param val the alpha value
   * @return {@code true} if the value is a valid alpha value
   */
  public static int validAplhaValue(int val)
  {
    if(!isValidAplhaValue(val))
    {
      String msg = String.format("%s is not a valid alpha value (0-100).", val);
      throw new IllegalArgumentException(msg);
    }
    return val;
  }
  
  private static final int RGBA_MIN = 0;
  private static final int RGB_MAX = 255;
  private static final int ALPHA_MAX = 100;
  
  /**
   * Returns {@code true}, if the RGB value is valid.
   * @param rgbVal value for red, green or blue
   * @return {@code true}, if the RGB value is valid
   */
  private static boolean isValidRgbValue(int rgbVal)
  {
    return rgbVal >= RGBA_MIN && rgbVal <= RGB_MAX;
  }
  
  /**
   * Returns {@code true}, if the alpha value is valid.
   * @param alphaVal alpha value
   * @return {@code true}, if the alpha value is valid
   */
  private static boolean isValidAplhaValue(int alphaVal)
  {
    return alphaVal >= RGBA_MIN && alphaVal <= ALPHA_MAX;
  }
}
