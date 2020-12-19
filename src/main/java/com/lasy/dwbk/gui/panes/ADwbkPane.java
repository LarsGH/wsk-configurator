package com.lasy.dwbk.gui.panes;

import com.lasy.dwbk.gui.util.GuiUtil;
import com.lasy.dwbk.util.Check;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * Common BorderPane layout. 
 * Children should just add the main content at the center.
 * 
 * @author larss
 *
 */
public abstract class ADwbkPane extends BorderPane
{

  protected static <TDwbkPane extends ADwbkPane> TDwbkPane createInitializedPane(TDwbkPane pane)
  {
    pane.init();
    return pane;
  }
  
  private Scene mainScene;
  
  public ADwbkPane(Scene mainScene, String header)
  {
    super();
    this.mainScene = Check.notNull(mainScene, "mainScene");
    Check.notNull(header, "header");
    
    setTop(GuiUtil.createHeader(header));
  }
  
  /**
   * Completes the Pane initialization.
   */
  public void init()
  {
    setCenter(createContent());
  }
  
  /**
   * Creates the main content specific to the pane.
   * @return pane specific content
   */
  protected abstract Node createContent();

  /**
   * Switches to the main pane.
   */
  public void goToMainPane()
  {
    System.out.println("going to main pane...");
    MainPane mainPane = MainPane.create(this.mainScene);
    goToPane(mainPane);
  }
  
  /**
   * Switches to the pane.
   * @param pane new pane to display
   */
  public void goToPane(ADwbkPane pane)
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
