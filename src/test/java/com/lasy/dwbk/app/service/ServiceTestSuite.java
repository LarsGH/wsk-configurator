package com.lasy.dwbk.app.service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.lasy.dwbk.app.service.impl.BboxCrudServiceTest;
import com.lasy.dwbk.app.service.impl.LayerCrudServiceTest;

@RunWith(Suite.class)
@SuiteClasses({
  BboxCrudServiceTest.class,
  LayerCrudServiceTest.class
})
public class ServiceTestSuite
{

}
