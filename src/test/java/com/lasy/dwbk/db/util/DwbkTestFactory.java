package com.lasy.dwbk.db.util;

import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.model.impl.BboxModel;
import com.lasy.dwbk.app.model.impl.BboxModelBuilder;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.app.model.impl.LayerModelBuilder;
import com.lasy.dwbk.util.BboxUtil;

/**
 * Factory for test models.
 * @author larss
 *
 */
public class DwbkTestFactory
{

  /**
   * Creates a layer with default values. The layer is automatically saved.
   * A bbox will be created and saved as well.
   * @return layer with default values
   */
  public static LayerModel createLayer()
  {
    return DwbkServiceProvider.getInstance().getLayerService()
      .create(createLayerBuilderWithDefaultValues());
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
      .withMetersPerPixel("10")
      .withUri("http://testUrl?service=WMS&version=1.0.0&request=GetMap");
  }
  
  /**
   * Creates a bbox with default values. The bbox is automatically saved.
   * @return bbox with default values
   */
  public static BboxModel createBbox()
  {
    return DwbkServiceProvider.getInstance().getBboxService()
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
}
