package com.lasy.wsk.validation.impl;

import org.assertj.core.api.Assertions;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lasy.wsk.validation.impl.LayerServiceUriValidator;

/**
 * Tests {@link LayerServiceUriValidator}.
 * @author larss
 *
 */
public class LayerServiceUriValidatorTest
{
  private static LayerServiceUriValidator sut;

  @BeforeClass
  public static void setUp()
  {
    sut = new LayerServiceUriValidator();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithServiceWms()
  {
    boolean isValid = sut.isValid("https://www.page?service=WmS&version=1.0.0&request=GetCapabilities");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsValidWithServiceWfs()
  {
    boolean isValid = sut.isValid("http://www.page?service=WfS&version=1.0.0&request=GetCapabilities");
    Assertions.assertThat(isValid).isTrue();
  }
  
  /**
   * Tests the validation.
   */
  @Test
  public void testIsNotValidWithServiceUnknown()
  {
    boolean isValid = sut.isValid("https://www.page?service=WCS&version=1.0.0&request=GetCapabilities");
    Assertions.assertThat(isValid).isFalse();
  }
}
