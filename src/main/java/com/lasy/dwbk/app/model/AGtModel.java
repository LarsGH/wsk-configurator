package com.lasy.dwbk.app.model;

import java.util.Objects;

import org.opengis.feature.simple.SimpleFeature;

import com.google.common.base.Preconditions;

/**
 * Common abstraction for models on GT feature basis.
 * @author larss
 *
 */
public abstract class AGtModel implements IGtModel
{
  
  private SimpleFeature feature;

  public AGtModel(SimpleFeature feature)
  {
    this.feature = Preconditions.checkNotNull(feature);
  }

  @Override
  public Integer getId()
  {
    // feature id: '<table_name>.ID'
    String gtId = feature.getID();
    String id = gtId.split("\\.")[1];
    return Integer.valueOf(id);
  }
  
  @Override
  public SimpleFeature getFeature()
  {
    return feature;
  }
  
  @Override
  public boolean equals(Object other)
  {
    if(other == null)
    {
      return false;
    }

    return Objects.equals(this.getFeature(), ((AGtModel)other).getFeature());
  }

}
