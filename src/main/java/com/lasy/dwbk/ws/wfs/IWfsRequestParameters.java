package com.lasy.dwbk.ws.wfs;

import com.lasy.dwbk.ws.IRequestParameters;

/**
 * Interface for WMS request parameters.
 * @author larss
 *
 */
public interface IWfsRequestParameters extends IRequestParameters
{
  
  /**
   * Returns the layer names.
   * @return layer names
   */
  String getTypeNames();

}
