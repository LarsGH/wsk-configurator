package com.lasy.dwbk.app;

import org.geotools.data.DataStore;

import com.lasy.dwbk.app.service.impl.BboxCrudService;
import com.lasy.dwbk.app.service.impl.LayerCrudService;
import com.lasy.dwbk.util.Check;

/**
 * Provides services. Needs to be initialized at startup!
 * 
 * @author larss
 */
public class DwbkServiceProvider
{
  private static DwbkServiceProvider instance;
  
  public static DwbkServiceProvider getInstance()
  {
    if(instance == null)
    {
      throw new IllegalStateException("ServiceProvider needs to be initialized at startup!");
    }
    return instance;
  }
  
  public static void initialize(DataStore store)
  {
    instance = new DwbkServiceProvider(store);
  }

  private BboxCrudService bboxService;
  private LayerCrudService layerService;
  
  protected DwbkServiceProvider(DataStore store)
  {
    Check.notNull(store, "store");
    registerServices(store);
  }

  private void registerServices(DataStore store)
  {
    bboxService = new BboxCrudService(store);
    layerService = new LayerCrudService(store);
  }
  
  public BboxCrudService getBboxService()
  {
    return bboxService;
  }
  
  public LayerCrudService getLayerService()
  {
    return layerService;
  }

}
