package com.lasy.dwbk.ws.wfs;

import com.lasy.dwbk.ws.QueryConst;

/**
 * WFS query constants. (https://docs.geoserver.org/latest/en/user/services/wfs/reference.html)
 * @author larss
 *
 */
public class WfsQueryConst
{
  /**
   * WFS operation constants.
   */
  public static class Operations extends com.lasy.dwbk.ws.QueryConst.Operations {
    
    /**
     * DescribeFeatureType: Returns a description of feature types supported by a WFS service.
     */
    public static final String DESCRIBE_FEATURE_TYPE = "DescribeFeatureType";
    
    /**
     * GetFeature: Returns a selection of features from a data source including geometry and attribute values.
     */
    public static final String GET_FEATURE = "GetFeature";
    
    /**
     * LockFeature: Prevents a feature from being edited through a persistent feature lock.
     */
    public static final String LOCK_FEATURE = "LockFeature ";
    
    /**
     * Transaction: Edits existing feature types by creating, updating, and deleting.
     */
    public static final String TRANSACTION = "Transaction";
  }

  /**
   * GetFeature constants.
   * @author larss
   *
   */
  public static class GetFeature extends QueryConst {
    
    /**
     * Layers to display on map. Format: {@code namespace:featuretype}. (mandatory)
     */
    public static final String TYPE_NAMES = "typeNames";
    
    /**
     *  ID of a specific feature to query.
     */
    public static final String FEATURE_ID = "featureID";
    
    /**
     * Maximum number of features. <b>version WFS 2.0.0 only!</b>.
     * Use MAX_FEATURES for versions below 2.0.0!
     */
    public static final String COUNT = "count";
    
    /**
     * Maximum number of features. <b>version below WFS 2.0.0 only!</b>.
     * For version 2.0.0 use COUNT!
     */
    public static final String MAX_FEATURES = "maxFeatures";
    
    /**
     * Sort by an attribute.
     * <ol>
     * <li>sortBy=<attribute>+A -> ascending</li>
     * <li>sortBy=<attribute>+D -> descending</li>
     * </ol>
     */
    public static final String SORT_BY = "sortBy";
    
    // more attributes...
    
  }
  
  
  
  
}
