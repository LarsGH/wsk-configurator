package com.lasy.dwbk.gui.panes.overview.impl;

import java.util.List;
import java.util.Optional;

import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.model.IGtModelBuilder;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.app.service.ADwbkCrudService;
import com.lasy.dwbk.app.service.impl.BboxCrudService;
import com.lasy.dwbk.gui.panes.edit.AModelEditPane;
import com.lasy.dwbk.gui.panes.edit.impl.LayerEditPane;
import com.lasy.dwbk.gui.panes.overview.AOverviewPane;
import com.lasy.dwbk.gui.util.GuiIcon;
import com.lasy.dwbk.gui.util.GuiUtil;
import com.lasy.dwbk.gui.util.ModelValueFactory;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;

/**
 * Overview pane for layers.
 * @author larss
 */
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
    TableColumn<LayerModel, String> isVisibleCol = new TableColumn<>("Initial sichtbar");
    isVisibleCol.setCellValueFactory(new ModelValueFactory<LayerModel>(layer -> GuiUtil.createBooleanDisplayValue(layer.isVisible())));
    
    TableColumn<LayerModel, String> storeLocalCol = new TableColumn<>("Lokal speichern");
    storeLocalCol.setCellValueFactory(new ModelValueFactory<LayerModel>(layer -> GuiUtil.createBooleanDisplayValue(layer.isStoreLocal())));
    
    TableColumn<LayerModel, String> isSavedCol = new TableColumn<>("Gespeichert");
    isSavedCol.setCellValueFactory(new ModelValueFactory<LayerModel>(layer -> GuiUtil.createBooleanDisplayValue(layer.isSaved())));

    return List.of(isVisibleCol, storeLocalCol, isSavedCol);
  }

  @Override
  protected AModelEditPane<LayerModel> getModelEditPane(LayerModel model)
  {
    return LayerEditPane.create(getMainScene(), model);
  }

  @Override
  protected Optional<String> getDeleteNotAllowedReason(LayerModel model)
  {
    return Optional.empty();
  }

  @Override
  protected Optional<String> getCreateNotAllowedReason()
  {
    if(noBboxesExist())
    {
      return Optional.of("Es muss mindestens eine Boundingbox vorhanden sein um den Kartenausschnitt festzulegen!");
    }
    return Optional.empty();
  }

  private boolean noBboxesExist()
  {
    BboxCrudService bboxService = DwbkServiceProvider.getInstance().getBboxService();
    return bboxService.readAll().isEmpty();
  }

}
