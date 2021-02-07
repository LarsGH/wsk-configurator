package com.lasy.dwbk.ws;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.lasy.dwbk.ws.wfs.WfsConfigTest;
import com.lasy.dwbk.ws.wms.TileMatrixParamsTest;
import com.lasy.dwbk.ws.wms.WmsConfigTest;

@RunWith(Suite.class)
@SuiteClasses({
  TileMatrixParamsTest.class,
  WfsConfigTest.class,
  WmsConfigTest.class,
})
public class WsTestSuite
{

}
