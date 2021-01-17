package com.lasy.dwbk.ws;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.lasy.dwbk.ws.wms.TileMatrixParamsTest;

@RunWith(Suite.class)
@SuiteClasses({
  QueryParametersTest.class,
  TileMatrixParamsTest.class,
})
public class WsTestSuite
{

}
