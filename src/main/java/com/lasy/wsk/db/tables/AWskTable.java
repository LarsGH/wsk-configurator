package com.lasy.wsk.db.tables;

import java.io.IOException;
import java.io.InputStream;

import org.opengis.feature.simple.SimpleFeatureType;

import com.lasy.wsk.app.WskFramework;
import com.lasy.wsk.app.error.WskFrameworkException;
import com.lasy.wsk.util.Check;

public abstract class AWskTable implements IWskTable
{

  private String tableName;

  protected AWskTable(String tableName)
  {
    this.tableName = Check.trimmedNotEmpty(tableName, "tableName");
  }
  
  @Override
  public String getTableName()
  {
    return tableName;
  }
  
  protected String getCreateTableFileName()
  {
    return String.format("create_%s.sql", getTableName());
  }
  
  @Override
  public InputStream getCreateScriptInputStream()
  {
    return getClass().getResourceAsStream(getCreateTableFileName());
  }
  
  @Override
  public SimpleFeatureType getSimpleFeatureType()
  {
    try
    {
      return WskFramework.getInstance().getWskGeoPackage().getDataStore().getSchema(getTableName());
    }
    catch (IOException e)
    {
      // could not access table schema
      throw WskFrameworkException.failForReason(null, "Auf das Schema der Tabelle '%s' kann nicht zugegriffen werden!", getTableName());
    }
  }
  
}
