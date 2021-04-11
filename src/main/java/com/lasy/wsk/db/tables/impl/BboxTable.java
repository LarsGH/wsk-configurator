package com.lasy.wsk.db.tables.impl;

import com.lasy.wsk.db.tables.AWskTable;

/**
 * Defines the table for layer definitions.
 * @author larss
 */
public class BboxTable extends AWskTable
{
  public static final String TABLE_NAME = "wsk_config_bbox";

  public static final String COL_MIN_LON = "min_lon";
  public static final String COL_MIN_LAT = "min_lat";

  public static final String COL_MAX_LON = "max_lon";
  public static final String COL_MAX_LAT = "max_lat";
  
  public static final String EPSG = "epsg";
  public static final String COL_IS_MAP_BOUNDARY = "is_map_boundary";

  public BboxTable()
  {
    super(TABLE_NAME);
  }

}
