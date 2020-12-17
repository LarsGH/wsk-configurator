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
  
  /** layer icon. */
  public static final Image LAYER = createIcon("layers_24.png");
  
  /** Bounding box icon. */
  public static final Image BOUNDING_BOX = createIcon("grid_24.png");
  
  /** Home (DB) icon. */
  public static final Image HOME_DB = createIcon("settings_24.png");
  
  /** Cancel icon. */
  public static final Image CANCEL = createIcon("cancel_24.png");
  
  /** Save icon. */
  public static final Image SAVE = createIcon("floppy-disk_24.png");
  
  /** Browser icon. */
  public static final Image BROWSER = createIcon("browser_24.png");

  private static Image createIcon(String iconName)
  {
    String url = GuiIcon.class.getResource(iconName).toExternalForm();
    return new Image(url);
  }
}
