package com.lasy.wsk.validation.impl;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lasy.wsk.validation.impl.IntegersSeparatedBySemicolonValidator;

/**
 * Tests {@link IntegersSeparatedBySemicolonValidator}.
 * @author larss
 *
 */
public class IntegersSeparatedBySemicolonValidatorTest
{

  private static IntegersSeparatedBySemicolonValidator sut;

  @BeforeClass
  public static void setUp()
  {
    sut = new IntegersSeparatedBySemicolonValidator();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithSingleInteger()
  {
    boolean isValid = sut.isValid("42");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithEndingSemicolon()
  {
    boolean isValid = sut.isValid("42;");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithMultipleIntegersSeparatedBySemicolon()
  {
    boolean isValid = sut.isValid("42;43;45");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithoutInput()
  {
    boolean isValid = sut.isValid("");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsNotValidWithSeparationByComma()
  {
    boolean isValid = sut.isValid("42,43");
    Assertions.assertThat(isValid).isFalse();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsNotValidWithText()
  {
    boolean isValid = sut.isValid("Test");
    Assertions.assertThat(isValid).isFalse();
  }
  
  
}
