package com.lasy.wsk.app.service.impl;

import java.util.function.Function;

import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeature;

import com.lasy.wsk.app.model.impl.LayerModel;
import com.lasy.wsk.app.model.impl.LayerModelBuilder;
import com.lasy.wsk.app.service.AWskCrudService;
import com.lasy.wsk.db.tables.impl.LayerTable;
import com.lasy.wsk.db.util.DbGeneratedLayerName;

/**
 * CRUD service for layers.
 * @author larss
 */
public class LayerCrudService extends AWskCrudService<LayerModel, LayerModelBuilder>
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

  @Override
  protected void doAfterCreate(LayerModel model)
  {
    // Set the local table name used to find the layer
    String localTableName = DbGeneratedLayerName.idToGeneratedTableName(model.getId());
    model.setLocalName(localTableName);
    
    this.update(model);
  }

  @Override
  protected void doAfterUpdate(LayerModel model)
  {
    // Nothing to do!
  }

}