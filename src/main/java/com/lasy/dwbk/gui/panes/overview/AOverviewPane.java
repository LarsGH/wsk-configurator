package com.lasy.dwbk.gui.panes.overview;

import com.lasy.dwbk.gui.panes.ADwbkPane;
import com.lasy.dwbk.gui.util.GuiIcon;
import com.lasy.dwbk.gui.util.GuiUtil;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public abstract class AOverviewPane extends ADwbkPane
{

  public AOverviewPane(Scene mainScene, String header)
  {
    super(mainScene, header);
    
    setBottom(createMainButtonBox());
  }
  
  private HBox createMainButtonBox()
  {
    Button mainBtn = GuiUtil.createIconButtonWithText(GuiIcon.HOME_DB, "Überblick", "Überblick");
    mainBtn.setOnAction(e -> {
      System.out.println("Überblick clicked...");
      goToMainPane();
    });
    
    return new HBox(mainBtn);
  }

}
