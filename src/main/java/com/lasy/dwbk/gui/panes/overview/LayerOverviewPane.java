package com.lasy.dwbk.gui.panes.overview;

import com.lasy.dwbk.gui.LayerOverviewComponent;
import com.lasy.dwbk.gui.panes.edit.LayerPane;
import com.lasy.dwbk.gui.util.GuiIcon;
import com.lasy.dwbk.gui.util.GuiUtil;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

public class LayerOverviewPane extends AOverviewPane
{

  public LayerOverviewPane(Scene mainScene)
  {
    super(mainScene, "Layer-Übersicht");
  }
  
  private Button createNewLayerButton()
  {
    Button btnAddLayer = GuiUtil.createIconButtonWithText(
      GuiIcon.CREATE, 
      "Erstellt einen neuen Layer",  
      "Neuen Layer erstellen");

    btnAddLayer.setOnMouseClicked(e -> {
      goToPane(new LayerPane(getMainScene(), null));
    });
    return btnAddLayer;
  }

  @Override
  protected Node createContent()
  {
    BorderPane pane = new BorderPane();
    pane.setTop(createNewLayerButton());
    
    LayerOverviewComponent layerTable = new LayerOverviewComponent(this);
    pane.setCenter(layerTable.getTableView());
    
    return pane;
  }

}
