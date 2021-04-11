package com.lasy.wsk;

import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Connection;
import java.util.Optional;
import java.util.logging.Level;

import org.geotools.geopkg.GeoPackage;
import org.geotools.jdbc.util.SqlUtil;

import com.lasy.wsk.app.WskFramework;
import com.lasy.wsk.app.WskServiceProvider;
import com.lasy.wsk.app.error.ErrorModule;
import com.lasy.wsk.app.error.WskFrameworkException;
import com.lasy.wsk.app.logging.WskLog;
import com.lasy.wsk.app.model.impl.LayerModel;
import com.lasy.wsk.ws.ILayerWriter;

public class Main
{

  public static void main(String[] args)
  {
    Thread.setDefaultUncaughtExceptionHandler(handleUncaughtException());

    try (WskFramework framework = WskFramework.getInstance())
    {
      createTestEntries();
//      testWriter(framework);
    }
    catch (Throwable t)
    {
      ErrorModule.handleError(t);
    }
  }

  private static final String DUMMY_INSERTS_SQL = "dummy_inserts.sql";

  private static void createTestEntries()
  {
    boolean isEmpty = WskServiceProvider.getInstance().getBboxService().readAll().isEmpty();
    if (isEmpty)
    {
      try
      {
        GeoPackage gpkg = WskFramework.getInstance().getWskGeoPackage().getGtGeoPackage();
        Connection connection = gpkg.getDataSource().getConnection();
        InputStream is = Main.class.getResourceAsStream(DUMMY_INSERTS_SQL);
        SqlUtil.runScript(is, connection);
        
        WskLog.log(Level.INFO, "Dummy-Einträge wurden erstellt.");
      }
      catch (Exception e)
      {
        WskFrameworkException.exitForReason(e, "Dummy-Einträge konnten nicht erstellt werden!");
      }
    }
  }

  private static void testWriter(WskFramework framework)
  {
    // NIPs Layer
    Optional<LayerModel> siedlungLuftbild = WskServiceProvider.getInstance().getLayerService().readById(10);
    if (siedlungLuftbild.isPresent())
    {
      ILayerWriter writer = ILayerWriter.createForLayer(siedlungLuftbild.get());
      writer.write();
    }
  }

  private static UncaughtExceptionHandler handleUncaughtException()
  {
    return (thread, thrown) -> {
      ErrorModule.handleError(thrown);
    };
  }
}
