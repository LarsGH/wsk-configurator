package com.lasy.dwbk.gui;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.base.Preconditions;
import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.gui.panes.ADwbkPane;
import com.lasy.dwbk.gui.panes.edit.LayerPane;
import com.lasy.dwbk.gui.util.ButtonTableCell;
import com.lasy.dwbk.gui.util.GuiIcon;
import com.lasy.dwbk.gui.util.GuiUtil;
import com.lasy.dwbk.gui.util.ModelValueFactory;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class LayerOverviewComponent
{

  private TableView<LayerModel> tableView;
  private ADwbkPane parentPane;
  
  /**
   * Creates a new layer overview.
   * @param panetPane the parent pane
   */
  public LayerOverviewComponent(ADwbkPane panetPane)
  {
    this.parentPane = Preconditions.checkNotNull(panetPane);
    this.tableView = createLayerTable();
  }
  
  private TableView<LayerModel> createLayerTable()
  {
    TableView<LayerModel> layerList = new TableView<LayerModel>();

    layerList.getColumns().addAll(createLayerColumns());
    Collection<LayerModel> layers = DwbkServiceProvider.getInstance().getLayerService().readAll();
    layerList.getItems().addAll(layers);

    return layerList;
  }
  
  private List<TableColumn<LayerModel, ?>> createLayerColumns()
  {
    TableColumn<LayerModel, String> nameCol = new TableColumn<>("Name");
    nameCol.setCellValueFactory(new ModelValueFactory<LayerModel>(LayerModel::getName));

    TableColumn<LayerModel, String> storeLocalCol = new TableColumn<>("Lokal speichern");
    storeLocalCol.setCellValueFactory(new ModelValueFactory<LayerModel>(layer -> GuiUtil.createBooleanDisplayValue(layer.isStoreLocal())));

    TableColumn<LayerModel, Button> editCol = new TableColumn<>("Editieren");
    Supplier<Button> btnEditLayer = () -> GuiUtil.createIconButton(GuiIcon.EDIT, "Layer editieren");
    editCol.setCellFactory(ButtonTableCell.<LayerModel> create(btnEditLayer, this::handleEditLayer));

    TableColumn<LayerModel, Button> deleteCol = new TableColumn<>("Löschen");
    Supplier<Button> btnDeleteLayer = () -> GuiUtil.createIconButton(GuiIcon.DELETE, "Layer l�schen");
    deleteCol.setCellFactory(ButtonTableCell.<LayerModel> create(btnDeleteLayer, this::handleDeleteLayer));

    return Arrays.asList(nameCol, storeLocalCol, editCol, deleteCol);
  }
  
  private void handleEditLayer(LayerModel layer)
  {
    System.out.println("clicked edit!");
    if (layer != null)
    {
      LayerPane layerPane = new LayerPane(parentPane.getMainScene(), layer);
      parentPane.goToPane(layerPane);
    }
  }
  
  private void handleDeleteLayer(LayerModel layer)
  {
    System.out.println("clicked delete!");
    if (layer != null)
    {
      System.out.println("TODO: delete layer... " + layer.getId());

      String deleteMsg = String.format("Layer '%s' wirklich löschen?", layer.getName());
      Alert alert = new Alert(AlertType.CONFIRMATION, deleteMsg, ButtonType.YES, ButtonType.NO);
      alert.setTitle("Layer löschen");
      alert.setHeaderText(null);

      Optional<ButtonType> result = alert.showAndWait();

      if (result.isPresent() && result.get() == ButtonType.YES)
      {
        // TODO: service call delete!
        System.out.println("deleting layer...!");
        this.tableView.getItems().remove(layer);
      }

    }
  }
  
  public TableView<LayerModel> getTableView()
  {
    return this.tableView;
  }
}
