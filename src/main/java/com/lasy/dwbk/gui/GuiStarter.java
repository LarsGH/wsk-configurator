package com.lasy.dwbk.gui;

import java.lang.Thread.UncaughtExceptionHandler;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.app.error.ErrorModule;
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
    Thread.setDefaultUncaughtExceptionHandler(handleUncaughtException());
    
    try (DwbkFramework framework = DwbkFramework.getInstance())
    {
      Application.launch();
    }
    catch (Throwable t)
    {
      ErrorModule.handleError(t);
    }
  }
  
  private static UncaughtExceptionHandler handleUncaughtException()
  {
    return (thread, thrown) -> {
      ErrorModule.handleError(thrown);
    };
  }

}