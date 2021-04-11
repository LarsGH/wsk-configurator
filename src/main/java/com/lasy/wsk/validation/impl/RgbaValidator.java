package com.lasy.wsk.validation.impl;

import java.util.Optional;

import com.lasy.wsk.util.Is;
import com.lasy.wsk.validation.IUserInputValidator;
import com.lasy.wsk.ws.wfs.style.RgbaStyleValue;
/**
 * RGBA-Color Validator.
 * @author larss
 *
 */
public class RgbaValidator implements IUserInputValidator
{
  @Override
  public boolean isValid(String input)
  {
    // RGBA is optional if there is no mandatory constraint!
    if(Is.nullOrTrimmedEmpty(input))
    {
      return true;
    }
    Optional<RgbaStyleValue> rgba = RgbaStyleValue.fromConfigString(input);
    return rgba.isPresent();
  }

  @Override
  public String replaceInput(String input)
  {
    return input;
  }

}
