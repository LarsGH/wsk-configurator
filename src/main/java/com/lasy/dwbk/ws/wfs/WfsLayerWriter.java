package com.lasy.dwbk.ws.wfs;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.util.Check;
import com.lasy.dwbk.ws.ILayerWriter;
import com.lasy.dwbk.ws.wms.WmsTileResponse;

/**
 * Writes the layer content to the geopackage.
 * @author larss
 *
 */
public class WfsLayerWriter implements ILayerWriter
{
  
  private final LayerModel layer;
  
  private final ExecutorService threadPool;
  private final LinkedBlockingQueue<Future<WmsTileResponse>> responseQueue;
  
  public WfsLayerWriter(LayerModel layer)
  {
    this.layer = Check.notNull(layer, "layer");

    int poolSize = DwbkFramework.getInstance().getSettings().getWmsMaxThreads() + 1;
    this.threadPool = Executors.newFixedThreadPool(poolSize);
    this.responseQueue = new LinkedBlockingQueue<>(5);
  }
  
  @Override
  public void write()
  {
    
  }
}
