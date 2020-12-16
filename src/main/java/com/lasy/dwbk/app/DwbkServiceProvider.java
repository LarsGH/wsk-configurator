package com.lasy.dwbk.app;

import org.geotools.data.DataStore;

import com.google.common.base.Preconditions;
import com.lasy.dwbk.app.service.impl.BboxCrudService;
import com.lasy.dwbk.app.service.impl.LayerCrudService;

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
    Preconditions.checkNotNull(store);
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
