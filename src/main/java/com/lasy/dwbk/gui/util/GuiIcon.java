package com.lasy.dwbk.gui.util;

import javafx.scene.image.Image;

public class GuiIcon
{

  /** Create icon. */
  public static final Image CREATE = createIcon("create_24.png");
  
  /** Edit icon. */
  public static final Image EDIT = createIcon("edit_24.png");
  
  /** Delete icon. */
  public static final Image DELETE = createIcon("delete_24.png");
  
  /** Info icon. */
  public static final Image INFO = createIcon("info_24.png");
  
  /** Wild fire icon. */
  public static final Image WILD_FIRE = createIcon("wildfire_24.png");

  private static Image createIcon(String iconName)
  {
    System.out.println("load icon: " + iconName);
    String url = GuiIcon.class.getResource(iconName).toExternalForm();
    return new Image(url);
  }
}
