package com.lasy.dwbk.gui.util;

import com.google.common.base.Preconditions;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

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
    
    Button btn = createTextButton(txt);
    btn.setGraphic(new ImageView(icon));
    if(tooltip != null)
    {
      btn.setTooltip(new Tooltip(tooltip));
    }
    btn.setTextAlignment(TextAlignment.CENTER);
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
    return createIconButtonWithText(icon, tooltip, null);
  }
  
  /**
   * Creates a new Button.
   * 
   * @param icon the button icon
   * @return new button
   */
  private static Button createTextButton(String txt)
  {
    Button btn = new Button();
    if(txt != null)
    {
      btn.setText(txt);
    }
    return btn;
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
    HBox.setMargin(header, new Insets(10));
    return box;
  }
  
  
  
}
