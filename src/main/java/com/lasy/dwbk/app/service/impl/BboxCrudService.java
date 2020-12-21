package com.lasy.dwbk.app.service.impl;

import java.util.function.Function;

import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeature;

import com.lasy.dwbk.app.model.impl.BboxModel;
import com.lasy.dwbk.app.model.impl.BboxModelBuilder;
import com.lasy.dwbk.app.service.ADwbkCrudService;
import com.lasy.dwbk.db.tables.impl.BboxTable;

/**
 * CRUD service for boundingboxes.
 * @author larss
 */
public class BboxCrudService extends ADwbkCrudService<BboxModel, BboxModelBuilder>
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
}
