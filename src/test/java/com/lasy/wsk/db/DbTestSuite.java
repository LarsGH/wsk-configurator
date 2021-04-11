package com.lasy.wsk.db;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.lasy.wsk.db.util.DbBooleanTest;
import com.lasy.wsk.db.util.DbGeneratedLayerNameTest;
import com.lasy.wsk.db.util.DbPasswordModifierTest;

@RunWith(Suite.class)
@SuiteClasses({
  DbBooleanTest.class,
  DbGeneratedLayerNameTest.class,
  DbPasswordModifierTest.class,
})
public class DbTestSuite
{

}
