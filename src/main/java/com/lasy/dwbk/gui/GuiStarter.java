package com.lasy.dwbk.gui;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.db.DwbkGeoPackage;
import com.lasy.dwbk.gui.panes.MainPane;
import com.lasy.dwbk.gui.util.GuiIcon;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GuiStarter extends Application
{

  @Override
  public void start(Stage stage) 
  {    
    Scene scene = new Scene(new BorderPane(), 640, 480);
    MainPane mainPane = MainPane.create(scene);
    scene.setRoot(mainPane);

    stage.setTitle("Waldbrandkarte Konfiguration");
    stage.getIcons().add(GuiIcon.WILD_FIRE);
    stage.setScene(scene);
    stage.show();
    stage.toFront();
  }

  public static void main(String[] args)
  {
    try (DwbkFramework framework = DwbkFramework.getInstance())
    {
      DwbkGeoPackage tmGpkg = framework.getGeoPackage();

      System.out.println("Available tables: " + tmGpkg.getAvailableTableNames());

      Application.launch();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

}