package com.lasy.dwbk.ws.wms;

import java.util.Map;

import com.lasy.dwbk.util.Check;
import com.lasy.dwbk.ws.IRequestParameters;

/**
 * Query parameters for WMS requests.
 * @author larss
 *
 */
public class WmsRequestParameters implements IWmsRequestParameters
{
  
  /**
   * Creates the request parameters.
   * @param params layer request parameters
   * @return request parameters
   */
  public static IWmsRequestParameters fromRequestParameters(IRequestParameters params)
  {
    Check.notNull(params, "params");
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
