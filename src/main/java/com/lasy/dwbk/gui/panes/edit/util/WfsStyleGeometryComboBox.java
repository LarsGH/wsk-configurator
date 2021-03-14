package com.lasy.dwbk.gui.panes.edit.util;

import com.lasy.dwbk.ws.wfs.style.EWfsStyleGeometry;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

/**
 * WFS style geometry selection dialog.
 * @author larss
 *
 */
public class WfsStyleGeometryComboBox extends ComboBox<EWfsStyleGeometry>
{

  /**
   * Display value for default entry ({@code null}).
   */
  private EWfsStyleGeometry wfsStyleGeom;

  public WfsStyleGeometryComboBox()
  {
    super();
    init();
  }

  private void init()
  {
    // add default entry
    getItems().addAll(EWfsStyleGeometry.values());

    setConverter(new StringConverter<EWfsStyleGeometry>() {

      @Override
      public String toString(EWfsStyleGeometry geom)
      {
        if (geom == null)
        {
          return "";
        }
        return geom.getLabel();
      }

      @Override
      public EWfsStyleGeometry fromString(String string)
      {
        return EWfsStyleGeometry.forConfiguredValue(string);
      }
    });

    setOnAction(e -> {
      EWfsStyleGeometry selectedItem = getSelectionModel().getSelectedItem();
      setSelectedWfsStyleGeom(selectedItem);
    });

  }

  /**
   * Returns the WFS style geometry (enum constant name).
   * @return id
   */
  public String getSelectedWfsStyleGeomConfigValue()
  {
    if (wfsStyleGeom != null)
    {
      return wfsStyleGeom.toString();
    }
    return null;
  }

  /**
   * Sets the selected WFS style geometry.
   * @param styleGeom style geometry
   */
  public void setSelectedWfsStyleGeom(EWfsStyleGeometry styleGeom)
  {
    this.wfsStyleGeom = styleGeom;
    getSelectionModel().select(styleGeom);
  }

}
