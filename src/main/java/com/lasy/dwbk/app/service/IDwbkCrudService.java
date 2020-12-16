package com.lasy.dwbk.app.service;

import java.util.Collection;
import java.util.Optional;

import com.lasy.dwbk.app.model.IGtModel;
import com.lasy.dwbk.app.model.IGtModelBuilder;

/**
 * Repository for config table contents.
 * @author larss
 *
 * @param <TModel> Model type for table contents
 * @param <TBuilder> Builder type to create a new model
 */
public interface IDwbkCrudService<TModel extends IGtModel, TBuilder extends IGtModelBuilder<TModel>>
{
  
  /**
   * Loads all models.
   * @return all models
   */
  Collection<TModel> readAll();
  
  /**
   * Loads the model by ID.
   * @param id the ID
   * @return the model
   */
  Optional<TModel> readById(int id);
  
  /**
   * Deletes a model by id.
   * @param id ID to delete
   * @return delete count
   */
  int deleteById(int id);
  
  /**
   * Modifies a model.
   * @param model the model
   * @return the model
   */
  TModel update(TModel model);
  
  /**
   * Creates a new model from the provided builder
   * @param builder builder
   * @return new model
   */
  TModel create(TBuilder builder);
  
  /**
   * Returns the table name to access data from.
   * @return table name to access data from
   */
  String getTableName();
}
