package com.lasy.wsk.validation.impl;

import com.lasy.wsk.validation.AUserInputValidator;

public class LayerServiceUriValidator extends AUserInputValidator
{
  
  /**
   * Validates that a WMS or WFS service is requested.
   */
  private static final String LAYER_SERVICE_PATTERN = ".*(service=[W|w][M|m|F|f][S|s]){1}.*";

  public LayerServiceUriValidator()
  {
    super(LAYER_SERVICE_PATTERN);
  }

}
