package com.lasy.wsk.app.service.impl;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeature;

import com.lasy.wsk.app.model.impl.BboxModel;
import com.lasy.wsk.app.model.impl.BboxModelBuilder;
import com.lasy.wsk.app.service.AWskCrudService;
import com.lasy.wsk.db.tables.impl.BboxTable;

/**
 * CRUD service for boundingboxes.
 * @author larss
 */
public class BboxCrudService extends AWskCrudService<BboxModel, BboxModelBuilder>
{

  public BboxCrudService(DataStore store)
  {
    super(store, BboxTable.TABLE_NAME);
  }

  @Override
  public Function<SimpleFeature, BboxModel> featureToModel()
  {
    return BboxModel::new;
  }

  @Override
  protected void doAfterCreate(BboxModel model)
  {
    removeMapBoundaryFromOtherBboxIfNecessary(model);
  }

  @Override
  protected void doAfterUpdate(BboxModel model)
  {
    removeMapBoundaryFromOtherBboxIfNecessary(model);
  }

  private void removeMapBoundaryFromOtherBboxIfNecessary(BboxModel changedBbox)
  {
    if (changedBbox.isMapBoundary())
    {
      List<BboxModel> bboxesWithMapBoundaryStatus = readAll().stream()
        // must be another bbox
        .filter(bbox -> !bbox.getId().equals(changedBbox.getId()))
        // must have active map boundary status
        .filter(BboxModel::isMapBoundary).collect(Collectors.toList());

      for (BboxModel bbox : bboxesWithMapBoundaryStatus)
      {
        bbox.setMapBoundary(false);
        updateWithoutAfterHandling(bbox);
      }
    }
  }
}
