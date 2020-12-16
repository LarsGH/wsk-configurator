package com.lasy.dwbk.app.model;

import com.lasy.dwbk.db.tables.IDwbkTable;

/**
 * Common builder interface for all GT models.
 * @author larss
 *
 * @param <TModel> the model which is built
 */
public interface IGtModelBuilder<TModel extends IGtModel>
{
  
  /** Default builder value to get a generated ID. */
  String GENERATE_ID = null;

  /**
   * Builds a new model.
   * @return new model
   */
  TModel build();
  
  /**
   * Returns the associated table.
   * @return table
   */
  IDwbkTable getTable();
}
