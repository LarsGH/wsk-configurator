package com.lasy.dwbk.gui.panes.overview.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.error.DwbkFrameworkException;
import com.lasy.dwbk.app.error.ErrorModule;
import com.lasy.dwbk.app.model.IGtModelBuilder;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.app.service.ADwbkCrudService;
import com.lasy.dwbk.app.service.impl.BboxCrudService;
import com.lasy.dwbk.db.util.DbScriptUtil;
import com.lasy.dwbk.gui.panes.edit.AModelEditPane;
import com.lasy.dwbk.gui.panes.edit.impl.LayerEditPane;
import com.lasy.dwbk.gui.panes.overview.AOverviewPane;
import com.lasy.dwbk.gui.util.ButtonTableCell;
import com.lasy.dwbk.gui.util.GuiIcon;
import com.lasy.dwbk.gui.util.GuiUtil;
import com.lasy.dwbk.gui.util.ModelValueFactory;
import com.lasy.dwbk.ws.ILayerWriter;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    return GuiUtil.createIconButtonWithText(GuiIcon.CREATE, "Erstellt einen neuen Layer", "Neuen Layer erstellen");
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

    TableColumn<LayerModel, String> lastDownloadCol = new TableColumn<>("Zuletzt heruntergeladen");
    lastDownloadCol.setCellValueFactory(new ModelValueFactory<LayerModel>(layer -> {
      if(layer.isStoreLocal())
      {
        Optional<LocalDateTime> lastDownloadDate = layer.getLastDownloadDate();
        if (lastDownloadDate.isPresent())
        {
          LocalDateTime dlDate = lastDownloadDate.get();
          
          return layer.getLastChangedDate().isAfter(dlDate) 
            ? String.join(System.lineSeparator(), dlDate.toString(), "* Konfigurationsänderungen!")
            : dlDate.toString();
        }
        return "NICHT LOKAL GESPEICHERT!";
      }
      // show nothing for layers that are not allowed to be stored locally
      return "";
    }));

    return List.of(isVisibleCol, lastDownloadCol);
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
    if (noBboxesExist())
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

  @Override
  protected List<TableColumn<LayerModel, Button>> createAdditionalButtons()
  {
    TableColumn<LayerModel, Button> downloadCol = new TableColumn<>("Herunterladen");
    Supplier<Button> btnDownload = () -> GuiUtil.createIconButton(GuiIcon.DOWNLOAD, "Speichert den Layer lokal");
    downloadCol.setCellFactory(ButtonTableCell.<LayerModel> create(btnDownload, this::handleDownload,
      // just show button if it can be stored local
      layer -> layer.isStoreLocal()));

    return List.of(downloadCol);
  }

  private void handleDownload(LayerModel layer)
  {
    if (layer != null)
    {
      try
      {
        Alert alert = createDownloadAlert(layer);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES)
        {
          ILayerWriter writer = ILayerWriter.createForLayer(layer);
          writer.write();
          
          layer.setLastDownloadDate(LocalDateTime.now());
          DwbkServiceProvider.getInstance().getLayerService().update(layer);
        }
      }
      catch (Exception e)
      {
        throw ErrorModule.createFrameworkException(e, t -> DwbkFrameworkException
          .failForReason(t, "Fehler beim Speichern des lokalen layers '%s'", layer.getName()));
      }

    }
  }

  private Alert createDownloadAlert(LayerModel layer)
  {
    String msg = String.format("'%s' jetzt herunterladen? Das Herunterladen kann einige Minuten dauern.", layer.getName());
    Alert alert = new Alert(AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
    alert.setTitle("Layer Herunterladen");
    alert.setHeaderText(null);
    return alert;
  }

  @Override
  protected void doHandleDelete(LayerModel layer)
  {
    DbScriptUtil.deleteLocalLayerContentIfPresent(layer);
  }

}
