package com.lasy.wsk.app;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Level;

import org.geotools.jdbc.util.SqlUtil;

import com.lasy.wsk.app.error.WskFrameworkException;
import com.lasy.wsk.app.logging.WskLog;
import com.lasy.wsk.db.WskGeoPackage;
import com.lasy.wsk.db.tables.IWskTable;
import com.lasy.wsk.db.tables.impl.BboxTable;
import com.lasy.wsk.db.tables.impl.LayerTable;
import com.lasy.wsk.util.Check;

/**
 * The framework makes sure that the {@link WskGeoPackage} exists with the basic configuration tables.
 * It also grants access by initializing the {@link WskServiceProvider}.
 * @author larss
 */
public class WskFramework implements AutoCloseable
{
  
  private static WskFramework instance;
  
  /**
   * Returns the framework.
   * @return framework
   */
  public static WskFramework getInstance()
  {
    if(instance == null)
    {
      // Use environment variable to initialize framework (if not initialized before)
      String path = WskEnvironment.getConfigDirectory().getAbsolutePath();
      instance = initializeFramework(path);
    }
    return instance;
  }

  public static WskFramework initializeFramework(String path)
  {
    instance = new WskFramework(path);
    return instance;
  }
  
  private WskGeoPackage gpkg;
  private boolean isRunning = false;
  private WskSettings settings;
  
  private WskFramework(String configDirectoryPath)
  {
    Check.trimmedNotEmpty(configDirectoryPath, "configDirectoryPath");
    
    this.settings = getValidatedSettings();
    this.gpkg = WskGeoPackage.createForPath(configDirectoryPath);
    
    init();
  }
  
  private WskSettings getValidatedSettings()
  {
    File settingFile = new File(WskEnvironment.getConfigDirectory(), WskSettings.SETTINGS_FILE_NAME);
    WskSettings settings = createSettings(settingFile);
    
    try(FileWriter writer = new FileWriter(settingFile, StandardCharsets.UTF_8))
    {
      // write current settings (may have changed with new attributes)
      writer.write(settings.toString());
      return settings;
    }
    catch (Exception e)
    {
      // Writing settings failed
      throw WskFrameworkException.failForReason(e, "Die Settings ('%s') konnten nicht geschrieben werden!", WskSettings.SETTINGS_FILE_NAME);
    }
  }

  private WskSettings createSettings(File settingFile)
  {
    if(settingFile.exists())
    {
      try
      {
        String settingFileContent = Files.readString(settingFile.toPath(), StandardCharsets.UTF_8);
        return WskSettings.createFromFileContent(settingFileContent);
      }
      catch (Exception e)
      {
        // Loading settings failed
        throw WskFrameworkException.failForReason(e, "Die Settings ('%s') konnten nicht geladen werden!", WskSettings.SETTINGS_FILE_NAME);
      }
    }
    else
    {
      // create new settings with default values
      return new WskSettings();
    }
  }

  private void init()
  {    
    createConfigTables(gpkg);
    WskServiceProvider.initialize(gpkg.getDataStore());
    // framework initialization success
    WskLog.log(Level.INFO, "Framwork erfolgreich initialisiert!");
    
    // Use configured log-level after init!
    WskLog.getInstance().setLogLevel(this.settings.getLogLevel());
    this.isRunning = true;
  }
  
  private void createConfigTables(WskGeoPackage gpkg)
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

  private void createConfigTable(WskGeoPackage gpkg, IWskTable table)
  {
    try
    {
      InputStream inputStream = table.getCreateScriptInputStream();
      Connection connection = gpkg.getGtGeoPackage().getDataSource().getConnection();
      SqlUtil.runScript(inputStream, connection);
    }
    catch (Exception e)
    {
      // Mandatory config table could not be created
      throw WskFrameworkException.failForReason(e, 
        "Zwingend erforderliche Konfigurationstabelle '%s' konnte nicht erstellt werden!", 
        table.getTableName());
    }
    String msg = String.format("Created config table '%s'!", table.getTableName());
    WskLog.log(Level.INFO, msg);
  }

  /**
   * Returns the geopackage.
   * @return geopackage
   */
  public WskGeoPackage getWskGeoPackage()
  {
    return this.gpkg;
  }

  @Override
  public void close() throws Exception
  {
    this.gpkg.close();
    WskLog.getInstance().close();
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
  public WskSettings getSettings()
  {
    return this.settings;
  }
  
}
