package com.lasy.dwbk.gui;

import java.util.logging.Level;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.app.DwbkFrameworkException;
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
      Application.launch();
    }
    catch (DwbkFrameworkException e)
    {
      // TODO: In file Loggen!
      
      // TODO: Neue Umgebungsvariable Framework Log-Level!
      
      // TODO: Alert mit Fehlermeldung!
      
      if(e.hasLevel(Level.SEVERE))
      {
        System.exit(-1);
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

}