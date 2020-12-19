package com.lasy.dwbk.util;

/**
 * Class for validation. Each method is supposed to return a boolean.
 * @author larss
 */
public class Is
{
  
  /**
   * Returns if the string is empty when trimmed.
   * @param str the string
   * @return {@code true} if the string is empty when trimmed
   */
  public static boolean nullOrTrimmedEmpty(String str)
  {
    return str == null
      || str.trim().isEmpty();
  }
}
