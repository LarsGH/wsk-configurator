package com.lasy.dwbk.db.util;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.service.impl.BboxCrudService;
import com.lasy.dwbk.app.service.impl.LayerCrudService;

public class DwbkEmptyModelsRule extends TestWatcher
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
    DwbkServiceProvider serviceProvider = DwbkServiceProvider.getInstance();
    
    LayerCrudService layerService = serviceProvider.getLayerService();
    layerService.readAll().forEach(layer -> layerService.deleteById(layer.getId()));
    
    BboxCrudService bboxService = serviceProvider.getBboxService();
    bboxService.readAll().forEach(bbox -> bboxService.deleteById(bbox.getId()));
  }

}
