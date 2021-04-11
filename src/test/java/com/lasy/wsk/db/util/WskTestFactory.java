package com.lasy.wsk.db.util;

import com.lasy.wsk.app.WskServiceProvider;
import com.lasy.wsk.app.model.impl.BboxModel;
import com.lasy.wsk.app.model.impl.BboxModelBuilder;
import com.lasy.wsk.app.model.impl.LayerModel;
import com.lasy.wsk.app.model.impl.LayerModelBuilder;
import com.lasy.wsk.util.BboxUtil;
import com.lasy.wsk.ws.wfs.WfsConfig;
import com.lasy.wsk.ws.wms.WmsConfig;

/**
 * Factory for test models.
 * @author larss
 *
 */
public class WskTestFactory
{

  /**
   * Creates a WMS layer with default values. The layer is automatically saved.
   * A bbox will be created and saved as well.
   * @return layer with default values
   */
  public static LayerModel createWmsLayer()
  {
    return WskServiceProvider.getInstance().getLayerService()
      .create(createWmsLayerBuilderWithDefaultValues());
  }
  
  /**
   * Creates a WFS layer with default values. The layer is automatically saved.
   * A bbox will be created and saved as well.
   * @return layer with default values
   */
  public static LayerModel createWfsLayer()
  {
    return WskServiceProvider.getInstance().getLayerService()
      .create(createWfsLayerBuilderWithDefaultValues());
  }
  
  /**
   * Returns a layer builder with default values.
   * A bbox will be created and saved as well.
   * @return builder with default values
   */
  public static LayerModelBuilder createLayerBuilderWithDefaultValues()
  {
    BboxModel bbox = createBbox();
    
    return LayerModel.builder("Factory-Layer")
      .withDescription("Layer for unit tests")
      .withDefaultVisible(true)
      .withBboxId(bbox.getId())
      .withRequest("http://testUrl?service=WMS&version=1.0.0&request=GetCapabilities");
  }
  
  /**
   * Returns a WMS layer builder with default values.
   * A bbox will be created and saved as well.
   * @return builder with default values
   */
  public static LayerModelBuilder createWmsLayerBuilderWithDefaultValues()
  {
    return createLayerBuilderWithDefaultValues()
      .withRequest("http://testUrl?service=WMS&version=1.0.0&request=GetCapabilities")
      .withWmsConfig(createWmsConfig());
  }
  
  /**
   * Returns a WFS layer builder with default values.
   * A bbox will be created and saved as well.
   * @return builder with default values
   */
  public static LayerModelBuilder createWfsLayerBuilderWithDefaultValues()
  {
    return createLayerBuilderWithDefaultValues()
      .withRequest("http://testUrl?service=WFS&version=1.0.0&request=GetCapabilities")
      .withWfsConfig(createWfsConfig());
  }
  
  /**
   * Creates a bbox with default values. The bbox is automatically saved.
   * @return bbox with default values
   */
  public static BboxModel createBbox()
  {
    return WskServiceProvider.getInstance().getBboxService()
      .create(createBboxBuilderWithDefaultValues());
  }
  
  /**
   * Returns a bbox builder with default values.
   * @return builder with default values
   */
  public static BboxModelBuilder createBboxBuilderWithDefaultValues()
  {
    return BboxModel.builder("Factory-Bbox")
      .withDescription("Bbox for unit tests")
      .withEpsg(BboxUtil.EPSG_3857)
      .withMapBoundary(true)
      .withMinLon("0")
      .withMinLat("0")
      .withMaxLon("10000")
      .withMaxLat("10000");
  }
  
  public static WmsConfig createWmsConfig()
  {
    WmsConfig config = new WmsConfig();
    config.setMetersPerPixel("1;5;10");
    config.setLayer("testLayer");
    config.setStyles("testStyles");
    
    return config;
  }
  
  public static WfsConfig createWfsConfig()
  {
    WfsConfig config = new WfsConfig();
    config.setTypeNames("testLayer");
    
    return config;
  }
}
