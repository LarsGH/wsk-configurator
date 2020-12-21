package com.lasy.dwbk.gui.panes.overview.impl;

import java.util.List;

import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.model.IGtModelBuilder;
import com.lasy.dwbk.app.model.impl.BboxModel;
import com.lasy.dwbk.app.service.ADwbkCrudService;
import com.lasy.dwbk.gui.panes.edit.AModelEditPane;
import com.lasy.dwbk.gui.panes.edit.impl.BboxEditPane;
import com.lasy.dwbk.gui.panes.overview.AOverviewPane;
import com.lasy.dwbk.gui.util.GuiIcon;
import com.lasy.dwbk.gui.util.GuiUtil;
import com.lasy.dwbk.gui.util.ModelValueFactory;

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
    super(mainScene, "Boundingbox-Übersicht");
  }

  @Override
  protected Button createNewModelButton()
  {
    return GuiUtil.createIconButtonWithText(
      GuiIcon.CREATE, 
      "Erstellt eine neue Boundingbox",  
      "Neue Boundingbox erstellen");
  }

  @Override
  protected ADwbkCrudService<BboxModel, ? extends IGtModelBuilder<BboxModel>> getCrudService()
  {
    return DwbkServiceProvider.getInstance().getBboxService();
  }

  @Override
  protected List<TableColumn<BboxModel, ?>> createSpecificModelColumns()
  {
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

    return List.of(epsgCol, lonMinCol, latMinCol, lonMaxCol, latMaxCol);
  }

  @Override
  protected AModelEditPane<BboxModel> getModelEditPane(BboxModel model)
  {
    return BboxEditPane.create(getMainScene(), model);
  }

}
