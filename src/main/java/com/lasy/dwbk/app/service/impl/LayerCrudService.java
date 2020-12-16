package com.lasy.dwbk.app.service.impl;

import java.util.function.Function;

import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeature;

import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.app.model.impl.LayerModelBuilder;
import com.lasy.dwbk.app.service.ADwbkCrudService;
import com.lasy.dwbk.db.tables.LayerTable;

/**
 * CRUD service for layers.
 * @author larss
 */
public class LayerCrudService extends ADwbkCrudService<LayerModel, LayerModelBuilder>
{

  public LayerCrudService(DataStore store)
  {
    super(store, LayerTable.TABLE_NAME); 
  }

  @Override
  public Function<SimpleFeature, LayerModel> featureToModel()
  {
    return LayerModel::new;
  }

}