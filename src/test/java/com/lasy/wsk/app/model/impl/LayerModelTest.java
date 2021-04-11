package com.lasy.wsk.app.model.impl;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import com.lasy.wsk.app.model.impl.LayerModel;
import com.lasy.wsk.db.util.WskEmptyModelsRule;
import com.lasy.wsk.db.util.WskTestFactory;
import com.lasy.wsk.ws.EWebServiceType;

/**
 * Tests {@link LayerModel}.
 * @author larss
 *
 */
public class LayerModelTest
{
  
  @Rule
  public WskEmptyModelsRule emptyModelsRule = new WskEmptyModelsRule();
  
  /**
   * Tests, the last download date.
   */
  @Test
  public void testLastDownloadDate()
  {
    LayerModel sut = WskTestFactory.createWmsLayer();
    Assertions.assertThat(sut.getLastDownloadDate()).isEmpty();
    
    sut.setLastDownloadDate(LocalDateTime.now());
    Assertions.assertThat(sut.getLastDownloadDate().get()).isEqualToIgnoringSeconds(LocalDateTime.now());
    
    // download date is reset if layer cannot be store locally
    sut.setStoreLocal(false);
    Assertions.assertThat(sut.getLastDownloadDate()).isEmpty();
  }
  
  /**
   * Tests that the service version and the service type changes by changing the request URI.
   */
  @Test
  public void testSetRequestUri()
  {
    LayerModel sut = WskTestFactory.createWfsLayer();
    
    sut.setRequest("http://testUrl?service=WMS&version=1.4.2&request=GetCapabilities");
    
    Assertions.assertThat(sut.getWebServiceType()).isEqualTo(EWebServiceType.WMS);
    Assertions.assertThat(sut.getWebServiceVersion()).isEqualTo("1.4.2");
    
    sut.setRequest("http://testUrl?service=WFS&version=9.4.2&request=GetCapabilities");
    
    Assertions.assertThat(sut.getWebServiceType()).isEqualTo(EWebServiceType.WFS);
    Assertions.assertThat(sut.getWebServiceVersion()).isEqualTo("9.4.2");
  }

}
