package com.lasy.wsk.db.util;

import java.util.Objects;

/**
 * Handle boolean parsing from / to database.
 * 
 * @author larss
 *
 */
public class DbBoolean
{
  protected static final String YES = "Y";
  protected static final String NO = "N";

  /**
   * Returns the boolean String representing the boolean.
   * 
   * @param bool boolean value
   * @return "Y" for {@code true} or "N" for {@code false}
   */
  public static String toDbValue(boolean bool)
  {
    if(bool)
    {
      return YES;
    }
    return NO;
  }
  
  /**
   * Returns the boolean represented by the input string.
   * 
   * @param dbBool the boolean as String (should be 'Y' or 'N')
   * @return {@code true} if the input is "Y", else {@code false}
   */
  public static boolean fromDbValue(String dbBool)
  {
    return Objects.equals(dbBool, YES);
  }
  
}
