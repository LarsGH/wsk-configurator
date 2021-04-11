package com.lasy.wsk.ws;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.lasy.wsk.ws.wfs.WfsConfigTest;
import com.lasy.wsk.ws.wfs.style.WfsStyleConfigTest;
import com.lasy.wsk.ws.wms.TileMatrixParamsTest;
import com.lasy.wsk.ws.wms.WmsConfigTest;

@RunWith(Suite.class)
@SuiteClasses({
  TileMatrixParamsTest.class,
  WfsConfigTest.class,
  WfsStyleConfigTest.class,
  WmsConfigTest.class,
})
public class WsTestSuite
{

}
