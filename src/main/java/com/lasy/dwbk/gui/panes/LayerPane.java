package com.lasy.dwbk.gui.panes;

import com.lasy.dwbk.app.model.impl.LayerModel;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class LayerPane extends AModelEditPane<LayerModel>
{

  /**
   * Layer create / edit GUI.
   * @param mainScene main scene
   * @param layer the layer (may be {@code null} to create new layers).
   */
  public LayerPane(Scene mainScene, LayerModel layer)
  {
    super(mainScene, "Layer Verwalten", layer);
  }



  @Override
  protected void doHandleUpdateModel(LayerModel model)
  {
    // TODO Auto-generated method stub
    
  }



  @Override
  protected void doHandleCreateNewModel()
  {
    // TODO Auto-generated method stub
    
  }



  @Override
  void validateUserInput() throws IllegalArgumentException
  {
    // TODO Auto-generated method stub
    
  }



  @Override
  protected Node createContent()
  {
    // TODO Auto-generated method stub
VBox attributes = new VBox(10);
    
    attributes.getChildren().add(new Label("test1"));
    attributes.getChildren().add(new Label("test2"));
    
    return attributes;
  }
  
  

}
