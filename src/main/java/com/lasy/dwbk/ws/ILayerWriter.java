package com.lasy.dwbk.ws;

import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.util.Check;
import com.lasy.dwbk.ws.wfs.WfsLayerWriter;
import com.lasy.dwbk.ws.wms.WmsLayerWriter;

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
    Check.notNull(layer, "layer");
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
