package com.lasy.dwbk.gui.panes;

import com.lasy.dwbk.gui.info.DwbkAppInfo;
import com.lasy.dwbk.gui.panes.overview.LayerOverviewPane;
import com.lasy.dwbk.gui.util.GuiIcon;
import com.lasy.dwbk.gui.util.GuiUtil;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class MainPane extends ADwbkPane
{
  
  private static final double BUTTON_WIDTH = 200;

  public MainPane(Scene mainScene)
  {
    super(mainScene, "Übersicht");
  }
  
  @Override
  public void goToMainPane()
  {
    // Nothing to do!
  }
  
  private Button createGoToLayerOverviewButton()
  {
    return createOverviewButton(
      GuiIcon.LAYER,
      "Wechselt zur Layer-Übersicht",
      "Layer-Übersicht",
      e -> {
        goToPane(new LayerOverviewPane(getMainScene()));
      });
  }
  
  private Button createGoToBboxOverviewButton()
  {    
    return createOverviewButton(
      GuiIcon.BOUNDING_BOX,
      "Wechselt zur Boundingbox-Übersicht",
      "Boundingbox-Übersicht",
      e -> {
        goToPane(new LayerOverviewPane(getMainScene()));
      });
  }
  
  private Button createInfoButton()
  {    
    return createOverviewButton( 
      GuiIcon.INFO,
      "Informationen zu dieser Anwendung",
      "Anwendungs-Informationen",
      e -> {
        Alert info = DwbkAppInfo.getInfoAlert();
        info.show();
      });
  }
  
  private Button createOverviewButton(Image icon, String help, String txt, EventHandler<? super MouseEvent> handler)
  {
    Button btn = GuiUtil.createIconButtonWithText(icon, help, txt);
    btn.setPrefWidth(BUTTON_WIDTH);
    btn.setAlignment(Pos.CENTER_LEFT);
    btn.setOnMouseClicked(handler);
    
    return btn;
  }

  @Override
  protected Node createContent()
  {
    VBox box = new VBox(
      20,
      createGoToLayerOverviewButton(),
      createGoToBboxOverviewButton(),
      createInfoButton());
    box.setAlignment(Pos.CENTER);
    box.setPrefWidth(BUTTON_WIDTH);
    
    return box;
  }
  
  // TODO: HInweis ungespeicherte Aenderungen + Speichern
  

}
