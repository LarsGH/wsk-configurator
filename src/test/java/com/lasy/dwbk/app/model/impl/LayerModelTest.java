package com.lasy.dwbk.app.model.impl;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.lasy.dwbk.db.util.DwbkTestFactory;

/**
 * Tests {@link LayerModel}.
 * @author larss
 *
 */
public class LayerModelTest
{
  
  /**
   * Tests, the last download date.
   */
  @Test
  public void testLastDownloadDate()
  {
    LayerModel sut = DwbkTestFactory.createLayer();
    Assertions.assertThat(sut.getLastDownloadDate()).isEmpty();
    
    sut.setLastDownloadDate(LocalDateTime.now());
    Assertions.assertThat(sut.getLastDownloadDate().get()).isEqualToIgnoringSeconds(LocalDateTime.now());
    
    // download date is reset if layer cannot be store locally
    sut.setStoreLocal(false);
    Assertions.assertThat(sut.getLastDownloadDate()).isEmpty();
  }

}
