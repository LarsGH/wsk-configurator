package com.lasy.wsk.validation.impl;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lasy.wsk.validation.impl.RgbaValidator;

/**
 * Tests {@link RgbaValidator}.
 * @author larss
 *
 */
public class RgbaValidatorTest
{
  private static RgbaValidator sut;

  @BeforeClass
  public static void setUp()
  {
    sut = new RgbaValidator();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testThatEmptyIsValid()
  {
    boolean isValid = sut.isValid("");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testThatNullIsValid()
  {
    boolean isValid = sut.isValid(null);
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testThatRgbaMinIsValid()
  {
    boolean isValid = sut.isValid("0;0;0;0");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testThatRgbaMaxIsValid()
  {
    boolean isValid = sut.isValid("255;255;255;100");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testThatRgbaWithSpacesIsValid()
  {
    boolean isValid = sut.isValid("0; 0; 0; 0");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testThatRgbaWithCommasIsInvalid()
  {
    boolean isValid = sut.isValid("0,0,0,0");
    Assertions.assertThat(isValid).isFalse();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testThatLessThanMinRgbIsInvalid()
  {
    boolean isValid = sut.isValid("-1;0;0;0");
    Assertions.assertThat(isValid).isFalse();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testThatMoreThanMaxRgbIsInvalid()
  {
    boolean isValid = sut.isValid("256;0;0;0");
    Assertions.assertThat(isValid).isFalse();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testThatLessThanMinAlphaIsInvalid()
  {
    boolean isValid = sut.isValid("0;0;0;-1");
    Assertions.assertThat(isValid).isFalse();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testThatMoreThanMaxAlphaIsInvalid()
  {
    boolean isValid = sut.isValid("0;0;0;101");
    Assertions.assertThat(isValid).isFalse();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testThatRgbIsInvalid()
  {
    boolean isValid = sut.isValid("0;0;0");
    Assertions.assertThat(isValid).isFalse();
  }
  

}
