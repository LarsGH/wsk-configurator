package com.lasy.dwbk.validation.impl;

import com.lasy.dwbk.validation.AUserInputValidator;

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
