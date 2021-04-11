package com.lasy.wsk.validation;

import com.lasy.wsk.util.Check;

/**
 * Common validator implementation for {@link IUserInputValidator}.
 * @author larss
 *
 */
public abstract class AUserInputValidator implements IUserInputValidator
{

  private final String validPattern;

  protected AUserInputValidator(String validPattern)
  {
    this.validPattern = Check.trimmedNotEmpty(validPattern, "validPattern");
  }
  
  @Override
  public String replaceInput(String input)
  {
    return input;
  }
  
  @Override
  public boolean isValid(String input)
  {
    input = getTrimmedInput(input);
    return input.matches(validPattern);
  }

  private String getTrimmedInput(String input)
  {
    if(input == null)
    {
      return "";
    }
    return input.trim();
  }
  
}
