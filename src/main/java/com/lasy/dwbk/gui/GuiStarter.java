package com.lasy.dwbk.gui;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Collection;
import java.util.Optional;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.error.ErrorModule;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.app.service.impl.LayerCrudService;
import com.lasy.dwbk.db.util.DbGeneratedLayerName;
import com.lasy.dwbk.gui.panes.MainPane;
import com.lasy.dwbk.gui.util.GuiIcon;
import com.lasy.dwbk.ws.wms.WmsLayerWriter;

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

    stage.setTitle("Waldbrandkarte Konfiguration");
    stage.getIcons().add(GuiIcon.WILD_FIRE);
    stage.setScene(scene);
    stage.setMaximized(true);
    stage.show();
    stage.toFront();
  }

  public static void main(String[] args)
  {
    Thread.setDefaultUncaughtExceptionHandler(handleUncaughtException());
    
    try (DwbkFramework framework = DwbkFramework.getInstance())
    {
//      migrate();
      // testWriter(framework);
      Application.launch();
    }
    catch (Throwable t)
    {
      ErrorModule.handleError(t);
    }
  }
  
  private static void testWriter(DwbkFramework framework)
  {
    // siedlung_luftbild Layer
    Optional<LayerModel> siedlungLuftbild = DwbkServiceProvider.getInstance().getLayerService().readById(8);
    if(siedlungLuftbild.isPresent()) {
      WmsLayerWriter writer = WmsLayerWriter.createForLayer(siedlungLuftbild.get());
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
    LayerCrudService layerService = DwbkServiceProvider.getInstance().getLayerService();
    Collection<LayerModel> layers = layerService.readAll();
    for(LayerModel model : layers)
    {
      String localTableName = DbGeneratedLayerName.idToGeneratedTableName(model.getId());
      model.setLocalName(localTableName);
      
      layerService.update(model);
    }
    
    System.out.println("Migration abgeschlossen!");
  }

}