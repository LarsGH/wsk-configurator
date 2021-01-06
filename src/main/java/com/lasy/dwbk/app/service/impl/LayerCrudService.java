package com.lasy.dwbk.app.service.impl;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.geotools.data.DataStore;
import org.opengis.feature.simple.SimpleFeature;

import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.app.model.impl.LayerModelBuilder;
import com.lasy.dwbk.app.service.ADwbkCrudService;
import com.lasy.dwbk.db.tables.impl.LayerTable;
import com.lasy.dwbk.db.util.DbGeneratedLayerName;
import com.lasy.dwbk.ws.QueryParameters;

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
    // Update the query parameter parts!
    Map<String, String> params = QueryParameters.fromLayerUri(model.getUri()).getParams();
    String paramsJson = params.entrySet().stream()
      .sorted(Map.Entry.comparingByKey())
      .map(entry -> String.format("\"%s\":\"%s\"", entry.getKey(), entry.getValue()))
      .collect(Collectors.joining("," + System.lineSeparator(), "{", "}"));
    model.setQueryParts(paramsJson);
    
    this.updateWithoutAfterHandling(model);
  }

}