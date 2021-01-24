package com.lasy.dwbk.db.util;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Map;

import org.geotools.geopkg.GeoPackage;
import org.geotools.jdbc.util.SqlUtil;

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
   * @param gpkg the GT geopackage
   * @param tableName the generated table name (layer localname)
   */
  public static void deleteGeneratedTable(GeoPackage gpkg, String tableName)
  {
    try
    {
      Connection connection = gpkg.getDataSource().getConnection();
      Map<String, String> params = Map.of(TABLENAME_PARAM, tableName);
      InputStream is = DbScriptUtil.class.getResourceAsStream(DELETE_GEN_TABLE_SCRIPT_NAME);
      SqlUtil.runScript(is, connection, params);      
    }
    catch (Exception e)
    {
      String msg = String.format("Execution of delete script for table '%s' failed!", tableName);
      throw new IllegalStateException(msg, e);
    }
  }
}
