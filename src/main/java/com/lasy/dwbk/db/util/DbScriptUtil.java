package com.lasy.dwbk.db.util;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;
import java.util.logging.Level;

import org.geotools.geopkg.GeoPackage;
import org.geotools.geopkg.TileEntry;
import org.geotools.jdbc.util.SqlUtil;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.app.error.DwbkFrameworkException;
import com.lasy.dwbk.app.logging.DwbkLog;
import com.lasy.dwbk.app.model.impl.LayerModel;

/**
 * Helper to execute useful scripts on the geopackage file.
 * @author larss
 *
 */
public class DbScriptUtil
{
  private static final String DELETE_GEN_TABLE_SCRIPT_NAME = "delete_gen_table.sql";
  
  /** The tablename parameter to replace in the script.  */
  private static final String TABLENAME_PARAM = "tableName";
  
  /**
   * Deletes a generated table from the geopackage. Delete affects:
   * <ul>
   * <li>delete of generated table</li>
   * <li>entry in gpkg_contents</li>
   * <li>entry in gpkg_tile_matrix</li>
   * <li>entry in gpkg_tile_matrix_set</li>
   * </ul>
   * 
   * @param layer the layer
   */
  public static void deleteLocalLayerContentIfPresent(LayerModel layer)
  {
    String localLayerName = layer.getLocalName();
    try
    {
      GeoPackage gpkg = DwbkFramework.getInstance().getDwbkGeoPackage().getGtGeoPackage();
      TileEntry tileEntry = gpkg.tile(localLayerName);
      if(tileEntry != null)
      {
        Connection connection = gpkg.getDataSource().getConnection();
        Map<String, String> params = Map.of(TABLENAME_PARAM, localLayerName);
        InputStream is = DbScriptUtil.class.getResourceAsStream(DELETE_GEN_TABLE_SCRIPT_NAME);
        SqlUtil.runScript(is, connection, params);
        DwbkLog.log(Level.INFO, "Offline Content von Layer '%s' wurde erfolgreich gelöscht. (Tabelle: '%s')", layer.getName(), localLayerName);
      }      
    }
    catch (Exception e)
    {
      throw DwbkFrameworkException.failForReason(e, "Aktueller offline Content von Layer '%s' konnte nicht gelöscht werden!", layer.getName());
    }
  }
}
