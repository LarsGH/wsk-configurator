package com.lasy.wsk.app;

import org.geotools.data.DataStore;

import com.lasy.wsk.app.service.impl.BboxCrudService;
import com.lasy.wsk.app.service.impl.LayerCrudService;
import com.lasy.wsk.util.Check;

/**
 * Provides services. Needs to be initialized at startup!
 * 
 * @author larss
 */
public class WskServiceProvider
{
  private static WskServiceProvider instance;
  
  public static WskServiceProvider getInstance()
  {
    if(instance == null)
    {
      throw new IllegalStateException("ServiceProvider needs to be initialized at startup!");
    }
    return instance;
  }
  
  public static void initialize(DataStore store)
  {
    instance = new WskServiceProvider(store);
  }

  private BboxCrudService bboxService;
  private LayerCrudService layerService;
  
  protected WskServiceProvider(DataStore store)
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
