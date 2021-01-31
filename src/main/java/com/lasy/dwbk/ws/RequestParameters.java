package com.lasy.dwbk.ws;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.lasy.dwbk.util.Check;

/**
 * Access to query parameter parts from the Layer URI.
 * @author larss
 *
 */
public class RequestParameters implements IRequestParameters
{
  
  /**
   * Creates the query parameters from a layer URI.
   * @param uri the layer URI.
   * @return query parameters
   */
  public static IRequestParameters fromLayerUri(String uri)
  {
    Check.trimmedNotEmpty(uri, "uri");
    
    Map<String, String> params = createParts(uri);
    return new RequestParameters(params);
  }
  
  /**
   * Creates the query parameters from a stored JSON.
   * @param json the stored JSON
   * @return query parameters
   */
  public static IRequestParameters fromJson(String json)
  {
    Check.trimmedNotEmpty(json, "json");
    
    @SuppressWarnings("unchecked")
    Map<String, String> map = new Gson().fromJson(json, Map.class);
    return new RequestParameters(map);
  }
  
  private final Map<String, String> params;
  
  private RequestParameters(Map<String, String> params) 
  {
    // always use sorted params
    Map<String, String> paramsSortedByKey = params.entrySet().stream()
      .sorted((e1,e2) -> e1.getKey().compareTo(e2.getKey()))
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedHashMap::new));
    
    this.params = Collections.unmodifiableMap(paramsSortedByKey);
  }

  private static Map<String, String> createParts(String query)
  {
    Map<String, String> parts = new HashMap<>();
    String[] queryParts = query.split("&");  
    
    // handle part until first '&' - first query part starts with '?' instead of '&'!
    addParamsForFirstPart(queryParts[0], parts);
    
    for(int i = 1; i < queryParts.length; i++)
    {
      addParamFromQueryPart(queryParts[i], parts);
    }
    
    return parts;
  }
  
  private static void addParamsForFirstPart(String firstPart, Map<String, String> parts)
  {
    String firstQueryPart = firstPart.substring( (firstPart.lastIndexOf("?")+1) );
    addParamFromQueryPart(firstQueryPart, parts);
    
    // add base URL
    String baseUrl = firstPart.replace(firstQueryPart, "");
    parts.put(QueryConst.BASE_URL, baseUrl);
  }

  private static void addParamFromQueryPart(String queryPart, Map<String, String> parts) 
  {
    try {
      String[] paramParts = queryPart.trim().split("=");
      // keys always lowercase!
      parts.put(paramParts[0].toLowerCase(), paramParts[1]);      
    } catch (Exception e)
    {
      throw new IllegalArgumentException("Invalid query (part): " + queryPart);
    }
  }

  @Override
  public Map<String, String> getParams() 
  {
    return this.params;
  }
}
