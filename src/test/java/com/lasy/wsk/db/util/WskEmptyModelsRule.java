package com.lasy.wsk.db.util;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.lasy.wsk.app.WskServiceProvider;
import com.lasy.wsk.app.service.impl.BboxCrudService;
import com.lasy.wsk.app.service.impl.LayerCrudService;

public class WskEmptyModelsRule extends TestWatcher
{
  
  @Override
  protected void finished(Description description)
  {
    deleteAll();
  }
  
  @Override
  protected void starting(Description description)
  {
    deleteAll();
  }
  
  private void deleteAll()
  {
    WskServiceProvider serviceProvider = WskServiceProvider.getInstance();
    
    LayerCrudService layerService = serviceProvider.getLayerService();
    layerService.readAll().forEach(layer -> layerService.deleteById(layer.getId()));
    
    BboxCrudService bboxService = serviceProvider.getBboxService();
    bboxService.readAll().forEach(bbox -> bboxService.deleteById(bbox.getId()));
  }

}
