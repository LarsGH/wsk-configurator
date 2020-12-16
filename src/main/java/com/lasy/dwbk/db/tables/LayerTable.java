package com.lasy.dwbk.db.tables;

import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeatureType;

// TODO: LayerDefinitionTable + BboxTable als Config-File separat speichern?!
/**
 * Defines the table for layer definitions.
 * @author larss
 */
public class LayerTable implements IDwbkTable
{
  public static final String TABLE_NAME = "tm_config_layer";

  // TODO: FOREIGN KEY?!
  private static final String FK_LAYER_BBOX = "FK_LAYER_BBOX";
  
  public static final String COL_NAME = "name";
  
  public static final String COL_URI = "uri";
  
  public static final String COL_STORE_LOCAL = "store_local";
  
  public static final String COL_BBOX_ID = "bbox_id";
  
  public static final String COL_USER = "user";
  
  public static final String COL_PW = "pw";

  @Override
  public String getTableName()
  {
    return TABLE_NAME;
  }

  @Override
  public SimpleFeatureType getSimpleFeatureType()
  {
    SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
    builder.setName(TABLE_NAME);
    builder.setCRS(DefaultGeographicCRS.WGS84);
    
    builder.nillable(false);
    builder.add(COL_NAME, String.class);
    
    builder.nillable(false);
    builder.add(COL_URI, String.class);
    
    // TODO: String Länge 1 funktioniert nicht!
    // Store local boolean as 'Y' / 'N'
    builder.nillable(false);
    builder.length(1);
    builder.add(COL_STORE_LOCAL, String.class);
    
    builder.add(COL_BBOX_ID, Integer.class);
    builder.add(COL_USER, String.class);
    builder.add(COL_PW, String.class);
    
    SimpleFeatureType type = builder.buildFeatureType();
    return type;
  }
}
