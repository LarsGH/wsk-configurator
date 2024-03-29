package com.lasy.wsk.app.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;

import org.geotools.data.DataStore;
import org.geotools.data.DataUtilities;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.util.factory.GeoTools;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

import com.lasy.wsk.app.error.ErrorModule;
import com.lasy.wsk.app.error.WskFrameworkException;
import com.lasy.wsk.app.model.IGtModel;
import com.lasy.wsk.app.model.IGtModelBuilder;
import com.lasy.wsk.util.Check;

public abstract class AWskCrudService<TModel extends IGtModel, TBuilder extends IGtModelBuilder<TModel>> implements IWskCrudService<TModel, TBuilder>
{

  private DataStore store;
  private String tableName;
  
  /**
   * Constructor.
   * @param store
   */
  public AWskCrudService(DataStore store, String tableName)
  {
    this.store = Check.notNull(store, "store"); 
    this.tableName = Check.trimmedNotEmpty(tableName, "tableName");
  }
  
  @Override
  public String getTableName()
  {
    return tableName;
  }
  
  @Override
  public Collection<TModel> readAll()
  {
    try
    {
      return loadFiltered(Filter.INCLUDE);
    }
    catch (Exception e)
    {
      // data could not be loaded
      throw ErrorModule.createFrameworkException(e, t -> WskFrameworkException
        .failForReason(t, "Daten ('%s') konnten nicht geladen werden.", getTableName()));
    }
  }

  @Override
  public Optional<TModel> readById(int id)
  {
    try
    {
      Filter idFilter = createIdFilter(id);
      Collection<TModel> parsedResults = loadFiltered(idFilter);
      return parsedResults.stream().findFirst();
    }
    catch (IOException e)
    {
      // data could not be loaded
      throw ErrorModule.createFrameworkException(e, t -> WskFrameworkException
        .failForReason(t, "Daten ('%s - ID: %s') konnten nicht geladen werden.", getTableName(), id));
    }
  }
  
  @Override
  public int deleteById(int id)
  {
    try
    {
      Filter idFilter = createIdFilter(id);
      SimpleFeatureStore featureStore = getSimpleFeatureStore();
      featureStore.removeFeatures(idFilter);
      return 1;
    }
    catch (Exception e)
    {
      // data could not be deleted
      throw ErrorModule.createFrameworkException(e, t -> WskFrameworkException
        .failForReason(t, "Daten ('%s - ID: %s') konnten nicht gelöscht werden.", getTableName(), id));
    }
  }
  
  private SimpleFeatureStore getSimpleFeatureStore()
  {
    try
    {
      SimpleFeatureSource source = store.getFeatureSource(getTableName());
      return (SimpleFeatureStore) source;
    }
    catch (Exception e)
    {
      // accessing table failed
      throw ErrorModule.createFrameworkException(e, t -> WskFrameworkException
        .failForReason(t, "Zugriff auf Tabelle '%s' ist fehlgeschlagen.", getTableName()));
    }
  }
  
  @Override
  public TModel create(TBuilder builder)
  {
    Check.notNull(builder, "builder");
    try
    {
      TModel model = builder.build();
      model.setLastChangedDate(LocalDateTime.now());
      SimpleFeature feature = model.getFeature();
      doCreate(feature);

      doAfterCreate(model);
      model = reloadModel(model);
      return model;
    }
    catch (Exception e)
    {
      // new entry could not be created
      throw ErrorModule.createFrameworkException(e, t -> WskFrameworkException
        .failForReason(t, "Neuer Eintrag ('%s') konnte nicht erstellt werden.", getTableName()));
    }
  }
  
  /**
   * Adds handling after the model was created.
   * @param model the previously created model.
   */
  protected abstract void doAfterCreate(TModel model);

  private TModel reloadModel(TModel model)
  {
    Integer id = model.getId();
    // data could not be reloaded
    model = readById(id)
      .orElseThrow(() -> WskFrameworkException
        .failForReason(null, "Daten ('%s - ID: %s') konnten nicht neu geladen werden.", getTableName(), id));
    return model;
  }
  
