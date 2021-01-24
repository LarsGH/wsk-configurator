package com.lasy.dwbk.ws.wms;

import java.util.Map;

import com.lasy.dwbk.ws.IRequestParameters;
import com.lasy.dwbk.ws.RequestParameters;

/**
 * Query parameters for WMS requests.
 * @author larss
 *
 */
public class WmsRequestParameters implements IWmsRequestParameters
{
  /**
   * Creates the query parameters from a layer URI.
   * @param uri the layer URI.
   * @return query parameters
   */
  public static IWmsRequestParameters fromLayerUri(String uri)
  {
    IRequestParameters params = RequestParameters.fromLayerUri(uri);
    return new WmsRequestParameters(params);
  }
  
  private final IRequestParameters params;

  private WmsRequestParameters(IRequestParameters parameters)
  {
    this.params = parameters;
  }

  @Override
  public Map<String, String> getParams()
  {
    return params.getParams();
  }

  @Override
  public String getVersion()
  {
    return getParams().get(WmsQueryConst.GetMap.VERSION);
  }

  @Override
  public String getLayer()
  {
    return getParams().get(WmsQueryConst.GetMap.LAYERS);
  }

  @Override
  public String getStyles()
  {
    return getParams().get(WmsQueryConst.GetMap.STYLES);
  }

  @Override
  public String getFormat()
  {
    return getParams().get(WmsQueryConst.GetMap.FORMAT);
  }

  @Override
  public boolean isTransparent()
  {
    String boolStr = getParams().getOrDefault(WmsQueryConst.GetMap.TRANSPARENT, "true");
    return Boolean.valueOf(boolStr);
  }
  
  
  
}
