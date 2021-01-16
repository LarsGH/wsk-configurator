package com.lasy.dwbk.db.tables.impl;

import com.lasy.dwbk.db.tables.ADwbkTable;

/**
 * Defines the table for layer definitions.
 * @author larss
 */
public class LayerTable  extends ADwbkTable
{
  public static final String TABLE_NAME = "dwbk_config_layer";
  
  public static final String COL_URI = "uri";
  
  /**
   * boolean column to determine if the layer was saved after last change (important for store local layers).
   */
  public static final String COL_IS_SAVED = "is_saved";
  public static final String COL_STORE_LOCAL = "store_local";
  public static final String COL_LAST_DL = "last_dl";
  public static final String COL_IS_VISIBLE = "is_visible";

  public static final String COL_USER = "user";
  public static final String COL_PW = "pw";
  
  public static final String COL_PIXEL_METERS = "pixel_meters";
  public static final String COL_LOCAL_NAME = "local_name";
  public static final String COL_QUERY_PARTS = "query_parts";
  
  public static final String COL_BBOX_ID = "bbox_id";
  
  public LayerTable()
  {
    super(TABLE_NAME);
  }
  
}
