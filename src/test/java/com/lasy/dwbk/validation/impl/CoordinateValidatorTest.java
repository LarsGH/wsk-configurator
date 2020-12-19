package com.lasy.dwbk.validation.impl;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests {@link CoordinateValidator}.
 * @author larss
 *
 */
public class CoordinateValidatorTest
{
  
  private static CoordinateValidator sut;

  @BeforeClass
  public static void setUp()
  {
    sut = new CoordinateValidator();
  }

  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithPositiveInteger()
  {
    boolean isValid = sut.isValid("42");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithNegativeInteger()
  {
    boolean isValid = sut.isValid("-42");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithPositiveDecimal()
  {
    boolean isValid = sut.isValid("42.1337");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithNegativeDecimal()
  {
    boolean isValid = sut.isValid("-42.1337");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsNotValidWithCharacters()
  {
    boolean isValid = sut.isValid("abc");
    Assertions.assertThat(isValid).isFalse();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsNotValidWithLeadingDecimalPoint()
  {
    boolean isValid = sut.isValid(".42");
    Assertions.assertThat(isValid).isFalse();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsNotValidWithTwoDecimalPoints()
  {
    boolean isValid = sut.isValid("1.2.3");
    Assertions.assertThat(isValid).isFalse();
  }
}
