package com.lasy.wsk.gui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collection;
import java.util.Optional;

import com.lasy.wsk.app.WskFramework;
import com.lasy.wsk.app.WskServiceProvider;
import com.lasy.wsk.app.error.ErrorModule;
import com.lasy.wsk.app.model.impl.LayerModel;
import com.lasy.wsk.app.service.impl.LayerCrudService;
import com.lasy.wsk.db.util.DbGeneratedLayerName;
import com.lasy.wsk.gui.panes.MainPane;
import com.lasy.wsk.gui.util.GuiIcon;
import com.lasy.wsk.ws.ILayerWriter;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GuiStarter extends Application
{

  @Override
  public void start(Stage stage)
  {
    Scene scene = new Scene(new BorderPane());
    MainPane mainPane = MainPane.create(scene);
    scene.setRoot(mainPane);

    stage.setTitle("Web-Service-Karte Konfiguration");
    stage.getIcons().add(GuiIcon.MAP);
    stage.setScene(scene);
    stage.setMaximized(true);
    stage.show();
    stage.toFront();
  }

  public static void main(String[] args)
  {
    Thread.setDefaultUncaughtExceptionHandler(handleUncaughtException());

    try (WskFramework framework = WskFramework.getInstance())
    {
      // migrate();
      // testWriter(framework);
      Application.launch();
    }
    catch (Throwable t)
    {
      ErrorModule.handleError(t);
    }
  }

  @SuppressWarnings("unused")
  private static void testWriter(WskFramework framework)
  {
    // siedlung_luftbild Layer
    Optional<LayerModel> siedlungLuftbild = WskServiceProvider.getInstance().getLayerService().readById(8);
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

  /**
   * Starts the migration.
   */
  @SuppressWarnings("unused")
  private static void migrate()
  {
    LayerCrudService layerService = WskServiceProvider.getInstance().getLayerService();
    Collection<LayerModel> layers = layerService.readAll();
    for (LayerModel model : layers)
    {
      String localTableName = DbGeneratedLayerName.idToGeneratedTableName(model.getId());
      model.setLocalName(localTableName);

      layerService.update(model);
    }

    // migration success
    System.out.println("Migration abgeschlossen!");
  }

}