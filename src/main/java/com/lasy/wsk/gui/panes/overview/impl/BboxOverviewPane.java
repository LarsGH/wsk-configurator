package com.lasy.wsk.gui.panes.overview.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.lasy.wsk.app.WskServiceProvider;
import com.lasy.wsk.app.model.IGtModelBuilder;
import com.lasy.wsk.app.model.impl.BboxModel;
import com.lasy.wsk.app.model.impl.LayerModel;
import com.lasy.wsk.app.service.AWskCrudService;
import com.lasy.wsk.app.service.impl.LayerCrudService;
import com.lasy.wsk.gui.panes.edit.AModelEditPane;
import com.lasy.wsk.gui.panes.edit.impl.BboxEditPane;
import com.lasy.wsk.gui.panes.overview.AOverviewPane;
import com.lasy.wsk.gui.util.GuiIcon;
import com.lasy.wsk.gui.util.GuiUtil;
import com.lasy.wsk.gui.util.ModelValueFactory;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;

/**
 * Overview pane for bounding-boxes.
 * @author larss
 *
 */
public class BboxOverviewPane extends AOverviewPane<BboxModel>
{
  /**
   * Creates the bounding-box overview pane.
   * @param mainScene the main scene
   * @return bounding-box overview pane
   */
  public static BboxOverviewPane create(Scene mainScene)
  {
    return createInitializedPane(new BboxOverviewPane(mainScene));
  }
  
  private BboxOverviewPane(Scene mainScene)
  {
    // boundingbox overview
    super(mainScene, "Boundingbox-Übersicht");
  }

  @Override
  protected Map<Button, AModelEditPane<BboxModel>> createNewModelButtonsWithTargetPane()
  {
    Map<Button, AModelEditPane<BboxModel>> btnWithTargets = new LinkedHashMap<>();
    
    Button createBtn = GuiUtil.createIconButtonWithText(
      GuiIcon.CREATE,
      // creates a new boundingbox
      "Erstellt eine neue Boundingbox",
      // create a new boundingbox
      "Neue Boundingbox erstellen");
    AModelEditPane<BboxModel> pane = getModelEditPane(null);
    
    btnWithTargets.put(createBtn, pane);
    return btnWithTargets;
  }

  @Override
  protected AWskCrudService<BboxModel, ? extends IGtModelBuilder<BboxModel>> getCrudService()
  {
    return WskServiceProvider.getInstance().getBboxService();
  }

  @Override
  protected List<TableColumn<BboxModel, ?>> createSpecificModelColumns()
  {
    TableColumn<BboxModel, String> isMapBoundaryCol = new TableColumn<>("Karten-Begrenzung");
    isMapBoundaryCol.setCellValueFactory(new ModelValueFactory<BboxModel>(bbox -> GuiUtil.createBooleanDisplayValue(bbox.isMapBoundary())));
    
    TableColumn<BboxModel, String> epsgCol = new TableColumn<>("EPSG-Code");
    epsgCol.setCellValueFactory(new ModelValueFactory<BboxModel>(bbox -> String.valueOf(bbox.getEpsg())));

    TableColumn<BboxModel, String> lonMinCol = new TableColumn<>("Länge (minimal)");
    lonMinCol.setCellValueFactory(new ModelValueFactory<BboxModel>(BboxModel::getMinLon));
    
    TableColumn<BboxModel, String> latMinCol = new TableColumn<>("Breite (minimal)");
    latMinCol.setCellValueFactory(new ModelValueFactory<BboxModel>(BboxModel::getMinLat));
    
    TableColumn<BboxModel, String> lonMaxCol = new TableColumn<>("Länge (maximal)");
    lonMaxCol.setCellValueFactory(new ModelValueFactory<BboxModel>(BboxModel::getMaxLon));
    
    TableColumn<BboxModel, String> latMaxCol = new TableColumn<>("Breite (maximal)");
    latMaxCol.setCellValueFactory(new ModelValueFactory<BboxModel>(BboxModel::getMaxLat));

    return List.of(isMapBoundaryCol, epsgCol, lonMinCol, latMinCol, lonMaxCol, latMaxCol);
  }

  @Override
  protected AModelEditPane<BboxModel> getModelEditPane(BboxModel bbox)
  {
    return BboxEditPane.create(getMainScene(), bbox);
  }

  @Override
  protected Optional<String> getDeleteNotAllowedReason(BboxModel bbox)
  {
    List<String> layersWithBbox = getLayersWithBbox(bbox);
    if(!layersWithBbox.isEmpty())
    {
      // boundingbox cannot be deleted because it is used by the following layers
      String reason = String.format("Boundingbox '%s' kann nicht gelöscht werden, da sie von folgenden Layern verwendet wird: %s",
        bbox.getName(),
        String.join(", ", layersWithBbox));
      return Optional.of(reason);
    }
    
    return Optional.empty();
  }
  
  private List<String> getLayersWithBbox(BboxModel bbox)
  {
    LayerCrudService layerCrudService = WskServiceProvider.getInstance().getLayerService();
    return layerCrudService.readAll().stream()
      .filter(layer -> Objects.equals(layer.getBboxId(), bbox.getId()))
      .map(LayerModel::getName)
      .collect(Collectors.toList());
  }

  @Override
  protected Optional<String> getCreateNotAllowedReason()
  {
    return Optional.empty();
  }

  @Override
  protected List<TableColumn<BboxModel, Button>> createAdditionalButtons()
  {
    return Collections.emptyList();
  }

}
