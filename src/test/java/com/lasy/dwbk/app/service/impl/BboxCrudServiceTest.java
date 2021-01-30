package com.lasy.dwbk.app.service.impl;

import java.time.LocalDateTime;
import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.geotools.data.DataStore;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.app.model.impl.BboxModel;
import com.lasy.dwbk.db.util.DwbkEmptyModelsRule;

/**
 * Testet {@link BboxCrudService}.
 * @author larss
 *
 */
public class BboxCrudServiceTest
{
  private static final String EXPECTED_BBOX_DESCRIPTION = "Bbox description...";
  private static final String EXPECTED_BBOX_MIN_LON = "9.999993";
  private static final String EXPECTED_BBOX_MIN_LAT = "1.111116";
  private static final String EXPECTED_BBOX_MAX_LON = "99.111117";
  private static final String EXPECTED_BBOX_MAX_LAT = "11.999992";
  private static final boolean EXPECTED_BBOX_IS_MAP_BOUNDARY = true;
  private static final int EXPECTED_BBOX_EPSG = 4326;
  
  @Rule
  public DwbkEmptyModelsRule emptyModelsRule = new DwbkEmptyModelsRule();

  private static BboxCrudService sut;
  
  @BeforeClass
  public static void setUp()
  {
    // Test can just be started with AllTests!
    Assume.assumeTrue("This test needs a running test-framework!", DwbkFramework.isRunning());
    DataStore store = DwbkFramework.getInstance().getDwbkGeoPackage().getDataStore();
    sut = new BboxCrudService(store);
  }
  
  @Test
  public void testCrud()
  {
    // create
    BboxModel boxA = assertThatBboxIsCreatedWithExpectedContent("Test-Bbox-A");
    
    boxA.setName("changedName");
    boxA.setDescription("changedDescription");
    boxA.setEpsg(25832);
    
    // save
    sut.update(boxA);
    
    // load (by ID)
    BboxModel reloadedBoxA = sut.readById(boxA.getId())
      .orElseThrow(() -> new AssertionError("Load by ID did not work!"));
    
    Assertions.assertThat(boxA).isEqualTo(reloadedBoxA);
    
    BboxModel boxB = assertThatBboxIsCreatedWithExpectedContent("Test-Bbox-B");
    
    // boxA lost the map boundary status!
    boxA = sut.readById(boxA.getId())
      .orElseThrow(() -> new AssertionError("Load by ID did not work!"));
    Assertions.assertThat(boxA.isMapBoundary()).isFalse();
    
    // load all
    Collection<BboxModel> all = sut.readAll();
    Assertions.assertThat(all).containsExactlyInAnyOrder(boxA, boxB);
    
    // delete
    int deleteCount = sut.deleteById(boxA.getId());
    Assertions.assertThat(deleteCount).isEqualTo(1);
    
    deleteCount = sut.deleteById(boxB.getId());
    Assertions.assertThat(deleteCount).isEqualTo(1);
    
    all = sut.readAll();
    Assertions.assertThat(all).isEmpty();
  }

  private BboxModel assertThatBboxIsCreatedWithExpectedContent(String name)
  {
    BboxModel newBbox = sut.create(BboxModel.builder(name)
      .withDescription(EXPECTED_BBOX_DESCRIPTION)
      .withEpsg(EXPECTED_BBOX_EPSG)
      .withMinLon(EXPECTED_BBOX_MIN_LON)
      .withMinLat(EXPECTED_BBOX_MIN_LAT) 
      .withMaxLon(EXPECTED_BBOX_MAX_LON)
      .withMaxLat(EXPECTED_BBOX_MAX_LAT)
      .withMapBoundary(EXPECTED_BBOX_IS_MAP_BOUNDARY));
    
    Assertions.assertThat(newBbox.getId()).isNotNull();
    Assertions.assertThat(newBbox.getName()).isEqualTo(name);
    Assertions.assertThat(newBbox.getDescription().get()).isEqualTo(EXPECTED_BBOX_DESCRIPTION);
    Assertions.assertThat(newBbox.getEpsg()).isEqualTo(EXPECTED_BBOX_EPSG);
    Assertions.assertThat(newBbox.getMinLon()).isEqualTo(EXPECTED_BBOX_MIN_LON);
    Assertions.assertThat(newBbox.getMinLat()).isEqualTo(EXPECTED_BBOX_MIN_LAT);
    Assertions.assertThat(newBbox.getMaxLon()).isEqualTo(EXPECTED_BBOX_MAX_LON);
    Assertions.assertThat(newBbox.getMaxLat()).isEqualTo(EXPECTED_BBOX_MAX_LAT);
    Assertions.assertThat(newBbox.isMapBoundary()).isTrue();
    Assertions.assertThat(newBbox.getLastChangedDate()).isEqualToIgnoringSeconds(LocalDateTime.now());
    
    return newBbox;
  }
  
  
}
