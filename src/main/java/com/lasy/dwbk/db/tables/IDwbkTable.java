package com.lasy.dwbk.db.tables;

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
  String COL_ID = "fid";
  
  /**
   * Returns the table name.
   * @return table name
   */
  String getTableName();
  
  
  /**
   * Returns the GT simple feature type.
   * @return simple feature type
   */
  SimpleFeatureType getSimpleFeatureType();
}
