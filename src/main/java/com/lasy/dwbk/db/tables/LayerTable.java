package com.lasy.dwbk.db.tables;

import org.geotools.feature.simple.SimpleFeatureTypeBuilder;

// TODO: LayerDefinitionTable + BboxTable als Config-File separat speichern?!
/**
 * Defines the table for layer definitions.
 * @author larss
 */
public class LayerTable  extends ADwbkTable
{
  public static final String TABLE_NAME = "dwbk_config_layer";

  // TODO: FOREIGN KEY?!
  private static final String FK_LAYER_BBOX = "FK_LAYER_BBOX";
  
  public static final String COL_URI = "uri";
  
  public static final String COL_STORE_LOCAL = "store_local";
  
  public static final String COL_BBOX_ID = "bbox_id";
  
  public static final String COL_USER = "user";
  
  public static final String COL_PW = "pw";
  
  /**
   * boolean column to determine if the layer was saved after last change (important for store local layers).
   */
  public static final String COL_IS_SAVED = "is_saved";

  public LayerTable()
  {
    super(TABLE_NAME);
  }
  
  @Override
  protected void doAddSpecificTableColumns(SimpleFeatureTypeBuilder builder)
  {
    addBooleanColumn(builder, COL_IS_SAVED);
    addBooleanColumn(builder, COL_STORE_LOCAL);
    
    builder.nillable(false);
    builder.add(COL_URI, String.class);
    
    builder.add(COL_BBOX_ID, Integer.class);
    builder.add(COL_USER, String.class);
    builder.add(COL_PW, String.class);
  }
}
