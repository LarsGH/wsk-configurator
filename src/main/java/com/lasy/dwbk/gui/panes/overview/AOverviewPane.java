package com.lasy.dwbk.gui.panes.overview;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.lasy.dwbk.app.model.IGtModel;
import com.lasy.dwbk.app.model.IGtModelBuilder;
import com.lasy.dwbk.app.service.ADwbkCrudService;
import com.lasy.dwbk.gui.panes.ADwbkPane;
import com.lasy.dwbk.gui.panes.edit.AModelEditPane;
import com.lasy.dwbk.gui.util.ButtonTableCell;
import com.lasy.dwbk.gui.util.GuiIcon;
import com.lasy.dwbk.gui.util.GuiUtil;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public abstract class AOverviewPane<TModelType extends IGtModel> extends ADwbkPane
{

  private TableView<TModelType> modelTable;
  
  public AOverviewPane(Scene mainScene, String header)
  {
    super(mainScene, header);
    
    setBottom(createMainButtonBox());
    modelTable = createModelTable();
  }
  
  private HBox createMainButtonBox()
  {
    Button mainBtn = GuiUtil.createIconButtonWithText(GuiIcon.HOME_DB, "Übersicht", "Wechsel zu Übersicht");
    mainBtn.setOnAction(e -> {
      goToMainPane();
    });
    
    return new HBox(GuiUtil.DEFAULT_SPACING, mainBtn);
  }
  
  @Override
  protected Node createContent()
  {
    BorderPane pane = new BorderPane();
    pane.setTop(createNewModelButtonWithAction());
    
    Collection<TModelType> models = getCrudService().readAll();
    modelTable.getItems().addAll(models);
    pane.setCenter(modelTable);
    
    return pane;
  }

  private Button createNewModelButtonWithAction()
  {
    Button newModelButton = createNewModelButton();
    newModelButton.setOnMouseClicked(e -> {
      goToPane(getModelEditPane(null));
    });
    return newModelButton;
  }
  
  private TableView<TModelType> createModelTable()
  {
    TableView<TModelType> layerList = new TableView<TModelType>();
    layerList.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    layerList.getColumns().addAll(createSpecificModelColumns());
    layerList.getColumns().addAll(createEditDeleteButtonColumns());

    return layerList;
  }
  
  private List<TableColumn<TModelType, ?>> createEditDeleteButtonColumns()
  {
    TableColumn<TModelType, Button> editCol = new TableColumn<>("Editieren");
    Supplier<Button> btnEdit = () -> GuiUtil.createIconButton(GuiIcon.EDIT, "Ruft die Editieren-Maske auf");
    editCol.setCellFactory(ButtonTableCell.<TModelType> create(btnEdit, this::handleEditModel));

    TableColumn<TModelType, Button> deleteCol = new TableColumn<>("Löschen");
    Supplier<Button> btnDelete = () -> GuiUtil.createIconButton(GuiIcon.DELETE, "Löscht die Auswahl");
    deleteCol.setCellFactory(ButtonTableCell.<TModelType> create(btnDelete, this::handleDeleteModel));
    
    return List.of(editCol, deleteCol);
  }
  
  private void handleEditModel(TModelType model)
  {
    if (model != null)
    {
      goToPane(getModelEditPane(model));
    }
  }
  
  private void handleDeleteModel(TModelType model)
  {
    if (model != null)
    {
      String deleteMsg = String.format("'%s' wirklich löschen?", model.getName());
      Alert alert = new Alert(AlertType.CONFIRMATION, deleteMsg, ButtonType.YES, ButtonType.NO);
      alert.setTitle("Auswahl löschen");
      alert.setHeaderText(null);

      Optional<ButtonType> result = alert.showAndWait();

      if (result.isPresent() && result.get() == ButtonType.YES)
      {
        int deleteCount = getCrudService().deleteById(model.getId());
        if(deleteCount > 0)
        {
          this.modelTable.getItems().remove(model);
        }
      }
    }
  }

  /**
   * Returns the CRUD service for the model.
   * @return CRUD service
   */
  protected abstract ADwbkCrudService<TModelType, ? extends IGtModelBuilder<TModelType>> getCrudService();
  
  /**
   * Creates the model specific columns that should be visible in the overview.
   * @return columns
   */
  protected abstract List<TableColumn<TModelType, ?>> createSpecificModelColumns();

  /**
   * Returns the model edit pane for the model.
   * @param model the model to edit (might be {@code null} to create new models)
   * @return pane
   */
  protected abstract AModelEditPane<TModelType> getModelEditPane(TModelType model);

  /**
   * Creates the button to create a new model (without action!).
   * @return button
   */
  protected abstract Button createNewModelButton();

}
