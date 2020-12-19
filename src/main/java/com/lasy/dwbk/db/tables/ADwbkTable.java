package com.lasy.dwbk.db.tables;

import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeatureType;

import com.lasy.dwbk.util.Check;

public abstract class ADwbkTable implements IDwbkTable
{

  private String tableName;

  protected ADwbkTable(String tableName)
  {
    this.tableName = Check.trimmedNotEmpty(tableName, "tableName");
  }
  
  @Override
  public String getTableName()
  {
    return tableName;
  }
  
  @Override
  public SimpleFeatureType getSimpleFeatureType()
  {
    SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
    builder.setName(getTableName());
    builder.setCRS(DefaultGeographicCRS.WGS84);
    
    addStandardColumns(builder);
    doAddSpecificTableColumns(builder);
   
    SimpleFeatureType featureType = builder.buildFeatureType();
    return featureType;
  }

  private void addStandardColumns(SimpleFeatureTypeBuilder builder)
  {
    builder.nillable(false);
    builder.add(COL_NAME, String.class);
    
    builder.nillable(true);
    builder.add(COL_DESCRIPTION, String.class);
    
    addDateColumn(builder, COL_LAST_CHANGED);
  }
  
  protected void addBooleanColumn(SimpleFeatureTypeBuilder builder, String columnName)
  {
    // TODO: String Laenge 1 funktioniert nicht!
    // Store local boolean as 'Y' / 'N'
    builder.nillable(false);
    builder.length(1);
    builder.add(columnName, String.class);
  }
  
  protected void addDateColumn(SimpleFeatureTypeBuilder builder, String columnName)
  {
    builder.nillable(false);
    // TODO: String Laenge validieren!
    final int dateColumnLength = "2007-12-03T10:15:30".length();
    builder.length(dateColumnLength);
    builder.add(columnName, String.class);
  }

  /**
   * Creates specific columns for a table / feature.
   * @param builder the builder
   */
  protected abstract void doAddSpecificTableColumns(SimpleFeatureTypeBuilder builder);
}
