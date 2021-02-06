package com.lasy.dwbk.ws.wfs;

import java.util.Map;

import com.lasy.dwbk.util.Check;
import com.lasy.dwbk.ws.IRequestParameters;

/**
 * Query parameters for WMS requests.
 * @author larss
 *
 */
public class WfsRequestParameters implements IWfsRequestParameters
{
  /**
   * Creates the request parameters.
   * @param params layer request parameters
   * @return request parameters
   */
  public static IWfsRequestParameters fromRequestParameters(IRequestParameters params)
  {
    Check.notNull(params, "params");
    return new WfsRequestParameters(params);
  }
  
  private final IRequestParameters params;

  private WfsRequestParameters(IRequestParameters parameters)
  {
    this.params = parameters;
  }

  @Override
  public Map<String, String> getParams()
  {
    return params.getParams();
  }

  @Override
  public String getTypeNames()
  {
    return getParams().get(WfsQueryConst.GetFeature.TYPE_NAMES.toLowerCase());
  }
  
  
  
}
