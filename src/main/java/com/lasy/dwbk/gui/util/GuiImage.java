package com.lasy.dwbk.gui.util;

import javafx.scene.image.Image;

/**
 * GUI Images.
 * @author larss
 *
 */
public class GuiImage
{
  
  /** The background image for the main page. */
  public static final Image MAIN_PAGE_BACKGROUND_IMG = createImage("wildfire_John_McColgan.jpg");
  
  private static Image createImage(String name)
  {
    String url = GuiIcon.class.getResource(name).toExternalForm();
    return new Image(url);
  }
}
