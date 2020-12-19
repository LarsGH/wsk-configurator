package com.lasy.dwbk.validation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.lasy.dwbk.validation.impl.AcceptAllValidatorTest;
import com.lasy.dwbk.validation.impl.CoordinateValidatorTest;

@RunWith(Suite.class)
@SuiteClasses({
  AcceptAllValidatorTest.class,
  CoordinateValidatorTest.class,
})
public class ValidationTestSuite
{

}
