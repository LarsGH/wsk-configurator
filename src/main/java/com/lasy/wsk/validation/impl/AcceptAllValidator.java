package com.lasy.wsk.validation.impl;

import com.lasy.wsk.validation.IUserInputValidator;
/**
 * Always returns {@code true}.
 * @author larss
 *
 */
public class AcceptAllValidator implements IUserInputValidator
{

  @Override
  public boolean isValid(String input)
  {
    return true;
  }

  @Override
  public String replaceInput(String input)
  {
    return input;
  }

}
