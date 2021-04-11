package com.lasy.wsk.validation.impl;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lasy.wsk.validation.impl.IntegersOnlyValidator;

/**
 * Tests {@link IntegersOnlyValidator}.
 * @author larss
 *
 */
public class IntegersOnlyValidatorTest
{

  private static IntegersOnlyValidator sut;

  @BeforeClass
  public static void setUp()
  {
    sut = new IntegersOnlyValidator();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithInteger()
  {
    boolean isValid = sut.isValid("42");
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
  public void testIsNotValidWithDecimalNumber()
  {
    boolean isValid = sut.isValid("4.2");
    Assertions.assertThat(isValid).isFalse();
  }

  /**
   * Tests the validation.
   */
  @Test
  public void testIsNotValidWithString()
  {
    boolean isValid = sut.isValid("asdf");
    Assertions.assertThat(isValid).isFalse();
  }
}
