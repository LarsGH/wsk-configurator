package com.lasy.dwbk.app.service.impl;

import java.math.BigDecimal;
import java.util.Collection;

import org.assertj.core.api.Assertions;
import org.geotools.data.DataStore;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.app.model.impl.BboxModel;
import com.lasy.dwbk.app.service.impl.BboxCrudService;
import com.lasy.dwbk.db.util.DwbkBigDecimal;

/**
 * Testet {@link BboxCrudService}.
 * @author larss
 *
 */
public class BboxCrudServiceTest
{
  private static final String EXPECTED_BBOX_DESCRIPTION = "Bbox description...";
  private static final BigDecimal EXPECTED_BBOX_MIN_LON = DwbkBigDecimal.create("9.9");
  private static final BigDecimal EXPECTED_BBOX_MIN_LAT = DwbkBigDecimal.create("1.1");
  private static final BigDecimal EXPECTED_BBOX_MAX_LON = DwbkBigDecimal.create("99.9");
  private static final BigDecimal EXPECTED_BBOX_MAX_LAT = DwbkBigDecimal.create("11.1");

  private static BboxCrudService sut;
  
  @BeforeClass
  public static void setUp()
  {
    // Test can just be started with AllTests!
    Assume.assumeTrue("This test needs a running test-framework!", DwbkFramework.isRunning());
    DataStore store = DwbkFramework.getInstance().getGeoPackage().getDataStore();
    sut = new BboxCrudService(store);
  }
  
  @Test
  public void testCrud()
  {
    // create
    BboxModel boxA = assertThatBboxIsCreatedWithExpectedContent("Test-Bbox-A");
    
    boxA.setName("changedName");
    boxA.setDescription("changedDescription");
    
    // save
    sut.update(boxA);
    
    // load (by ID)
    BboxModel reloadedBoxA = sut.readById(boxA.getId())
      .orElseThrow(() -> new AssertionError("Load by ID did not work!"));
    
    Assertions.assertThat(boxA).isEqualTo(reloadedBoxA);
    
    BboxModel boxB = assertThatBboxIsCreatedWithExpectedContent("Test-Bbox-B");
    
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
      .withMinLon(EXPECTED_BBOX_MIN_LON.toPlainString())
      .withMinLat(EXPECTED_BBOX_MIN_LAT.toPlainString()) 
      .withMaxLon(EXPECTED_BBOX_MAX_LON.toPlainString())
      .withMaxLat(EXPECTED_BBOX_MAX_LAT.toPlainString()));
    
    Assertions.assertThat(newBbox.getId()).isNotNull();
    Assertions.assertThat(newBbox.getName()).isEqualTo(name);
    Assertions.assertThat(newBbox.getDescription().get()).isEqualTo(EXPECTED_BBOX_DESCRIPTION);
    Assertions.assertThat(newBbox.getMinLon()).isCloseTo(EXPECTED_BBOX_MIN_LON, DwbkBigDecimal.TEST_OFFSET);
    Assertions.assertThat(newBbox.getMinLat()).isCloseTo(EXPECTED_BBOX_MIN_LAT, DwbkBigDecimal.TEST_OFFSET);
    Assertions.assertThat(newBbox.getMaxLon()).isCloseTo(EXPECTED_BBOX_MAX_LON, DwbkBigDecimal.TEST_OFFSET);
    Assertions.assertThat(newBbox.getMaxLat()).isCloseTo(EXPECTED_BBOX_MAX_LAT, DwbkBigDecimal.TEST_OFFSET);
    
    return newBbox;
  }
  
  
}
