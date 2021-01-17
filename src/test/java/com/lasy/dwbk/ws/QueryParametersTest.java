package com.lasy.dwbk.ws;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.lasy.dwbk.ws.wms.WmsQueryConst;

/**
 * Tests {@link QueryParameters}.
 * @author larss
 *
 */
public class QueryParametersTest
{
  private static final String GET_MAP_QUERY = "https://www.wms.nrw.de/geobasis/wms_nw_dvg"
    + "?service=WMS"
    + "&version=1.3.0"
    + "&reQuest=GetMap"
    + "&layers=nw_dvg_gem"
    + "&CRS=EPSG:4326"
    + "&bBox=51.67352,6.95187,51.67529,6.95547"
    + "&width=1000"
    + "&height=1000"
    + "&Format=image/png"
    + "&styles=default";
  
  /**
   * Tests the creation of parameters
   */
  @Test
  public void testParameters()
  {    
    QueryParameters sut = QueryParameters.fromLayerUri(GET_MAP_QUERY);
    
    Assertions.assertThat(sut.getParams()).containsExactlyInAnyOrderEntriesOf(Map.ofEntries(
      Map.entry(WmsQueryConst.GetMap.BASE_URL, "https://www.wms.nrw.de/geobasis/wms_nw_dvg?"),
      Map.entry(WmsQueryConst.GetMap.SERVICE, "WMS"), 
      Map.entry(WmsQueryConst.GetMap.VERSION, "1.3.0"), 
      Map.entry(WmsQueryConst.GetMap.REQUEST, "GetMap"),
      Map.entry(WmsQueryConst.GetMap.LAYERS, "nw_dvg_gem"), 
      Map.entry(WmsQueryConst.GetMap.CRS, "EPSG:4326"),
      Map.entry(WmsQueryConst.GetMap.BBOX, "51.67352,6.95187,51.67529,6.95547"), 
      Map.entry(WmsQueryConst.GetMap.WIDTH, "1000"),
      Map.entry(WmsQueryConst.GetMap.HEIGHT, "1000"), 
      Map.entry(WmsQueryConst.GetMap.FORMAT, "image/png"),
      Map.entry(WmsQueryConst.GetMap.STYLES, "default")));
  }
  
  /**
   * Tests the creation of the getCapabilities request
   */
  @Test
  public void testGetCapabilitiesRequest()
  {    
    QueryParameters sut = QueryParameters.fromLayerUri(GET_MAP_QUERY);
    
    Assertions.assertThat(sut.getCapablitiesRequest())
    .isEqualTo("https://www.wms.nrw.de/geobasis/wms_nw_dvg"
      + "?service=WMS"
      + "&version=1.3.0"
      + "&request=GetCapabilities");
  }
  
  /**
   * Tests the creation of the base URL.
   */
  @Test
  public void testGetBaseUrl()
  {    
    QueryParameters sut = QueryParameters.fromLayerUri(GET_MAP_QUERY);
    
    Assertions.assertThat(sut.getBaseRequest()).isEqualTo("https://www.wms.nrw.de/geobasis/wms_nw_dvg?");
  }
}
