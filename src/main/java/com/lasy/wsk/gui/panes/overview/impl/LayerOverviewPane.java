package com.lasy.wsk.gui.panes.overview.impl;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.lasy.wsk.app.WskServiceProvider;
import com.lasy.wsk.app.error.ErrorModule;
import com.lasy.wsk.app.error.WskFrameworkException;
import com.lasy.wsk.app.model.IGtModelBuilder;
import com.lasy.wsk.app.model.impl.LayerModel;
import com.lasy.wsk.app.service.AWskCrudService;
import com.lasy.wsk.app.service.impl.BboxCrudService;
import com.lasy.wsk.db.util.DbScriptUtil;
import com.lasy.wsk.gui.panes.edit.AModelEditPane;
import com.lasy.wsk.gui.panes.edit.impl.WfsLayerEditPane;
import com.lasy.wsk.gui.panes.edit.impl.WmsLayerEditPane;
import com.lasy.wsk.gui.panes.overview.AOverviewPane;
import com.lasy.wsk.gui.util.GuiIcon;
import com.lasy.wsk.gui.util.GuiUtil;
import com.lasy.wsk.gui.util.ModelValueFactory;
import com.lasy.wsk.ws.EWebServiceType;
import com.lasy.wsk.ws.ILayerWriter;

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
  protected Map<Button, AModelEditPane<LayerModel>> createNewModelButtonsWithTargetPane()
  {
    Map<Button, AModelEditPane<LayerModel>> btnWithTargets = new LinkedHashMap<>();
    
    Button wmsBtn = GuiUtil.createIconButtonWithText(
      GuiIcon.CREATE,
      // creates a new WMS layer
      "Erstellt einen neuen WMS Layer", 
      "Neuen WMS Layer erstellen");
    AModelEditPane<LayerModel> wmsPane = WmsLayerEditPane.create(getMainScene(), null);
    btnWithTargets.put(wmsBtn, wmsPane);
    
    Button wfsBtn = GuiUtil.createIconButtonWithText(
      GuiIcon.CREATE,
      // creates a new WFS layer
      "Erstellt einen neuen WFS Layer", 
      "Neuen WFS Layer erstellen");
    AModelEditPane<LayerModel> wfsPane = WfsLayerEditPane.create(getMainScene(), null);
    btnWithTargets.put(wfsBtn, wfsPane);
    
    return btnWithTargets;
  }

  @Override
  protected AWskCrudService<LayerModel, ? extends IGtModelBuilder<LayerModel>> getCrudService()
  {
    return WskServiceProvider.getInstance().getLayerService();
  }

  @Override
  protected List<TableColumn<LayerModel, ?>> createSpecificModelColumns()
  {
    TableColumn<LayerModel, String> serviceCol = new TableColumn<>("Service");
    serviceCol.setCellValueFactory(new ModelValueFactory<LayerModel>(layer -> layer.getWebServiceType().toString()));

    // initially visible
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
            // configuration changes
            ? String.join(System.lineSeparator(), dlDate.toString(), "* Konfigurationsänderungen!")
            : dlDate.toString();
        }
        // NOT SAVED LOCALLY
        return "NICHT LOKAL GESPEICHERT!";
      }
      // show nothing for layers that are not allowed to be stored locally
      return "";
    }));    

    return List.of(serviceCol, isVisibleCol, lastDownloadCol);
  }

  @Override
  protected AModelEditPane<LayerModel> getModelEditPane(LayerModel model)
  {
    if(model != null)
    {
      EWebServiceType webServiceType = model.getWebServiceType();
      if(Objects.equals(webServiceType, EWebServiceType.WMS))
      {
        return WmsLayerEditPane.create(getMainScene(), model);
      }
      else if(Objects.equals(webServiceType, EWebServiceType.WFS))
      {
        return WfsLayerEditPane.create(getMainScene(), model);
      }
    }
      
    return null;
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
      // at least one boundingbox must be present to assign the map boundary
      return Optional.of("Es muss mindestens eine Boundingbox vorhanden sein um den Kartenausschnitt festzulegen!");
    }
    return Optional.empty();
  }

  private boolean noBboxesExist()
  {
    BboxCrudService bboxService = WskServiceProvider.getInstance().getBboxService();
    return bboxService.readAll().isEmpty();
  }

  @Override
  protected List<TableColumn<LayerModel, Button>> createAdditionalButtons()
  {
    // download
    TableColumn<LayerModel, Button> downloadCol = GuiUtil.createGridButtonColumn("Herunterladen",
      // saves the layer locally
      () -> GuiUtil.createIconButton(GuiIcon.DOWNLOAD, "Speichert den Layer lokal"), 
      this::handleDownload,
      // just show button if it can be stored local
      layer -> layer.isStoreLocal());

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
        }
      }
      catch (Exception e)
      {
        layer.setLastDownloadDate(null);
        throw ErrorModule.createFrameworkException(e, t -> WskFrameworkException
          // error while saving the local layer *
          .failForReason(t, "Fehler beim Speichern des lokalen layers '%s'", layer.getName()));
      }
      finally
      {
        WskServiceProvider.getInstance().getLayerService().update(layer);
      }
    }
  }

  private Alert createDownloadAlert(LayerModel layer)
  {
    // download * now? the download may take some minutes.
    String msg = String.format("'%s' jetzt herunterladen? Das Herunterladen kann einige Minuten dauern.", layer.getName());
    Alert alert = new Alert(AlertType.CONFIRMATION, msg, ButtonType.YES, ButtonType.NO);
    // download layer
    alert.setTitle("Layer Herunterladen");
    alert.setHeaderText(null);
    return alert;
  }

  @Override
  protected void doHandleDelete(LayerModel layer)
  {
    DbScriptUtil.deleteLocalWmsLayerContentIfPresent(layer);
  }

}
