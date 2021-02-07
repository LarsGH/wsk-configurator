package com.lasy.dwbk.app.model.impl;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.Rule;
import org.junit.Test;

import com.lasy.dwbk.db.util.DwbkEmptyModelsRule;
import com.lasy.dwbk.db.util.DwbkTestFactory;
import com.lasy.dwbk.ws.EWebServiceType;

/**
 * Tests {@link LayerModel}.
 * @author larss
 *
 */
public class LayerModelTest
{
  
  @Rule
  public DwbkEmptyModelsRule emptyModelsRule = new DwbkEmptyModelsRule();
  
  /**
   * Tests, the last download date.
   */
  @Test
  public void testLastDownloadDate()
  {
    LayerModel sut = DwbkTestFactory.createWmsLayer();
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
    LayerModel sut = DwbkTestFactory.createWfsLayer();
    
    sut.setRequest("http://testUrl?service=WMS&version=1.4.2&request=GetCapabilities");
    
    Assertions.assertThat(sut.getWebServiceType()).isEqualTo(EWebServiceType.WMS);
    Assertions.assertThat(sut.getWebServiceVersion()).isEqualTo("1.4.2");
    
    sut.setRequest("http://testUrl?service=WFS&version=9.4.2&request=GetCapabilities");
    
    Assertions.assertThat(sut.getWebServiceType()).isEqualTo(EWebServiceType.WFS);
    Assertions.assertThat(sut.getWebServiceVersion()).isEqualTo("9.4.2");
  }

}
