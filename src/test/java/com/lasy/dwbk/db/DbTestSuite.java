package com.lasy.dwbk.db;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.lasy.dwbk.db.util.DbBooleanTest;
import com.lasy.dwbk.db.util.DbPasswordModifierTest;

@RunWith(Suite.class)
@SuiteClasses({
  DbBooleanTest.class,
  DbPasswordModifierTest.class,
})
public class DbTestSuite
{

}
