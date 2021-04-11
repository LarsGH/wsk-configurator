package com.lasy.wsk;

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

import com.lasy.wsk.app.WskFramework;
import com.lasy.wsk.app.model.ModelTestSuite;
import com.lasy.wsk.app.service.ServiceTestSuite;
import com.lasy.wsk.db.DbTestSuite;
import com.lasy.wsk.util.WskUtilTestSuite;
import com.lasy.wsk.validation.ValidationTestSuite;
import com.lasy.wsk.ws.WsTestSuite;

@RunWith(Suite.class)
@SuiteClasses({
  ModelTestSuite.class,
  ServiceTestSuite.class,
  DbTestSuite.class,
  WskUtilTestSuite.class,
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
      WskFramework.initializeFramework(projectRoot.getPath());
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
