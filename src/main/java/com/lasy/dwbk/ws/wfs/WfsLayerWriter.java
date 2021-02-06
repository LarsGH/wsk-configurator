package com.lasy.dwbk.ws.wfs;

import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.geopkg.FeatureEntry;
import org.geotools.geopkg.GeoPackage;
import org.geotools.util.factory.GeoTools;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.spatial.BBOX;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.lasy.dwbk.app.DwbkFramework;
import com.lasy.dwbk.app.error.ErrorModule;
import com.lasy.dwbk.app.logging.DwbkLog;
import com.lasy.dwbk.app.model.impl.LayerModel;
import com.lasy.dwbk.db.util.DbScriptUtil;
import com.lasy.dwbk.util.BboxUtil;
import com.lasy.dwbk.util.Check;
import com.lasy.dwbk.ws.ILayerWriter;

/**
 * Writes the layer content to the geopackage.
 * @author larss
 */
public class WfsLayerWriter implements ILayerWriter
{

  private final LayerModel layer;

  public WfsLayerWriter(LayerModel layer)
  {
    this.layer = Check.notNull(layer, "layer");
  }

  @Override
  public void write()
  {
    try
    {
      DbScriptUtil.deleteLocalWfsLayerContentIfPresent(this.layer);

      // Get WFS datastore
      IWfsRequestParameters requestParameters = WfsRequestParameters.fromRequestParameters(this.layer.getRequestParameters());
      Map<String, String> connectionParameters = createWfsDatastoreParams(requestParameters);
      DataStore dataStore = DataStoreFinder.getDataStore(connectionParameters);

      String typeNames = requestParameters.getTypeNames();
      SimpleFeatureSource source = dataStore.getFeatureSource(typeNames);

      // Bbox filter
      SimpleFeatureType origFeatureType = source.getSchema();
      BBOX filter = createBboxFilter(origFeatureType);

      SimpleFeatureCollection features = source.getFeatures(filter);
      DwbkLog.log(Level.INFO, "WFS Features '%s' erfolgreich geladen.", typeNames);

      // Write features to geopackage
      FeatureEntry entry = createFeatureEntry(origFeatureType);
      GeoPackage gpkg = DwbkFramework.getInstance().getDwbkGeoPackage().getGtGeoPackage();
      gpkg.add(entry, features);
      DwbkLog.log(Level.INFO, "WFS Features '%s' erfolgreich in Geopackage gespeichert.", typeNames);

      // Update new tablename (default name is the WFS service layer name)
      String wfsTableName = origFeatureType.getTypeName();
      DbScriptUtil.updateWfsLayerName(this.layer, wfsTableName);
      
      DwbkLog.log(Level.INFO, "WFS Layer '%s' erfolgreich geschrieben.", this.layer.getName());
    }
    catch (Exception e)
    {
      ErrorModule.handleError(e);
    }

  }

  private FeatureEntry createFeatureEntry(SimpleFeatureType origFeatureType)
  {
    FeatureEntry entry = new FeatureEntry();
    CoordinateReferenceSystem crs = origFeatureType.getCoordinateReferenceSystem();
    int epsg = BboxUtil.getEpsgCodeFromCrs(crs);
    entry.setSrid(epsg);
    entry.setBounds(BboxUtil.getEnvelopeForBboxInCrs(this.layer.getBbox(), crs));
    return entry;
  }

  private Map<String, String> createWfsDatastoreParams(IWfsRequestParameters requestParameters)
  {
    String capablitiesRequest = requestParameters.getCapablitiesRequest();
    return Map.of("WFSDataStoreFactory:GET_CAPABILITIES_URL", capablitiesRequest);
  }

  private BBOX createBboxFilter(SimpleFeatureType schema)
  {
    String geomName = schema.getGeometryDescriptor().getLocalName();
    FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());
    ReferencedEnvelope bbox = BboxUtil.getEnvelopeForBbox(this.layer.getBbox());
    BBOX filter = ff.bbox(ff.property(geomName), bbox);
    return filter;
  }

  @SuppressWarnings("unused")
  private String getGtFeatureString(SimpleFeature feature)
  {
    String attributeInfo = feature.getFeatureType().getAttributeDescriptors().stream().map(attrDesc -> {
      String attributeName = attrDesc.getLocalName();
      Object attributeValue = feature.getAttribute(attributeName);
      return attributeName + ": " + attributeValue;
    }).collect(Collectors.joining(System.lineSeparator()));
    return String.format("## %s", attributeInfo);
  }
}