  private void doCreate(SimpleFeature feature) throws IOException
  {
    Transaction transaction = new DefaultTransaction(String.format("create_%s", getTableName()));
    try
    {
      SimpleFeatureStore featureStore = getSimpleFeatureStore();
      featureStore.setTransaction(transaction);
      SimpleFeatureCollection collection = DataUtilities.collection(feature);

      featureStore.addFeatures(collection);
      transaction.commit();
    }
    catch (Exception e)
    {
      transaction.rollback();
      throw e;
    }
    finally
    {
      transaction.close();
    }
  }
  
  @Override
  public TModel update(TModel model)
  {
    return update(model, true);
  }
  
  protected TModel updateWithoutAfterHandling(TModel model)
  {
    return update(model, false);
  }
  
  private TModel update(TModel model, boolean withAfterHandling)
  {
    Check.notNull(model, "model");

    Integer id = model.getId();
    try
    {
      doUpdate(model);
      model = reloadModel(model);
      
      if(withAfterHandling)
      {
        doAfterUpdate(model);
      }
      return model;
    }
    catch (Exception e)
    {
      // data could not be updated
      throw ErrorModule.createFrameworkException(e, t -> WskFrameworkException
          .failForReason(t, "Daten ('%s - ID: %s') konnten nicht aktualisiert werden.", getTableName(), id));
    }
  }
  
  /**
   * Adds handling after the model was updated.
   * @param model the previously updated model.
   */
  protected abstract void doAfterUpdate(TModel model);

  private void doUpdate(TModel model) throws IOException
  {
    Transaction transaction = new DefaultTransaction(String.format("update_%s", getTableName()));
    try
    {
      SimpleFeatureStore featureStore = getSimpleFeatureStore();
      featureStore.setTransaction(transaction);

      // update changed date
      model.setLastChangedDate(LocalDateTime.now());
      
      Map<Name, Object> attributes = getAttributes(model);
      List<Name> attributeNames = new ArrayList<Name>();
      List<Object> attributeValues = new ArrayList<Object>();
      
      for(Entry<Name, Object> attribute : attributes.entrySet())
      {
        attributeNames.add(attribute.getKey());
        attributeValues.add(attribute.getValue());
      }
      
      Filter idFilter = createIdFilter(model.getId());
      
      featureStore.modifyFeatures(
        attributeNames.toArray(new Name[attributeNames.size()]), 
        attributeValues.toArray(), 
        idFilter);
      transaction.commit();
    }
    catch (Exception e)
    {
      transaction.rollback();
      throw e;
    }
    finally
    {
      transaction.close();
    }
  }

  private Map<Name, Object> getAttributes(TModel model)
  {
    SimpleFeature feature = model.getFeature();
    Map<Name, Object> result = new HashMap<>();
    
    for(AttributeDescriptor attribute : feature.getFeatureType().getAttributeDescriptors())
    {
      Name name = attribute.getName();
      Object value = feature.getAttribute(name);
      result.put(name, value);
    }
    return result;
  }
  
  private Filter createIdFilter(int id)
  {
    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
    
    String featureIdName = String.format("%s.%s", getTableName(), id);
    FeatureId featureId = ff.featureId(featureIdName);
    
    return ff.id(featureId);
  }
  
  private Collection<TModel> loadFiltered(Filter filter) throws IOException
  {
    List<TModel> all = new ArrayList<>();

    SimpleFeatureStore featureStore = getSimpleFeatureStore();
    SimpleFeatureCollection features = featureStore.getFeatures(filter);

    SimpleFeatureIterator iterator = features.features();
    try
    {
      while (iterator.hasNext())
      {
        SimpleFeature f = iterator.next();
        TModel model = featureToModel().apply(f);
        all.add(model);
      }
    }
    finally
    {
      iterator.close();
    }

    return all;
  }
  
  /**
   * Returns a function to map from a GT feature to the model
   * @return function to map from a GT feature to the model
   */
  public abstract Function<SimpleFeature, TModel> featureToModel();
  
}
