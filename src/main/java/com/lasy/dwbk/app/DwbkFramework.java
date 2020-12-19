package com.lasy.dwbk.app;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.data.DataStore;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geopkg.FeatureEntry;

import com.lasy.dwbk.db.DwbkGeoPackage;
import com.lasy.dwbk.db.tables.BboxTable;
import com.lasy.dwbk.db.tables.IDwbkTable;
import com.lasy.dwbk.db.tables.LayerTable;
import com.lasy.dwbk.util.Check;
import com.lasy.dwbk.util.Is;

/**
 * The framework makes sure that the {@link DwbkGeoPackage} exists with the basic configuration tables.
 * It also grants access by initializing the {@link DwbkServiceProvider}.
 * @author larss
 */
public class DwbkFramework implements AutoCloseable
{
  private static Logger LOG = Logger.getLogger(DwbkFramework.class.getSimpleName());
  
  private static boolean isRunning = false;
  private static DwbkFramework instance;
  
  private static final String DB_FILE_DIRECTORY_ENV_NAME = "DWBK_DB_DIR";
  private static String getPathFromEnv()
  {
    String path = System.getenv(DB_FILE_DIRECTORY_ENV_NAME);

    if (Is.nullOrTrimmedEmpty(path))
    {
      String msg = String.format("Environment variable '%s' needs to be set!", DB_FILE_DIRECTORY_ENV_NAME);
      throw new IllegalStateException(msg);
    }
    return path;
  }
  
  /**
   * Returns the framework.
   * @return framework
   */
  public static DwbkFramework getInstance()
  {
    if(instance == null)
    {
      // Use environment variable to initialize framework (if not initialized before)
      String path = getPathFromEnv();
      initialize(path);
    }
    return instance;
  }

  public static void initialize(String path)
  {
    DwbkGeoPackage tmGpkg = DwbkGeoPackage.createForPath(path);
    
    instance = new DwbkFramework(tmGpkg);
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
    LOG.log(Level.INFO, "Framwork initialized successfully!");
  }
  
  private void createConfigTables(DwbkGeoPackage gpkg)
  {
    List<String> availableTableNames = gpkg.getAvailableTableNames();
    if ( !availableTableNames.contains(LayerTable.TABLE_NAME))
    {
      LayerTable table = new LayerTable();
      createConfigTable(gpkg.getDataStore(), table);
    }

    if ( !availableTableNames.contains(BboxTable.TABLE_NAME))
    {
      BboxTable table = new BboxTable();
      createConfigTable(gpkg.getDataStore(), table);
    }
  }

  private void createConfigTable(DataStore store, IDwbkTable table)
  {
    try
    {
      FeatureEntry entry = new FeatureEntry();
      // geo attributes do not matter for config tables (but are mandatory)
      entry.setBounds(ReferencedEnvelope.EVERYTHING);
      entry.setSrid(4326);
      store.createSchema(table.getSimpleFeatureType());
    }
    catch (IOException e)
    {
      String msg = String.format("Failed creating config table '%s'!", table.getTableName());
      LOG.log(Level.INFO, msg);
    }
    String msg = String.format("Created config table '%s'!", table.getTableName());
    LOG.log(Level.INFO, msg);
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
