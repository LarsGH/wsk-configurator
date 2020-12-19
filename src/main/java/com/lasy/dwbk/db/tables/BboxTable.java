package com.lasy.dwbk.db.tables;

import org.geotools.feature.simple.SimpleFeatureTypeBuilder;

/**
 * Defines the table for layer definitions.
 * @author larss
 */
public class BboxTable extends ADwbkTable
{
  public static final String TABLE_NAME = "dwbk_config_bbox";

  public static final String COL_MIN_LON = "min_lon";
  public static final String COL_MIN_LAT = "min_lat";

  public static final String COL_MAX_LON = "max_lon";
  public static final String COL_MAX_LAT = "max_lat";

  public BboxTable()
  {
    super(TABLE_NAME);
  }

  @Override
  protected void doAddSpecificTableColumns(SimpleFeatureTypeBuilder builder)
  {
    builder.nillable(false);
    builder.add(COL_MIN_LON, String.class);

    builder.nillable(false);
    builder.add(COL_MIN_LAT, String.class);

    builder.nillable(false);
    builder.add(COL_MAX_LON, String.class);

    builder.nillable(false);
    builder.add(COL_MAX_LAT, String.class);
  }

}
