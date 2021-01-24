package com.lasy.dwbk.app;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;

import org.geotools.jdbc.util.SqlUtil;

import com.lasy.dwbk.app.error.DwbkFrameworkException;
import com.lasy.dwbk.app.logging.DwbkLog;
import com.lasy.dwbk.db.DwbkGeoPackage;
import com.lasy.dwbk.db.tables.IDwbkTable;
import com.lasy.dwbk.db.tables.impl.BboxTable;
import com.lasy.dwbk.db.tables.impl.LayerTable;
import com.lasy.dwbk.util.Check;

/**
 * The framework makes sure that the {@link DwbkGeoPackage} exists with the basic configuration tables.
 * It also grants access by initializing the {@link DwbkServiceProvider}.
 * @author larss
 */
public class DwbkFramework implements AutoCloseable
{
  
  private static DwbkFramework instance;
  
  /**
   * Returns the framework.
   * @return framework
   */
  public static DwbkFramework getInstance()
  {
    if(instance == null)
    {
      // Use environment variable to initialize framework (if not initialized before)
      String path = DwbkEnvironment.getConfigDirectory().getAbsolutePath();
      instance = initializeFramework(path);
    }
    return instance;
  }

  public static DwbkFramework initializeFramework(String path)
  {
    instance = new DwbkFramework(path);
    return instance;
  }
  
  private DwbkGeoPackage gpkg;
  private boolean isRunning = false;
  private DwbkSettings settings;
  
  private DwbkFramework(String configDirectoryPath)
  {
    Check.trimmedNotEmpty(configDirectoryPath, "configDirectoryPath");
    
    this.settings = getValidatedSettings();
    this.gpkg = DwbkGeoPackage.createForPath(configDirectoryPath);
    
    init();
  }
  
  private DwbkSettings getValidatedSettings()
  {
    File settingFile = new File(DwbkEnvironment.getConfigDirectory(), DwbkSettings.SETTINGS_FILE_NAME);
    DwbkSettings settings = createSettings(settingFile);
    
    try(FileWriter writer = new FileWriter(settingFile, StandardCharsets.UTF_8))
    {
      // write current settings (may have changed with new attributes)
      writer.write(settings.toString());
      return settings;
    }
    catch (Exception e)
    {
      throw DwbkFrameworkException.failForReason(e, "Die Settings ('%s') konnten nicht geschrieben werden!", DwbkSettings.SETTINGS_FILE_NAME);
    }
  }

  private DwbkSettings createSettings(File settingFile)
  {
    if(settingFile.exists())
    {
      try
      {
        String settingFileContent = Files.readString(settingFile.toPath(), StandardCharsets.UTF_8);
        return DwbkSettings.createFromFileContent(settingFileContent);
      }
      catch (Exception e)
      {
        throw DwbkFrameworkException.failForReason(e, "Die Settings ('%s') konnten nicht geladen werden!", DwbkSettings.SETTINGS_FILE_NAME);
      }
    }
    else
    {
      // create new settings with default values
      return new DwbkSettings();
    }
  }

  private void init()
  {    
    createConfigTables(gpkg);
    DwbkServiceProvider.initialize(gpkg.getDataStore());
    DwbkLog.log(Level.INFO, "Framwork erfolgreich initialisiert!");
    
    // Use configured log-level after init!
    DwbkLog.getInstance().setLogLevel(this.settings.getLogLevel());
    this.isRunning = true;
  }
  
  private void createConfigTables(DwbkGeoPackage gpkg)
  {
    List<String> availableTableNames = gpkg.getAvailableTableNames();
    if ( !availableTableNames.contains(BboxTable.TABLE_NAME))
    {
      BboxTable table = new BboxTable();
      createConfigTable(gpkg, table);
    }
    
    if ( !availableTableNames.contains(LayerTable.TABLE_NAME))
    {
      LayerTable table = new LayerTable();
      createConfigTable(gpkg, table);
    }
  }

  private void createConfigTable(DwbkGeoPackage gpkg, IDwbkTable table)
  {
    try
    {
      InputStream inputStream = table.getCreateScriptInputStream();
      Connection connection = gpkg.getGtGeoPackage().getDataSource().getConnection();
      SqlUtil.runScript(inputStream, connection);
    }
    catch (Exception e)
    {
      throw DwbkFrameworkException.failForReason(e, 
        "Zwingend erforderliche Konfigurationstabelle '%s' konnte nicht erstellt werden!", 
        table.getTableName());
    }
    String msg = String.format("Created config table '%s'!", table.getTableName());
    DwbkLog.log(Level.INFO, msg);
  }

  /**
   * Returns the geopackage.
   * @return geopackage
   */
  public DwbkGeoPackage getDwbkGeoPackage()
  {
    return this.gpkg;
  }

  @Override
  public void close() throws Exception
  {
    this.gpkg.close();
    DwbkLog.getInstance().close();
    isRunning = false;
  }
  
  /**
   * Returns if the framework is running.
   * @return {@code true} if the framework is running.
   */
  public static boolean isRunning()
  {
    return instance == null
      ? false
      : instance.isRunning;
  }
  
  /**
   * Returns the settings.
   * @return settings
   */
  public DwbkSettings getSettings()
  {
    return this.settings;
  }
  
}
