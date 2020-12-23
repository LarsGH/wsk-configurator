package com.lasy.dwbk.app;

import java.io.InputStream;
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
  
  private static boolean isRunning = false;
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
      initialize(path);
    }
    return instance;
  }

  public static void initialize(String path)
  {
    try
    {
      DwbkLog.log(Level.INFO, "Framework wird mit folgenden Umgebungsvariablen gestartet: %n%s", DwbkEnvironment.getConfiguredInfo());
      DwbkGeoPackage tmGpkg = DwbkGeoPackage.createForPath(path);
      instance = new DwbkFramework(tmGpkg);
    }
    catch (Exception e)
    {
      throw DwbkFrameworkException.failForReason(null, "Geopackage konnte nicht geladen werden!");
    }
    isRunning = true;
  }
  
  private DwbkGeoPackage gpkg;
  
  private DwbkFramework(DwbkGeoPackage gpkg)
  {
    this.gpkg = Check.notNull(gpkg, "gpkg");
    init();
  }

  private void init()
  {
    createConfigTables(gpkg);
    DwbkServiceProvider.initialize(gpkg.getDataStore());
    DwbkLog.log(Level.INFO, "Framwork erfolgreich initialisiert!");
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
      Connection connection = gpkg.getGeoPackage().getDataSource().getConnection();
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
  public DwbkGeoPackage getGeoPackage()
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
    return isRunning;
  }
  
}
