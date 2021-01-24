package com.lasy.dwbk.ws;

import java.util.Map;

/**
 * Standard interface for all service request parameters.
 * @author larss
 *
 */
public interface IRequestParameters
{

  /**
   * Returns the parameters.
   * @return parameters
   */
  Map<String, String> getParams();
  
  /**
   * Returns the base URL until the first parameter (ending with '?'). 
   * @return base URL
   */
  default String getBaseRequest()
  {
    return getParams().get(QueryConst.BASE_URL);
  }
  
  /**
   * Returns the getCapabilities request.
   * @return getCapabilities request
   */
  default String getCapablitiesRequest() {
    Map<String, String> params = getParams();
    return params.get(QueryConst.BASE_URL) 
      + QueryConst.SERVICE + "="  + params.get(QueryConst.SERVICE) + "&"
      + QueryConst.VERSION + "="  + params.get(QueryConst.VERSION) + "&"
      + QueryConst.REQUEST + "="  + QueryConst.Operations.GET_CAPABILITIES;
  }
}
