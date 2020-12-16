package com.lasy.dwbk;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.app.service.impl.ServiceTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
  ServiceTestSuite.class,
})
public class AllTests
{
  private static Logger LOG = Logger.getLogger(AllTests.class.getSimpleName());
  
  @ClassRule
  public static TemporaryFolder folder = new TemporaryFolder();
  
  @BeforeClass
  public static void setUp()
  {
    try {
      LOG.log(Level.INFO, "Setup testsuite... initializing framework");
      File projectRoot = folder.getRoot();
      DwbkFramework.initialize(projectRoot.getPath());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @AfterClass
  public static void tearDown()
  {
    System.out.println("Finished test suite!");
  }
}
