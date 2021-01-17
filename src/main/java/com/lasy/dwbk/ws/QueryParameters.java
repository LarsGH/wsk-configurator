package com.lasy.dwbk.ws;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.lasy.dwbk.util.Check;

/**
 * Access to query parameter parts from the Layer URI.
 * @author larss
 *
 */
public class QueryParameters
{
  
  /**
   * Creates the query parameters from a layer URI.
   * @param uri the layer URI.
   * @return query parameters
   */
  public static QueryParameters fromLayerUri(String uri)
  {
    return new QueryParameters(uri);
  }
  
  private Map<String, String> params = new HashMap<String, String>();
  
  private QueryParameters(String query) 
  {
    Check.trimmedNotEmpty(query, "query");
    
    this.initParts(query);
  }

  private void initParts(String query)
  {
    String[] queryParts = query.split("&");    
    
    // handle part until first '&' - first query part starts with '?' instead of '&'!
    addParamsForFirstPart(queryParts[0]);
    
    for(int i = 1; i < queryParts.length; i++)
    {
      addParamFromQueryPart(queryParts[i]);
    }
  }
  
  private void addParamsForFirstPart(String firstPart)
  {
    String firstQueryPart = firstPart.substring( (firstPart.lastIndexOf("?")+1) );
    addParamFromQueryPart(firstQueryPart);
    
    // add base URL
    String baseUrl = firstPart.replace(firstQueryPart, "");
    this.params.put(QueryConst.BASE_URL, baseUrl);
  }

  private void addParamFromQueryPart(String queryPart) 
  {
    try {
      String[] paramParts = queryPart.trim().split("=");
      // keys always lowercase!
      this.params.put(paramParts[0].toLowerCase(), paramParts[1]);      
    } catch (Exception e)
    {
      throw new IllegalArgumentException("Invalid query (part): " + queryPart);
    }
  }
  
  /**
   * Returns the parameters.
   * @return parameters
   */
  public Map<String, String> getParams() 
  {
    return Collections.unmodifiableMap(this.params);
  }
  
  /**
   * Returns the base URL until the first parameter (ending with '?'). 
   * @return base URL
   */
  public String getBaseRequest()
  {
    return params.get(QueryConst.BASE_URL);
  }
  
  /**
   * Returns the getCapabilities request.
   * @return getCapabilities request
   */
  public String getCapablitiesRequest() {
    return params.get(QueryConst.BASE_URL) 
      + QueryConst.SERVICE + "="  + params.get(QueryConst.SERVICE) + "&"
      + QueryConst.VERSION + "="  + params.get(QueryConst.VERSION) + "&"
      + QueryConst.REQUEST + "="  + QueryConst.Operations.GET_CAPABILITIES;
  }
}
