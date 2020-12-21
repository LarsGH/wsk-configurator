package com.lasy.dwbk.db;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.geopkg.GeoPackage;
import org.geotools.geopkg.GeoPkgDataStoreFactory;

import com.lasy.dwbk.util.Check;

/**
 * Access to the project GPKG file.
 * @author larss
 */ 
public class DwbkGeoPackage implements AutoCloseable
{
  private static Logger LOG = Logger.getLogger(DwbkGeoPackage.class.getSimpleName());
  private static final String DB_FILE_NAME = "dwbk_db.gpkg";

  /**
   * Creates a new {@link DwbkGeoPackage} in the provided directory.
   * @param directoryPath where the geopackage will be created
   * @return TmGeoPackage
   */
  public static DwbkGeoPackage createForPath(String directoryPath)
  {
    File dir = new File(directoryPath);
    if ( !dir.isDirectory())
    {
      String msg = String.format("No directory! Was: &s", directoryPath);
      throw new IllegalStateException(msg);
    }

    File f = new File(dir, DB_FILE_NAME);

    try
    {
      if(f.createNewFile())
      {
        String msg = String.format("Created new geopackage: %s", f.getAbsolutePath());
        LOG.log(Level.INFO, msg);
      }
    }
    catch (IOException e)
    {
      String msg = String.format("Geopackage creation failed: %s", f.getAbsoluteFile());
      throw new IllegalStateException(msg, e);
    }
    
    return new DwbkGeoPackage(f);
  }

  private GeoPackage gpkg;
  DataStore dataStore;

  private DwbkGeoPackage(File gpkgFile)
  {
    Check.notNull(gpkgFile, "gpkgFile");
    try
    {
      gpkg = new GeoPackage(gpkgFile);
      gpkg.init();
      
      dataStore = createDatastore(gpkg);
    }
    catch (IOException e)
    {
      String msg = String.format("Failed to create GT Geopackage from file: %s", gpkgFile.getAbsoluteFile());
      throw new IllegalStateException(msg, e);
    }
    
    String msg = String.format("Opened geopackage: %s", gpkgFile.getAbsolutePath());
    LOG.log(Level.INFO, msg);
  }

  private DataStore createDatastore(GeoPackage gpkg)
  {
    try
    {
      HashMap<String, Object> params = createConnectionParams(gpkg);
      
      DataStore store = DataStoreFinder.getDataStore(params);
      return store;
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Datastore not found!", e);
    }
  }

  private HashMap<String, Object> createConnectionParams(GeoPackage gpkg)
  {
    HashMap<String, Object> params = new HashMap<>();
    params.put(GeoPkgDataStoreFactory.DBTYPE.key, "geopkg");
    params.put(GeoPkgDataStoreFactory.DATABASE.key, gpkg.getFile().getAbsolutePath());
    params.put(GeoPkgDataStoreFactory.EXPOSE_PK.key, true);
//    params.put(GeoPkgDataStoreFactory.MAXCONN.key, 1);
    return params;
  }

  /**
   * Returns the GeoPackage.
   * @return GeoPackage
   */
  public GeoPackage getGeoPackage()
  {
    return gpkg;
  }
  
  /**
   * Returns the GT datastore.
   * @return GT datastore
   */
  public DataStore getDataStore()
  {
    return dataStore;
  }
  
  /**
   * Returns a list of available table names.
   * @return list of available table names
   */
  public List<String> getAvailableTableNames()
  {
    try
    {
      String[] typeNames = getDataStore().getTypeNames();
      return List.of(typeNames);
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Cannot read available table names.", e);
    }
  }

  @Override
  public void close() throws Exception
  {
    if(dataStore != null)
    {
      dataStore.dispose();
    }
    if(gpkg != null)
    {
      gpkg.close();
    }
  }
}
