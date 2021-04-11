package com.lasy.wsk.validation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.lasy.wsk.validation.impl.AcceptAllValidatorTest;
import com.lasy.wsk.validation.impl.CoordinateValidatorTest;
import com.lasy.wsk.validation.impl.IntegersOnlyValidatorTest;
import com.lasy.wsk.validation.impl.IntegersSeparatedBySemicolonValidatorTest;
import com.lasy.wsk.validation.impl.LayerServiceUriValidatorTest;
import com.lasy.wsk.validation.impl.RgbaValidatorTest;
import com.lasy.wsk.validation.impl.TextOnlyValidatorTest;

@RunWith(Suite.class)
@SuiteClasses({
  AcceptAllValidatorTest.class,
  CoordinateValidatorTest.class,
  IntegersOnlyValidatorTest.class,
  IntegersSeparatedBySemicolonValidatorTest.class, 
  LayerServiceUriValidatorTest.class,
  RgbaValidatorTest.class,
  TextOnlyValidatorTest.class,
})
public class ValidationTestSuite
{

}
