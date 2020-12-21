package com.lasy.dwbk.validation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.lasy.dwbk.validation.impl.AcceptAllValidatorTest;
import com.lasy.dwbk.validation.impl.CoordinateValidatorTest;
import com.lasy.dwbk.validation.impl.IntegersOnlyValidatorTest;
import com.lasy.dwbk.validation.impl.TextOnlyValidatorTest;

@RunWith(Suite.class)
@SuiteClasses({
  AcceptAllValidatorTest.class,
  CoordinateValidatorTest.class,
  IntegersOnlyValidatorTest.class,
  TextOnlyValidatorTest.class,
})
public class ValidationTestSuite
{

}
