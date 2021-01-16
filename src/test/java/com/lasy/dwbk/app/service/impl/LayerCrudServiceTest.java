package com.lasy.dwbk.app.service.impl;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.geotools.data.DataStore;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.model.impl.BboxModel;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.db.util.DbGeneratedLayerName;

/**
 * Testet {@link LayerCrudService}.
 * @author larss
 *
 */
public class LayerCrudServiceTest
{
  private static final String EXPECTED_BBOX_NAME = "Bbox-Name";
  private static final String EXPECTED_BBOX_DESCRIPTION = "Bbox description...";
  private static final String EXPECTED_BBOX_MIN_LON = "9.99999";
  private static final String EXPECTED_BBOX_MIN_LAT = "1.11111";
  private static final String EXPECTED_BBOX_MAX_LON = "99.11111";
  private static final String EXPECTED_BBOX_MAX_LAT = "11.99999";
  
  private static final String EXPECTED_LAYER_DESCRIPTION = "My layer description...";
  private static final String EXPECTED_LAYER_URI = "https://my-test-uri.com?service=wms&version=1.0.0&layer=test";
  private static final boolean EXPECTED_STORE_LOCAL = true;
  private static final boolean EXPECTED_IS_SAVED = true;
  private static final boolean EXPECTED_IS_VISIBLE = true;
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
        .withMinLon(EXPECTED_BBOX_MIN_LON)
        .withMinLat(EXPECTED_BBOX_MIN_LAT)
        .withMaxLon(EXPECTED_BBOX_MAX_LON)
        .withMaxLat(EXPECTED_BBOX_MAX_LAT));
    
    DataStore store = DwbkFramework.getInstance().getGeoPackage().getDataStore();
    sut = new LayerCrudService(store);
  }
  
  @Test
  public void testCrud()
  {
    // create
    LayerModel layerA = assertThatLayerIsCreatedWithExpectedContent("Test-Layer-A");
    Assertions.assertThat(layerA.getBbox()).isEqualTo(bbox);
    
    layerA.setName("changedName");
    layerA.setPw("changedPw");
    layerA.setStoreLocal(false);
    layerA.setMetersPerPixelText(null);
    layerA.setSaved(false);
    layerA.setVisible(false);    
    layerA.updateLastDownloadDate();
    
    // save
    sut.update(layerA);
    Assertions.assertThat(layerA.getLastDownloadDate().get()).isEqualToIgnoringSeconds(LocalDateTime.now());
    
    // load (by ID)
    LayerModel reloadedLayerA = sut.readById(layerA.getId())
      .orElseThrow(() -> new AssertionError("Load by ID did not work!"));
    
    Assertions.assertThat(layerA).isEqualTo(reloadedLayerA);
    Assertions.assertThat(reloadedLayerA.getMetersPerPixelPerZoomLevel()).isEmpty();
    
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
    String expectedUri = EXPECTED_LAYER_URI + "_" + name;
    LayerModel newLayer = sut.create(LayerModel.builder(name)
      .withDescription(EXPECTED_LAYER_DESCRIPTION)
      .withUri(expectedUri)
      .withStoreLocal(EXPECTED_STORE_LOCAL)
      .withMetersPerPixel("1;10;100")
      .withSavedStatus(EXPECTED_IS_SAVED)
      .withDefaultVisible(EXPECTED_IS_VISIBLE)
      .withBboxId(bbox.getId())
      .withUser(EXPECTED_USER)
      .withPassword(EXPECTED_PW));
          
    Assertions.assertThat(newLayer.getId()).isNotNull();
    Assertions.assertThat(newLayer.getName()).isEqualTo(name);
    Assertions.assertThat(newLayer.getDescription().get()).isEqualTo(EXPECTED_LAYER_DESCRIPTION);
    Assertions.assertThat(newLayer.getUri()).isEqualTo(expectedUri);
    Assertions.assertThat(newLayer.isStoreLocal()).isTrue();
    
    Map<Integer, Integer> expectedMetersPerPixelPerZoomLevel = new LinkedHashMap<Integer, Integer>();
    expectedMetersPerPixelPerZoomLevel.put(0, 100);
    expectedMetersPerPixelPerZoomLevel.put(1, 10);
    expectedMetersPerPixelPerZoomLevel.put(2, 1);
    Assertions.assertThat(newLayer.getMetersPerPixelPerZoomLevel()).containsExactlyEntriesOf(expectedMetersPerPixelPerZoomLevel);
    
    Assertions.assertThat(newLayer.isSaved()).isTrue();
    Assertions.assertThat(newLayer.isVisible()).isTrue();
    Assertions.assertThat(newLayer.getUser().get()).isEqualTo(EXPECTED_USER);
    Assertions.assertThat(newLayer.getPw().get()).isEqualTo(EXPECTED_PW);
    Assertions.assertThat(newLayer.getBboxId()).isEqualTo(bbox.getId());
    Assertions.assertThat(newLayer.getLastChangedDate()).isEqualToIgnoringSeconds(LocalDateTime.now());
    String expectedLocalName = DbGeneratedLayerName.idToGeneratedTableName(newLayer.getId());
    Assertions.assertThat(newLayer.getLocalName()).isEqualTo(expectedLocalName);
    
    String expectedQueryParts = String.format("{\"base_url\":\"https://my-test-uri.com?\",\r\n"
      + "\"layer\":\"test_%s\",\r\n"
      + "\"service\":\"wms\",\r\n"
      + "\"version\":\"1.0.0\"}", newLayer.getName());
    Assertions.assertThat(newLayer.getQueryParts()).isEqualTo(expectedQueryParts);
    
    return newLayer;
  }
  
  
}
