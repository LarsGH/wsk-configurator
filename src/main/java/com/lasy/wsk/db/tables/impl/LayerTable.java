package com.lasy.wsk.db.tables.impl;

import com.lasy.wsk.db.tables.AWskTable;

/**
 * Defines the table for layer definitions.
 * @author larss
 */
public class LayerTable  extends AWskTable
{
  public static final String TABLE_NAME = "wsk_config_layer";
  
  public static final String COL_REQUEST = "request";
  public static final String COL_STORE_LOCAL = "store_local";
  public static final String COL_LAST_DL = "last_dl";
  public static final String COL_IS_VISIBLE = "is_visible";

  public static final String COL_USER = "user";
  public static final String COL_PW = "pw";
  
  public static final String COL_LOCAL_NAME = "local_name";
  
  public static final String COL_SERVICE = "service";
  public static final String COL_SERVICE_CONFIG = "service_config";
  
  public static final String COL_BBOX_ID = "bbox_id";
  
  public LayerTable()
  {
    super(TABLE_NAME);
  }
  
}
