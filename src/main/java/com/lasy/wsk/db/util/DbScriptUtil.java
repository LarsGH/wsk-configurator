package com.lasy.wsk.db.util;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;
import java.util.logging.Level;

import org.geotools.geopkg.FeatureEntry;
import org.geotools.geopkg.GeoPackage;
import org.geotools.geopkg.TileEntry;
import org.geotools.jdbc.util.SqlUtil;

import com.lasy.wsk.app.WskFramework;
import com.lasy.wsk.app.error.WskFrameworkException;
import com.lasy.wsk.app.logging.WskLog;
import com.lasy.wsk.app.model.impl.LayerModel;
import com.lasy.wsk.util.Check;
import com.lasy.wsk.ws.wfs.WfsConfig;

/**
 * Helper to execute useful scripts on the geopackage file.
 * @author larss
 */
public class DbScriptUtil
{
  // Scripts
  /** Delete script for WMS layers */
  private static final String DELETE_WMS_TABLE_SCRIPT = "delete_gen_wms_table.sql";

  /** Delete script for WFS layers */
  private static final String DELETE_WFS_TABLE_SCRIPT = "delete_gen_wfs_table.sql";

  /** Update script for WFS layer names. By default GT uses the WFS service layer name. */
  private static final String UPDATE_WFS_TABLE_NAME_SCRIPT = "update_gen_wfs_table_name.sql";

  // Parameters
  /** The tablename parameter to replace in the script. */
  private static final String TABLENAME_PARAM = "tableName";

  /** The new tablename parameter to replace in the script. */
  private static final String NEW_TABLENAME_PARAM = "newTableName";

  /**
   * Deletes a generated WMS table from the geopackage. Delete affects:
   * <ul>
   * <li>generated table</li>
   * <li>entry in gpkg_contents</li>
   * <li>entry in gpkg_tile_matrix</li>
   * <li>entry in gpkg_tile_matrix_set</li>
   * </ul>
   * @param layer the layer
   */
  public static void deleteLocalWmsLayerContentIfPresent(LayerModel layer)
  {
    String localLayerName = Check.notNull(layer, "layer").getLocalName();
    try
    {
      GeoPackage gpkg = WskFramework.getInstance().getWskGeoPackage().getGtGeoPackage();
      TileEntry tileEntry = gpkg.tile(localLayerName);
      if (tileEntry != null)
      {
        Connection connection = gpkg.getDataSource().getConnection();
        Map<String, String> params = Map.of(TABLENAME_PARAM, localLayerName);
        InputStream is = DbScriptUtil.class.getResourceAsStream(DELETE_WMS_TABLE_SCRIPT);
        SqlUtil.runScript(is, connection, params);
        // offline content of WMS layer successfully deleted
        WskLog.log(Level.INFO, "Offline Content von WMS Layer '%s' wurde erfolgreich gelöscht. (Tabelle: '%s')", layer.getName(), localLayerName);
      }
    }
    catch (Exception e)
    {
      // offline content of WMS layer could not be deleted
      throw WskFrameworkException.failForReason(e, "Aktueller offline Content von WMS Layer '%s' konnte nicht gelöscht werden!", layer.getName());
    }
  }

  /**
   * Deletes a generated WFS table from the geopackage. Delete affects:
   * <ul>
   * <li>generated table</li>
   * <li>entry in gpkg_contents</li>
   * <li>entry in gpkg_geometry_columns</li>
   * </ul>
   * @param layer the layer
   */
  public static void deleteLocalWfsLayerContentIfPresent(LayerModel layer)
  {
    Check.notNull(layer, "layer");
    try
    {
      GeoPackage gpkg = WskFramework.getInstance().getWskGeoPackage().getGtGeoPackage();
      
      // delete content for framework table name
      deleteWfsContentIfPresent(layer.getLocalName(), gpkg);

      // also delete content by service typeNames (necessary for write errors)
      WfsConfig wfsConfig = layer.getWfsConfig();
      String typeNames = wfsConfig.getTypeNames();
      deleteWfsContentIfPresent(typeNames, gpkg);

      // WFS layer does not have offline content
      WskLog.log(Level.INFO, "WFS Layer '%s' hat keinen offline Content.", layer.getName());
    }
    catch (Exception e)
    {
      // current offline content of WFS layer could not be deleted
      throw WskFrameworkException.failForReason(e, "Aktueller offline Content von WFS Layer '%s' konnte nicht gelöscht werden!", layer.getName());
    }
  }

  private static void deleteWfsContentIfPresent(String tableName, GeoPackage gpkg)
  {
    try
    {
      FeatureEntry feature = gpkg.feature(tableName);
      if (feature != null)
      {
        Connection connection = gpkg.getDataSource().getConnection();
        Map<String, String> params = Map.of(TABLENAME_PARAM, tableName);
        InputStream is = DbScriptUtil.class.getResourceAsStream(DELETE_WFS_TABLE_SCRIPT);
        SqlUtil.runScript(is, connection, params);
        // table deleted (if present)
        WskLog.log(Level.INFO, "Tabelle gelöscht (falls vorhanden): '%s'", tableName);
      }
      else
      {
        // no offline content for table found
        WskLog.log(Level.INFO, "Kein offline Content für Tabelle gefunden: '%s'", tableName);
      }
    }
    catch (Exception e)
    {
      // table was not be deleted
      throw WskFrameworkException.failForReason(e, "Tabelle '%s' konnte nicht gelöscht werden!", tableName);
    }
  }

  public static void updateWfsLayerName(LayerModel layer, String wfsTableName)
  {
    String localLayerName = Check.notNull(layer, "layer").getLocalName();
    try
    {
      GeoPackage gpkg = WskFramework.getInstance().getWskGeoPackage().getGtGeoPackage();
      FeatureEntry feature = gpkg.feature(wfsTableName);
      if (feature != null)
      {
        Connection connection = gpkg.getDataSource().getConnection();
        Map<String, String> params = Map.of(TABLENAME_PARAM, wfsTableName, NEW_TABLENAME_PARAM, localLayerName);
        InputStream is = DbScriptUtil.class.getResourceAsStream(UPDATE_WFS_TABLE_NAME_SCRIPT);
        SqlUtil.runScript(is, connection, params);
        // table was renamed successfully
        WskLog.log(Level.INFO, "WFS Layer ('%s') Tabelle wurde erfolgreich umbenannt. '%s' -> '%s'", layer.getName(), wfsTableName, localLayerName);
      }
    }
    catch (Exception e)
    {
      // table rename failed
      throw WskFrameworkException.failForReason(e, "WFS Layer ('%s') Tabelle konnte nicht umbenannt werden! Tabelle: '%s'", layer.getName(), wfsTableName);
    }
  }
}
