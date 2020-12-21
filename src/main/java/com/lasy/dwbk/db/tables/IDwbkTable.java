package com.lasy.dwbk.db.tables;

import java.io.InputStream;

import org.opengis.feature.simple.SimpleFeatureType;

/**
 * Common interface for all config tables.
 * @author larss
 */
public interface IDwbkTable
{
  /**
   * Default ID column name.
   */
  String COL_ID = "id";
  
  /**
   * Standard name column name.
   */
  String COL_NAME = "name";
  
  /**
   * Standard description column name.
   */
  String COL_DESCRIPTION = "description";
  
  /**
   * Standard last changed column name.
   */
  String COL_LAST_CHANGED = "last_changed";
  
  /**
   * Returns the table name.
   * @return table name
   */
  String getTableName();
  
  /**
   * Returns the InputStream for the table create resource file.
   * @return InputStream for the table create resource file
   */
  InputStream getCreateScriptInputStream();
  
  
  /**
   * Returns the GT simple feature type.
   * @return simple feature type
   */
  SimpleFeatureType getSimpleFeatureType();
}
