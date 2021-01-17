package com.lasy.dwbk.db.tables;

import java.io.IOException;
import java.io.InputStream;

import org.opengis.feature.simple.SimpleFeatureType;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.app.error.DwbkFrameworkException;
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
      return DwbkFramework.getInstance().getDwbkGeoPackage().getDataStore().getSchema(getTableName());
    }
    catch (IOException e)
    {
      throw DwbkFrameworkException.failForReason(null, "Auf das Schema der Tabelle '%s' kann nicht zugegriffen werden!", getTableName());
    }
  }
  
}
