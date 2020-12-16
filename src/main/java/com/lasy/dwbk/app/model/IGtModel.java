package com.lasy.dwbk.app.model;

import org.opengis.feature.simple.SimpleFeature;

/**
 * Common interface for all GT feature models.
 * @author larss
 */
public interface IGtModel
{

  /**
   * Returns the ID
   * @return ID
   */
  Integer getId();
  
  /**
   * Returns the feature
   * @return feature attributes
   */
  SimpleFeature getFeature();
  
}
