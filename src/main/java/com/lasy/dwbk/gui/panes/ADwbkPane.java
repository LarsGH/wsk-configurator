package com.lasy.dwbk.gui.panes;

import com.google.common.base.Preconditions;
import com.lasy.dwbk.gui.util.GuiUtil;

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

  private Scene mainScene;
  
  public ADwbkPane(Scene mainScene, String header)
  {
    super();
    this.mainScene = Preconditions.checkNotNull(mainScene);
    Preconditions.checkNotNull(header);
    
    setTop(GuiUtil.createHeader(header));
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
    MainPane mainPane = new MainPane(this.mainScene);
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
