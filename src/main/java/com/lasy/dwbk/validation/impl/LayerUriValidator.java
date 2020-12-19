package com.lasy.dwbk.validation.impl;

import com.lasy.dwbk.validation.AUserInputValidator;

public class LayerUriValidator extends AUserInputValidator
{
  
  // TODO: layer URI pattern! + test!
  /**
   * Layer URI pattern.
   */
  private static final String URI_PATTERN = ".*";

  public LayerUriValidator()
  {
    super(URI_PATTERN);
  }

}
