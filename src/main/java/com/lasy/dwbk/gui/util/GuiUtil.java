package com.lasy.dwbk.gui.util;

import com.lasy.dwbk.util.Check;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
  
  public static final double DEFAULT_SPACING = 10;

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
    Check.notNull(icon, "icon");
    
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
  
  private static final double DEFAULT_LABEL_SIZE = 12;
  
  /**
   * Creates a new bold label.
   * @param txt the label text
   * @return bold label
   */
  public static Label createBoldLabel(String txt)
  {
    Label lbl = new Label(txt);
    lbl.setFont(Font.font(null, FontWeight.BOLD, DEFAULT_LABEL_SIZE));
    return lbl;
  }
  
  private static Label createBoldLabelWithSize(String txt, double size)
  {
    Label lbl = new Label(txt);
    lbl.setFont(Font.font(null, FontWeight.BOLD, size));
    return lbl;
  }
  
  private static final double DEFAULT_HEADER_SIZE = 20;
  
  /**
   * Creates a new Header.
   * 
   * @param txt header content
   * @return new header
   */
  public static HBox createHeader(String txt)
  {
    Label header = createBoldLabelWithSize(txt, DEFAULT_HEADER_SIZE);
    HBox box = new HBox(header);
    box.setAlignment(Pos.CENTER);
    HBox.setMargin(header, new Insets(DEFAULT_SPACING));
    return box;
  }
  
  /**
   * Creates an alert with an OK-Option only.
   * @param type alert type
   * @param title alert title
   * @param message alert message
   * @return alert
   */
  public static Alert createOkAlert(AlertType type, String title, String message)
  {
    Alert alert = new Alert(type, message, ButtonType.OK);
    alert.setTitle(title);
    alert.setHeaderText(null);
    return alert;
  }
  
}
