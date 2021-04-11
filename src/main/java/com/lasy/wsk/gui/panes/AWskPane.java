package com.lasy.wsk.gui.panes;

import com.lasy.wsk.gui.util.GuiUtil;
import com.lasy.wsk.util.Check;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

/**
 * Common BorderPane layout. 
 * Children should just add the main content at the center.
 * 
 * @author larss
 *
 */
public abstract class AWskPane extends AWskMarginPane
{

  protected static <TWskPane extends AWskPane> TWskPane createInitializedPane(TWskPane pane)
  {
    pane.init();
    return pane;
  }
  
  private Scene mainScene;
  
  public AWskPane(Scene mainScene, String header)
  {
    super();
    this.mainScene = Check.notNull(mainScene, "mainScene");
    Check.notNull(header, "header");
    
    setTopWithMargin(GuiUtil.createHeader(header));
  }
  
  /**
   * Completes the Pane initialization.
   */
  public void init()
  {
    setCenterWithMargin(createCenterPane());
    setBottomWithMargin(createBottomContent());
  }

  private ScrollPane createCenterPane()
  {
    ScrollPane centerPane = new ScrollPane(createCenterContent());
    centerPane.setFitToWidth(true);
    centerPane.setFitToHeight(true);
    centerPane.setCenterShape(true);
    
    Background bg = new Background(new BackgroundFill(Color.TRANSPARENT, null, null));
    centerPane.setBackground(bg);
    
    return centerPane;
  }
  
  /**
   * Creates the bottom content specific to the pane.
   * @return specific content
   */
  protected abstract Node createBottomContent();

  /**
   * Creates the main content specific to the pane.
   * @return specific content
   */
  protected abstract Node createCenterContent();

  /**
   * Switches to the main pane.
   */
  public void goToMainPane()
  {
    MainPane mainPane = MainPane.create(this.mainScene);
    goToPane(mainPane);
  }
  
  /**
   * Switches to the pane.
   * @param pane new pane to display
   */
  public void goToPane(AWskPane pane)
  {
    mainScene.setRoot(pane);
  }
  
  /**
   * Returns the main scene.
   * @return main scene
   */
  public Scene getMainScene()
  {
    return this.mainScene;
  }
}
