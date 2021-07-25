package com.lasy.wsk.gui.panes.edit.util;

import java.util.Collection;

import com.lasy.wsk.app.WskServiceProvider;
import com.lasy.wsk.app.model.impl.BboxModel;
import com.lasy.wsk.app.service.impl.BboxCrudService;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class BboxComboBox extends ComboBox<BboxModel>
{
  
  private BboxModel bbox;
  private Collection<BboxModel> availableBboxes;
  
  public BboxComboBox()
  {
    super();
    
    init();
  }

  private void init()
  {
    availableBboxes = bboxService().readAll();    
    getItems().addAll(availableBboxes);
    
    setConverter(new StringConverter<BboxModel>() {
      
      @Override
      public String toString(BboxModel box)
      {
        if(box == null)
        {
          return "";
        }
        return box.getName();
      }
      
      @Override
      public BboxModel fromString(String string)
      {
        return null;
      }
    });
    
    setOnAction(e -> {
      BboxModel selectedItem = getSelectionModel().getSelectedItem();
      setSelectedBbox(selectedItem);
    });
    
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
      getSelectionModel().select(box);
    }
  }
  
  private void setSelectedBbox(BboxModel bbox)
  {
    this.bbox = bbox;
  }

  private BboxCrudService bboxService()
  {
    return WskServiceProvider.getInstance().getBboxService();
  }

}
