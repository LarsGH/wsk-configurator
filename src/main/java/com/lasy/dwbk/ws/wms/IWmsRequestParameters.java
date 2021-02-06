package com.lasy.dwbk.ws.wms;

import com.lasy.dwbk.ws.IRequestParameters;

/**
 * Interface for WMS request parameters.
 * @author larss
 *
 */
public interface IWmsRequestParameters extends IRequestParameters
{

  /**
   * Returns the layers parameter value.
   * @return layers
   */
  String getLayer();

  /**
   * Returns the styles parameter value.
   * @return styles
   */
  String getStyles();

  /**
   * Returns the format parameter value.
   * @return format
   */
  String getFormat();

  /**
   * Returns the transparent parameter value. Default is {@code true}.
   * @return {@code true} if transparency should be used.
   */
  boolean isTransparent();
  

}
