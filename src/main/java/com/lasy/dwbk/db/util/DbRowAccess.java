package com.lasy.dwbk.db.util;

import org.opengis.feature.simple.SimpleFeature;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class DbRowAccess
{
  /**
   * Returns the casted value from the result set. 
   * If the value is NULL or cannot be casted then {@code null} is returned.
   * 
   * @param <T> the cast type
   * @param feature attribute row
   * @param columnLabel the column
   * @param type cast type
   * @return casted value or {@code null}.
   */
  public static <T> T getValueElseNull(SimpleFeature feature, String columnLabel, Class<T> type)
  {
    Preconditions.checkNotNull(feature);
    Preconditions.checkArgument(!Strings.isNullOrEmpty(columnLabel));
    Preconditions.checkNotNull(type);
    
    try
    {
      Object value = feature.getAttribute(columnLabel); 
      return type.cast(value);
    }
    catch (Exception e)
    {
      return null;
    }
  }
  
  /**
   * Returns the casted value from the result set.
   * 
   * @param <T> the cast type
   * @param feature attribute row
   * @param columnLabel the column
   * @param type cast type
   * @throws IllegalStateException If the value cannot be casted for any reason
   * @return casted value
   */
  public static <T> T getMandatoryValue(SimpleFeature feature, String columnLabel, Class<T> type) throws IllegalStateException
  {
    Preconditions.checkNotNull(feature);
    Preconditions.checkArgument(!Strings.isNullOrEmpty(columnLabel));
    Preconditions.checkNotNull(type);
    
    Object value = feature.getAttribute(columnLabel);
    if(value == null)
    {
      String msg = String.format("Mandatory column '%s' is null.", columnLabel);
      throw new IllegalStateException(msg);
    }
    
    try {
      return type.cast(value);
    }
    catch (Exception e)
    {
      String msg = String.format("Mandatory column '%s' did not match expected type '%s'.", columnLabel, type.getSimpleName());
      throw new IllegalStateException(msg, e);
    }
  }
}
