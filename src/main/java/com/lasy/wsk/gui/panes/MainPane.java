package com.lasy.wsk.gui.panes;

import com.lasy.wsk.gui.info.WskAppInfo;
import com.lasy.wsk.gui.panes.overview.impl.BboxOverviewPane;
import com.lasy.wsk.gui.panes.overview.impl.LayerOverviewPane;
import com.lasy.wsk.gui.util.GuiIcon;
import com.lasy.wsk.gui.util.GuiImage;
import com.lasy.wsk.gui.util.GuiUtil;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;

/**
 * The main pane shown when the application is started.
 * @author larss
 *
 */
public class MainPane extends AWskPane
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
    // configuration overview
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
      // go to layer overview
      "Wechselt zur Layer-Übersicht",
      // layer overview
      "Layer-Übersicht",
      e -> {
        goToPane(LayerOverviewPane.create(getMainScene()));
      });
  }
  
  private Button createGoToBboxOverviewButton()
  {    
    return createOverviewButton(
      GuiIcon.BOUNDING_BOX,
      // to to boundingbox overview
      "Wechselt zur Boundingbox-Übersicht",
      // boundingbox overview
      "Boundingbox-Übersicht",
      e -> {
        goToPane(BboxOverviewPane.create(getMainScene()));
      });
  }
  
  private Button createInfoButton()
  {    
    return createOverviewButton( 
      GuiIcon.INFO,
      // information about this application
      "Informationen zu dieser Anwendung",
      // application information
      "Anwendungs-Informationen",
      e -> {
        Alert info = WskAppInfo.getInfoAlert();
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

  /**
   * Creates an image Background.
   * @return image background
   */
  private Background createImageBackground()
  {
    BackgroundSize bgSize = new BackgroundSize(
      BackgroundSize.AUTO, 
      BackgroundSize.AUTO, 
      false, 
      false, 
      true, 
      true);
    
    Background bg = new Background(new BackgroundImage(
      GuiImage.MAIN_PAGE_BACKGROUND_IMG, 
      BackgroundRepeat.NO_REPEAT, 
      BackgroundRepeat.NO_REPEAT, 
      BackgroundPosition.CENTER, 
      bgSize));
    return bg;
  }

  @Override
  protected Node createBottomContent()
  {
    return null;
  }

}
