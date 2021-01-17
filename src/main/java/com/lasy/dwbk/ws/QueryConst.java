package com.lasy.dwbk.ws;

/**
 * Common query constants.
 * @author larss
 *
 */
public class QueryConst
{
  /**
   * Operation constants.
   */
  public static class Operations {
    /**
     * GetCapabilities.
     */
    public static final String GET_CAPABILITIES = "GetCapabilities";
  }

  /**
   * The base URL until the first parameter (ending with '?'). <b>This is not a standard!</b>
   */
  public static final String BASE_URL = "base_url";
  
  /**
   * Service name. Value is WMS. (mandatory).
   */
  public static final String SERVICE = "service";
  
  /**
   * Service version. Value is one of 1.0.0, 1.1.0, 1.1.1, 1.3.0. (mandatory).
   */
  public static final String VERSION = "version";
  
  /**
   * Operation name. (mandatory).
   */
  public static final String REQUEST = "request"; 
  
}
