package com.lasy.dwbk;

import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Connection;
import java.util.Optional;
import java.util.logging.Level;

import org.geotools.geopkg.GeoPackage;
import org.geotools.jdbc.util.SqlUtil;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.error.DwbkFrameworkException;
import com.lasy.dwbk.app.error.ErrorModule;
import com.lasy.dwbk.app.logging.DwbkLog;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.ws.ILayerWriter;

public class Main
{

  public static void main(String[] args)
  {
    Thread.setDefaultUncaughtExceptionHandler(handleUncaughtException());

    try (DwbkFramework framework = DwbkFramework.getInstance())
    {
      createTestEntries();
      testWriter(framework);
    }
    catch (Throwable t)
    {
      ErrorModule.handleError(t);
    }
  }

  private static final String DUMMY_INSERTS_SQL = "dummy_inserts.sql";

  private static void createTestEntries()
  {
    boolean isEmpty = DwbkServiceProvider.getInstance().getBboxService().readAll().isEmpty();
    if (isEmpty)
    {
      try
      {
        GeoPackage gpkg = DwbkFramework.getInstance().getDwbkGeoPackage().getGtGeoPackage();
        Connection connection = gpkg.getDataSource().getConnection();
        InputStream is = Main.class.getResourceAsStream(DUMMY_INSERTS_SQL);
        SqlUtil.runScript(is, connection);
        
        DwbkLog.log(Level.INFO, "Dummy-Einträge wurden erstellt.");
      }
      catch (Exception e)
      {
        DwbkFrameworkException.exitForReason(e, "Dummy-Einträge konnten nicht erstellt werden!");
      }
    }
  }

  private static void testWriter(DwbkFramework framework)
  {
    // NIPs Layer
    Optional<LayerModel> siedlungLuftbild = DwbkServiceProvider.getInstance().getLayerService().readById(10);
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
