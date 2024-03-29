package com.lasy.wsk.gui.panes.overview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.lasy.wsk.app.model.IGtModel;
import com.lasy.wsk.app.model.IGtModelBuilder;
import com.lasy.wsk.app.service.AWskCrudService;
import com.lasy.wsk.gui.panes.AWskPane;
import com.lasy.wsk.gui.panes.edit.AModelEditPane;
import com.lasy.wsk.gui.util.GuiIcon;
import com.lasy.wsk.gui.util.GuiUtil;
import com.lasy.wsk.gui.util.ModelValueFactory;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public abstract class AOverviewPane<TModelType extends IGtModel> extends AWskPane
{

  private TableView<TModelType> modelTable;

  public AOverviewPane(Scene mainScene, String header)
  {
    super(mainScene, header);

    modelTable = createModelTable();
  }

  private HBox createMainButtonBox()
  {
    // overview. go to overview
    Button mainBtn = GuiUtil.createIconButtonWithText(GuiIcon.HOME_DB, "Übersicht", "Wechsel zu Übersicht");
    mainBtn.setOnAction(e -> {
      goToMainPane();
    });

    HBox box = new HBox(GuiUtil.DEFAULT_SPACING, mainBtn);
    box.setAlignment(Pos.CENTER_RIGHT);
    return box;
  }

  @Override
  protected Node createCenterContent()
  {
    BorderPane pane = new BorderPane();
    List<Button> createButtons = createNewModelButtonWithAction();
    HBox hBox = new HBox(GuiUtil.DEFAULT_SPACING);
    hBox.getChildren().addAll(createButtons);
    
    BorderPane.setMargin(hBox, new Insets(GuiUtil.DEFAULT_SPACING, 0, GuiUtil.DEFAULT_SPACING, 0));
    pane.setTop(hBox);

    Collection<TModelType> models = getCrudService().readAll();
    modelTable.getItems().addAll(models);
    pane.setCenter(modelTable);

    return pane;
  }

  @Override
  protected Node createBottomContent()
  {
    return createMainButtonBox();
  }

  private List<Button> createNewModelButtonWithAction()
  {
    List<Button> buttons = new ArrayList<>();
    Map<Button, AModelEditPane<TModelType>> btnTargetMapping = createNewModelButtonsWithTargetPane();
    btnTargetMapping.forEach((btn, pane) -> {
      btn.setOnMouseClicked(e -> {
        // Prevent creation for reasons
        Optional<String> createNotAllowedReason = getCreateNotAllowedReason();
        if (createNotAllowedReason.isPresent())
        {
          // creation not possible
          Alert alert = GuiUtil.createOkAlert(AlertType.WARNING, "Erstellen nicht möglich", createNotAllowedReason.get());
          alert.show();
          return;
        }
        
        goToPane(pane);
      });
      buttons.add(btn);
    });
    return buttons;
  }

  /**
   * Returns the reason why a create might not be allowed.
   * @return reason why a create might not be allowed
   */
  protected abstract Optional<String> getCreateNotAllowedReason();

  private TableView<TModelType> createModelTable()
  {
    TableView<TModelType> modelTable = new TableView<TModelType>();
    modelTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    modelTable.getColumns().addAll(createNameAndDescriptionColumns());
    modelTable.getColumns().addAll(createSpecificModelColumns());
    modelTable.getColumns().addAll(createChangedAndButtonColumns());

    return modelTable;
  }

  private List<TableColumn<TModelType, ?>> createNameAndDescriptionColumns()
  {
    TableColumn<TModelType, String> nameCol = new TableColumn<>("Name");
    nameCol.setCellValueFactory(new ModelValueFactory<TModelType>(TModelType::getName));

    TableColumn<TModelType, String> descriptionCol = new TableColumn<>("Beschreibung");
    descriptionCol.setCellValueFactory(new ModelValueFactory<TModelType>(bbox -> bbox.getDescription().orElse("")));

    return List.of(nameCol, descriptionCol);
  }

  private List<TableColumn<TModelType, ?>> createChangedAndButtonColumns()
  {
    List<TableColumn<TModelType, ?>> colList = new ArrayList<>();

    TableColumn<TModelType, String> lastChangeCol = new TableColumn<>("Letzte Änderung");
    lastChangeCol.setCellValueFactory(new ModelValueFactory<TModelType>(model -> model.getLastChangedDate().toString()));
    colList.add(lastChangeCol);

    // add more buttons before edit if necessary
    List<TableColumn<TModelType, Button>> additionalButtons = createAdditionalButtons();
    colList.addAll(additionalButtons);

    TableColumn<TModelType, Button> editCol = GuiUtil.createGridButtonColumn("Editieren",
      // shows the edit mask
      () -> GuiUtil.createIconButton(GuiIcon.EDIT, "Ruft die Editieren-Maske auf"), 
      this::handleEditModel,
      null);
    colList.add(editCol);

    TableColumn<TModelType, Button> deleteCol = GuiUtil.createGridButtonColumn("Löschen",
      // deletes the selection
      () -> GuiUtil.createIconButton(GuiIcon.DELETE, "Löscht die Auswahl"), 
      this::handleDeleteModel,
      null);
    colList.add(deleteCol);

    return colList;
  }

  /**
   * Creates new buttons that are inserted before the edit button.
   * @return new buttons
   */
  protected abstract List<TableColumn<TModelType, Button>> createAdditionalButtons();

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
      // Prevent delete for reasons
      Optional<String> deleteNotAllowedReason = getDeleteNotAllowedReason(model);
      if (deleteNotAllowedReason.isPresent())
      {
        // deletion not possible
        Alert alert = GuiUtil.createOkAlert(AlertType.WARNING, "Löschen nicht möglich", deleteNotAllowedReason.get());
        alert.show();
      }
      else
      {
        // delete after confirmation
        Alert alert = createDeleteAlert(model);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES)
        {
          // handle more delete logic besides CRUD service call if necessary
          doHandleDelete(model);

          int deleteCount = getCrudService().deleteById(model.getId());
          if (deleteCount > 0)
          {
            this.modelTable.getItems().remove(model);
          }
        }
      }
    }
  }

  /**
   * Handle more delete logic (besides CRUD service call)
   * @param model the model to delete
   */
  protected void doHandleDelete(TModelType model)
  {
    // By default do nothing
  }

  private Alert createDeleteAlert(TModelType model)
  {
    // delete *?
    String deleteMsg = String.format("'%s' wirklich löschen?", model.getName());
    Alert alert = new Alert(AlertType.CONFIRMATION, deleteMsg, ButtonType.YES, ButtonType.NO);
    // delete selection
    alert.setTitle("Auswahl löschen");
    alert.setHeaderText(null);
    return alert;
  }

  /**
   * Returns the reason why a delete might not be allowed.
   * @param model the model to check
   * @return reason why a delete might not be allowed
   */
  protected abstract Optional<String> getDeleteNotAllowedReason(TModelType model);

  /**
   * Returns the CRUD service for the model.
   * @return CRUD service
   */
  protected abstract AWskCrudService<TModelType, ? extends IGtModelBuilder<TModelType>> getCrudService();

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
   * Creates the buttons to create a new model (without action!).
   * Mapping defines button -> target pane.
   * @return buttons
   */
  protected abstract Map<Button, AModelEditPane<TModelType>> createNewModelButtonsWithTargetPane();

}
