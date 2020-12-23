package com.lasy.dwbk.gui.panes;

import com.lasy.dwbk.gui.info.DwbkAppInfo;
import com.lasy.dwbk.gui.panes.overview.impl.BboxOverviewPane;
import com.lasy.dwbk.gui.panes.overview.impl.LayerOverviewPane;
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
  
  /**
   * Creates the main pane.
   * @param mainScene the main scene
   * @return main pane
   */
  public static MainPane create(Scene mainScene)
  {
    return createInitializedPane(new MainPane(mainScene));
  }
  
  private static final double BUTTON_WIDTH = 200;

  private MainPane(Scene mainScene)
  {
    // TODO: allgemeine Settings (passwort speichern erlauben?)
    super(mainScene, "Konfigurations-Übersicht");
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
        goToPane(LayerOverviewPane.create(getMainScene()));
      });
  }
  
  private Button createGoToBboxOverviewButton()
  {    
    return createOverviewButton(
      GuiIcon.BOUNDING_BOX,
      "Wechselt zur Boundingbox-Übersicht",
      "Boundingbox-Übersicht",
      e -> {
        goToPane(BboxOverviewPane.create(getMainScene()));
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
  protected Node createCenterContent()
  {
    VBox box = new VBox(
      GuiUtil.DEFAULT_SPACING,
      createGoToLayerOverviewButton(),
      createGoToBboxOverviewButton(),
      createInfoButton());
    box.setAlignment(Pos.CENTER);
    box.setPrefWidth(BUTTON_WIDTH);
    
    return box;
  }

  @Override
  protected Node createBottomContent()
  {
    return null;
  }
  
  // TODO: HInweis ungespeicherte Aenderungen + Speichern button
  

}
