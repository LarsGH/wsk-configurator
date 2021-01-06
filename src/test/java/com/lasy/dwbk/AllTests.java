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
import com.lasy.dwbk.app.service.ServiceTestSuite;
import com.lasy.dwbk.db.DbTestSuite;
import com.lasy.dwbk.validation.ValidationTestSuite;
import com.lasy.dwbk.ws.WsTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
  ServiceTestSuite.class,
  DbTestSuite.class,
  ValidationTestSuite.class,
  WsTestSuite.class,
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
      LOG.log(Level.INFO, "Starting testsuite... initializing framework...");
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
    LOG.log(Level.INFO, "Finished test suite!");
  }
}
