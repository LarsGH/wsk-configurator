package com.lasy.dwbk.app.service.impl;

import java.math.BigDecimal;
import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.geotools.data.DataStore;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.app.model.impl.BboxModel;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.app.service.impl.LayerCrudService;
import com.lasy.dwbk.db.util.DwbkBigDecimal;

/**
 * Testet {@link LayerCrudService}.
 * @author larss
 *
 */
public class LayerCrudServiceTest
{
  private static final String EXPECTED_BBOX_NAME = "Bbox-Name";
  private static final String EXPECTED_BBOX_DESCRIPTION = "Bbox description...";
  private static final BigDecimal EXPECTED_BBOX_MIN_LON = DwbkBigDecimal.create("9.9");
  private static final BigDecimal EXPECTED_BBOX_MIN_LAT = DwbkBigDecimal.create("1.1");
  private static final BigDecimal EXPECTED_BBOX_MAX_LON = DwbkBigDecimal.create("99.9");
  private static final BigDecimal EXPECTED_BBOX_MAX_LAT = DwbkBigDecimal.create("11.1");
  
  private static final String EXPECTED_LAYER_URI = "https://my-test-uri.com";
  private static final boolean EXPECTED_STORE_LOCAL = true;
  private static final String EXPECTED_USER = "test-user";
  private static final String EXPECTED_PW = "test-pw-1234";

  private static LayerCrudService sut;
  
  private static BboxModel bbox;
  
  @BeforeClass
  public static void setUp()
  {
    // Test can just be started with AllTests!
    Assume.assumeTrue("This test needs a running test-framework!", DwbkFramework.isRunning());
    
    bbox = DwbkServiceProvider.getInstance().getBboxService()
      .create(BboxModel.builder(EXPECTED_BBOX_NAME)
        .withDescription(EXPECTED_BBOX_DESCRIPTION)
        .withMinLon(EXPECTED_BBOX_MIN_LON.toPlainString())
        .withMinLat(EXPECTED_BBOX_MIN_LAT.toPlainString())
        .withMaxLon(EXPECTED_BBOX_MAX_LON.toPlainString())
        .withMaxLat(EXPECTED_BBOX_MAX_LAT.toPlainString()));
    
    DataStore store = DwbkFramework.getInstance().getGeoPackage().getDataStore();
    sut = new LayerCrudService(store);
  }
  
  @Test
  public void testCrud()
  {
    // TODO: HIER WEITER DEBUGGEN!
    // create
    LayerModel layerA = assertThatLayerIsCreatedWithExpectedContent("Test-Layer-A");
    Assertions.assertThat(layerA.getBbox().get()).isEqualTo(bbox);
    
    layerA.setName("changedName");
    layerA.setPw("changedPw");
    layerA.setStoreLocal(false);
    layerA.setBboxId(null);
    
    // save
    sut.update(layerA);
    
    // load (by ID)
    LayerModel reloadedLayerA = sut.readById(layerA.getId())
      .orElseThrow(() -> new AssertionError("Load by ID did not work!"));
    
    Assertions.assertThat(layerA).isEqualTo(reloadedLayerA);
    
    LayerModel layerB = assertThatLayerIsCreatedWithExpectedContent("Test-Layer-B");
    
    // load all
    Collection<LayerModel> all = sut.readAll();
    Assertions.assertThat(all).containsExactlyInAnyOrder(layerA, layerB);
    
    // delete
    int deleteCount = sut.deleteById(layerA.getId());
    Assertions.assertThat(deleteCount).isEqualTo(1);
    
    deleteCount = sut.deleteById(layerB.getId());
    Assertions.assertThat(deleteCount).isEqualTo(1);
    
    all = sut.readAll();
    Assertions.assertThat(all).isEmpty();
  }

  private LayerModel assertThatLayerIsCreatedWithExpectedContent(String name)
  {
    LayerModel newLayer = sut.create(LayerModel.builder(name)
      .withUri(EXPECTED_LAYER_URI)
      .withStoreLocal(EXPECTED_STORE_LOCAL)
      .withBboxId(bbox.getId())
      .withUser(EXPECTED_USER)
      .withPassword(EXPECTED_PW));
          
    Assertions.assertThat(newLayer.getId()).isNotNull();
    Assertions.assertThat(newLayer.getName()).isEqualTo(name);
    Assertions.assertThat(newLayer.isStoreLocal()).isTrue();
    Assertions.assertThat(newLayer.getUser().get()).isEqualTo(EXPECTED_USER);
    Assertions.assertThat(newLayer.getPw().get()).isEqualTo(EXPECTED_PW);
    
    return newLayer;
  }
  
  
}
