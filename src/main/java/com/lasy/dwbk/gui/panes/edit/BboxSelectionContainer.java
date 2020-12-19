package com.lasy.dwbk.gui.panes.edit;

import java.util.Collection;

import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.model.impl.BboxModel;
import com.lasy.dwbk.app.service.impl.BboxCrudService;
import com.lasy.dwbk.gui.util.GuiIcon;
import com.lasy.dwbk.gui.util.GuiUtil;

import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class BboxSelectionContainer extends HBox
{
  
  private static final String DEFAULT_BBOX_VALUE = "Keine Boundingbox gewählt";
  
  private Label bboxNameLbl;
  private Button selectionDialogBtn;

  private BboxModel bbox;
  private Collection<BboxModel> availableBboxes;
  
  public BboxSelectionContainer()
  {
    super(GuiUtil.DEFAULT_SPACING);
    
    init();
  }

  private void init()
  {
    availableBboxes = bboxService().readAll();

    bboxNameLbl = GuiUtil.createBoldLabel(DEFAULT_BBOX_VALUE);
    selectionDialogBtn = createSelectionDialogButton();
    
    getChildren().addAll(bboxNameLbl, selectionDialogBtn);
  }

  private Button createSelectionDialogButton()
  {
    Button btn = GuiUtil.createIconButton(GuiIcon.EDIT, "Bounding-Box Auswahl");
    btn.setOnAction(e -> {
      ChoiceDialog<BboxModel> dlg = createBboxSelectionDialog();
      BboxModel box = dlg.showAndWait().orElse(null);
      setSelectedBbox(box);
    });
    return btn;
  }

  private ChoiceDialog<BboxModel> createBboxSelectionDialog()
  {
    // TODO: HIER WEITER - display value!
    ChoiceDialog<BboxModel> dlg = new ChoiceDialog<BboxModel>(bbox, availableBboxes);
    dlg.setHeaderText(null);
    dlg.setTitle("Boundingbox Auswahl");
    return dlg;
  }
  
  /**
   * Returns the id of the selected bounding-box.
   * @return id
   */
  public Integer getSelectedBboxId()
  {
    if(bbox != null)
    {
      return bbox.getId();
    }
    return null;
  }
  
  /**
   * Sets the initially selected bounding-box.
   */
  public void setSelectedBboxById(Integer id)
  {
    if(id != null)
    {
      BboxModel box = bboxService().readById(id).orElse(null);
      setSelectedBbox(box);
    }
  }
  
  private void setSelectedBbox(BboxModel bbox)
  {
    this.bbox = bbox;
    updateBboxNameLbl();
  }
  
  private void updateBboxNameLbl()
  {
    String bboxLblValue = bbox == null
      ? DEFAULT_BBOX_VALUE
      : bbox.getName();
    
    bboxNameLbl.setText(bboxLblValue);
  }

  private BboxCrudService bboxService()
  {
    return DwbkServiceProvider.getInstance().getBboxService();
  }

}
