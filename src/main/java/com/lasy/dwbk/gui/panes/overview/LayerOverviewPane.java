package com.lasy.dwbk.gui.panes.overview;

import java.util.List;

import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.model.IGtModelBuilder;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.app.service.ADwbkCrudService;
import com.lasy.dwbk.gui.panes.edit.AModelEditPane;
import com.lasy.dwbk.gui.panes.edit.LayerEditPane;
import com.lasy.dwbk.gui.util.GuiIcon;
import com.lasy.dwbk.gui.util.GuiUtil;
import com.lasy.dwbk.gui.util.ModelValueFactory;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;

public class LayerOverviewPane extends AOverviewPane<LayerModel>
{
  /**
   * Creates the layer overview pane.
   * @param mainScene the main scene
   * @return layer overview pane
   */
  public static LayerOverviewPane create(Scene mainScene)
  {
    return createInitializedPane(new LayerOverviewPane(mainScene));
  }
  
  private LayerOverviewPane(Scene mainScene)
  {
    super(mainScene, "Layer-Übersicht");
  }

  @Override
  protected Button createNewModelButton()
  {
    return GuiUtil.createIconButtonWithText(
      GuiIcon.CREATE, 
      "Erstellt einen neuen Layer",  
      "Neuen Layer erstellen");
  }

  @Override
  protected ADwbkCrudService<LayerModel, ? extends IGtModelBuilder<LayerModel>> getCrudService()
  {
    return DwbkServiceProvider.getInstance().getLayerService();
  }

  @Override
  protected List<TableColumn<LayerModel, ?>> createSpecificModelColumns()
  {
    TableColumn<LayerModel, String> nameCol = new TableColumn<>("Name");
    nameCol.setCellValueFactory(new ModelValueFactory<LayerModel>(LayerModel::getName));

    TableColumn<LayerModel, String> storeLocalCol = new TableColumn<>("Lokal speichern");
    storeLocalCol.setCellValueFactory(new ModelValueFactory<LayerModel>(layer -> GuiUtil.createBooleanDisplayValue(layer.isStoreLocal())));
    
    TableColumn<LayerModel, String> isSavedCol = new TableColumn<>("Gespeichert");
    isSavedCol.setCellValueFactory(new ModelValueFactory<LayerModel>(layer -> GuiUtil.createBooleanDisplayValue(layer.isSaved())));
    
    TableColumn<LayerModel, String> lastChangeCol = new TableColumn<>("Letzte Änderung");
    lastChangeCol.setCellValueFactory(new ModelValueFactory<LayerModel>(layer -> layer.getLastChangedDate().toString()));

    return List.of(nameCol, storeLocalCol, isSavedCol, lastChangeCol);
  }

  @Override
  protected AModelEditPane<LayerModel> getModelEditPane(LayerModel model)
  {
    return LayerEditPane.create(getMainScene(), model);
  }

}
