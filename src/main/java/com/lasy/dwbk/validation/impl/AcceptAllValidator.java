package com.lasy.dwbk.validation.impl;

import com.lasy.dwbk.validation.IUserInputValidator;
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
