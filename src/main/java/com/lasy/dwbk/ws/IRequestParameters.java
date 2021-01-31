package com.lasy.dwbk.ws;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
  
  /**
   * Returns the web service type.
   * @return web service type
   */
  default EWebService getWebService()
  {
    Map<String, String> params = getParams();
    String service = params.get(QueryConst.SERVICE).toUpperCase();
    return EWebService.valueOf(service);
  }
  
  /**
   * Returns the parameters as JSON string.
   * @return parameters as JSON string
   */
  default String toJson()
  {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    return gson.toJson(getParams());
  }
}
