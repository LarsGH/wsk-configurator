package com.lasy.dwbk.validation.impl;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link AcceptAllValidator}.
 * @author larss
 *
 */
public class AcceptAllValidatorTest
{
  private static AcceptAllValidator sut;

  @BeforeClass
  public static void setUp()
  {
    sut = new AcceptAllValidator();
  }

  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithNumber()
  {
    boolean isValid = sut.isValid("42");
    Assertions.assertThat(isValid).isTrue();
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
}
