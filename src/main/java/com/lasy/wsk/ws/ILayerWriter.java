package com.lasy.wsk.ws;

import com.lasy.wsk.app.error.WskFrameworkException;
import com.lasy.wsk.app.model.impl.LayerModel;
import com.lasy.wsk.util.Check;
import com.lasy.wsk.ws.wfs.WfsLayerWriter;
import com.lasy.wsk.ws.wms.WmsLayerWriter;

/**
 * Common interface to write layers (WMS / WFS).
 * @author larss
 *
 */
public interface ILayerWriter
{
  
  /**
   * Creates a new layer writer.
   * @param layer the layer to write
   * @return layer writer
   */
  public static ILayerWriter createForLayer(LayerModel layer)
  {
    boolean storeLocal = Check.notNull(layer, "layer").isStoreLocal();
    if(!storeLocal)
    {
      throw WskFrameworkException.failForReason(new IllegalStateException(),
        // layer configuration does not allow local saving
        "Die Konfiguration des Layers ('%s') erlaubt keine lokale Speicherung!", layer.getLocalName());
    }
    EWebServiceType webService = layer.getWebServiceType();
    switch(webService)
    {
      case WMS: 
        return new WmsLayerWriter(layer);
      case WFS: 
        return new WfsLayerWriter(layer);
      default: 
        throw new IllegalArgumentException(String.format("Cannot find layer writer for service type: '%s'", webService));
    }
  }

  /**
   * Writes the layer content to the geopackage.
   * The content depends on the configured URI and boundingbox.
   */
  void write();
  
}
