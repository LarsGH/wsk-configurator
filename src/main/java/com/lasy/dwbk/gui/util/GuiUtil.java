package com.lasy.dwbk.gui.util;

import com.google.common.base.Preconditions;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GuiUtil
{

  /**
   * Returns the display value for the boolean.
   * 
   * @param bool boolean value
   * @return "Ja" for {@code true} and "Nein" for {@code false}
   */ 
  public static String createBooleanDisplayValue(boolean bool)
  {
    return bool 
      ? "Ja" 
      : "Nein";
  }
  
  /**
   * Creates a new Button.
   * 
   * @param icon the button icon
   * @param tooltip the help text
   * @param txt the button text
   * @return new button
   */
  public static Button createIconButtonWithText(Image icon, String tooltip, String txt)
  {
    Preconditions.checkNotNull(icon);
    Preconditions.checkNotNull(tooltip);
    Preconditions.checkNotNull(txt);
    
    Button btn = createTextButton(txt);
    btn.setGraphic(new ImageView(icon));
    btn.setTooltip(new Tooltip(tooltip));
    return btn;
  }
  
  /**
   * Creates a new Button.
   * 
   * @param icon the button icon
   * @param tooltip the help text
   * @return new button
   */
  public static Button createIconButton(Image icon, String tooltip)
  {
    Preconditions.checkNotNull(icon);
    Preconditions.checkNotNull(tooltip);
    
    Button btn = new Button();
    btn.setGraphic(new ImageView(icon));
    btn.setTooltip(new Tooltip(tooltip));
    return btn;
  }
  
  /**
   * Creates a new Button.
   * 
   * @param icon the button icon
   * @return new button
   */
  public static Button createTextButton(String txt)
  {
    Preconditions.checkNotNull(txt);
       
    return new Button(txt);
  }
  
  /**
   * Creates a new Header.
   * 
   * @param txt header content
   * @return new header
   */
  public static HBox createHeader(String txt)
  {
    Label header = new Label(txt);
    header.setFont(Font.font(null, FontWeight.BOLD, 20));
    HBox box = new HBox(header);
    box.setAlignment(Pos.CENTER);
    return box;
  }
  
  
  
}
