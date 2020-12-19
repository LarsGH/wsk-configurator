package com.lasy.dwbk.gui.info;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import com.lasy.dwbk.gui.util.GuiIcon;
import com.lasy.dwbk.gui.util.GuiUtil;

import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Application informations.
 * @author larss
 */
public class DwbkAppInfo
{

  private static final String ICON_CREATOR_URL = "https://www.flaticon.com/authors/freepik";
  private static final String ICON_SITE_URL = "https://www.flaticon.com/";
  
  private static final String GROUP_ID = "dwbk-configurator";
  private static final String ARTIFACT_ID = "dwbk-configurator";

  private static String getVersion()
  {
    InputStream propStream = null;
    try
    {
      String propertiesPath = String.format("/META-INF/maven/%s/%s/pom.properties", GROUP_ID, ARTIFACT_ID);
      propStream = DwbkAppInfo.class.getResourceAsStream(propertiesPath);
      Properties properties = new Properties();
      properties.load(propStream);
      
      return properties.getProperty("version");
    }
    catch (Exception e)
    {
      // ignore
    }
    finally
    {
      if(propStream != null)
      {
        try
        {
          propStream.close();
        }
        catch (IOException e)
        {
          // ignore
        }
      }
    }
    return "unbekannt";
  }

  public static VBox getInfoBox()
  {
    HBox header = GuiUtil.createHeader("Digitale Waldbrand-Karte");

    Label versionInfo = new Label("Version: " + getVersion());

    Label iconCreator = new Label("Icon-Ersteller: Freepik");
    Button iconCreatorBtn = createBrowseButton("Öffnet die Seite des Icon-Erstellers", ICON_CREATOR_URL);
    HBox iconCreatorInfo = new HBox(GuiUtil.DEFAULT_SPACING, iconCreatorBtn, iconCreator);
    iconCreatorInfo.setAlignment(Pos.CENTER_LEFT);

    Label iconSite = new Label("Icon-Plattform: Flaticon.com");
    Button iconSiteBtn = createBrowseButton("Öffnet die Seite der Icon-Plattform", ICON_SITE_URL);
    HBox iconSiteInfo = new HBox(GuiUtil.DEFAULT_SPACING, iconSiteBtn, iconSite);
    iconSiteInfo.setAlignment(Pos.CENTER_LEFT);

    return new VBox(GuiUtil.DEFAULT_SPACING, header, versionInfo, iconCreatorInfo, iconSiteInfo);
  }

  private static Button createBrowseButton(String toolTip, String site)
  {
    Button btn = GuiUtil.createIconButton(GuiIcon.BROWSER, toolTip);
    btn.setOnAction(e -> {
      openSite(site);
    });
    return btn;
  }

  private static void openSite(String site)
  {
    Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
    {
      try
      {
        URI uri = new URI(site);
        desktop.browse(uri);
        return;
      }
      catch (Exception e)
      {
        // showing alert...
      }
    }

    String msg = String.format("Das Öffnen der Seite (%s) wird nicht unterstützt!", site);
    Alert alert = GuiUtil.createOkAlert(AlertType.WARNING, "Funktion nicht unterstützt", msg);
    alert.show();
  }

  public static Alert getInfoAlert()
  {
    Alert info = new Alert(AlertType.INFORMATION, null, ButtonType.OK);
    info.setTitle("Anwendungs-Informationen");
    info.setHeaderText(null);
    info.getDialogPane().setContent(getInfoBox());
    
    // Add title icon
    Stage alertStage = (Stage) info.getDialogPane().getScene().getWindow();
    alertStage.getIcons().add(GuiIcon.WILD_FIRE);

    return info;
  }

}
