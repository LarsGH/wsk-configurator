package com.lasy.dwbk.ws.wms;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.model.impl.BboxModel;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.db.util.DwbkEmptyModelsRule;
import com.lasy.dwbk.db.util.DwbkTestFactory;

/**
 * Tests {@link TileMatrixParams}.
 * @author larss
 *
 */
public class TileMatrixParamsTest
{
  
  private static BboxModel bbox;
  private static LayerModel layer;
  
  @Rule
  public DwbkEmptyModelsRule emptyModelsRule = new DwbkEmptyModelsRule();
  
  /**
   * Setup.
   */
  @Before
  public void setUp()
  {
    layer = DwbkTestFactory.createWmsLayer();
    bbox = layer.getBbox(); 
  }
  
  /**
   * Test, that the tile matrix parameters are created for each configured zoom level.
   */
  @Test
  public void testCreateForLayerCreatesParamsForEachZoomLevel()
  {
    setMetersPerPixelText(layer, "1;2;5;10");
    
    List<TileMatrixParams> tileMatrixParams = TileMatrixParams.createForLayer(layer);
    
    Assertions.assertThat(tileMatrixParams).hasSize(4);
    
    // Params are sorted by zoomlevel
    for(int i = 0; i < tileMatrixParams.size(); i++)
    {
      TileMatrixParams params = tileMatrixParams.get(i);
      Assertions.assertThat(params.getZoomLevel()).isEqualTo(i);
    }
  }
  
  /**
   * Tests, that the envelope is enlarged if the parameters do not initially fit.
   */
  @Test
  public void testCreateForLayer()
  {
    useMetersPerPixelAndBboxSize(2, 3_501, 3_777);
    
    List<TileMatrixParams> tileMatrixParams = TileMatrixParams.createForLayer(layer);
    
    TileMatrixParams params = tileMatrixParams.get(0);
    Assertions.assertThat(params.getMatrixHeight()).isEqualTo(2);
    Assertions.assertThat(params.getMatrixWidth()).isEqualTo(2);
    Assertions.assertThat(params.getMetersPerPixel()).isEqualTo(2);
    Assertions.assertThat(params.getTileHeightInPixels()).isEqualTo(1000);
    Assertions.assertThat(params.getTileWidthInPixels()).isEqualTo(1000);
    Assertions.assertThat(params.getZoomLevel()).isEqualTo(0);
  }
  
  private void useMetersPerPixelAndBboxSize(int metersPerPixel, int bBoxHeight, int bBoxWidth)
  {
    setMetersPerPixelText(layer, String.valueOf(metersPerPixel));
    bbox.setMinLon("0");
    bbox.setMinLat("0");
    bbox.setMaxLon(String.valueOf(bBoxWidth));
    bbox.setMaxLat(String.valueOf(bBoxHeight));
    DwbkServiceProvider.getInstance().getBboxService().update(bbox);
  }
  
  private void setMetersPerPixelText(LayerModel layer, String metersPerPixel)
  {
    WmsConfig wmsConfig = layer.getWmsConfig();
    wmsConfig.setMetersPerPixel(metersPerPixel);
    layer.setWmsConfig(wmsConfig);
    
    DwbkServiceProvider.getInstance().getLayerService().update(layer);
  }
  
}
