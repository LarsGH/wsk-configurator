package com.lasy.dwbk.validation.impl;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link TextOnlyValidator}.
 * @author larss
 *
 */
public class TextOnlyValidatorTest
{
  private static TextOnlyValidator sut;

  @BeforeClass
  public static void setUp()
  {
    sut = new TextOnlyValidator();
  }

  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithString()
  {
    boolean isValid = sut.isValid("asdf");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithEmptyString()
  {
    boolean isValid = sut.isValid("");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithNull()
  {
    boolean isValid = sut.isValid(null);
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithSpace()
  {
    boolean isValid = sut.isValid(" ");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsNotValidWithNumber()
  {
    boolean isValid = sut.isValid("42");
    Assertions.assertThat(isValid).isFalse();
  }
  
}
