package com.lasy.dwbk.app.model.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.opengis.feature.simple.SimpleFeature;

import com.google.gson.GsonBuilder;
import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.model.AGtModel;
import com.lasy.dwbk.db.tables.impl.LayerTable;
import com.lasy.dwbk.db.util.DbBoolean;
import com.lasy.dwbk.db.util.DbGeneratedLayerName;
import com.lasy.dwbk.db.util.DbPasswordModifier;
import com.lasy.dwbk.db.util.DbRowAccess;
import com.lasy.dwbk.util.Check;
import com.lasy.dwbk.util.Is;
import com.lasy.dwbk.ws.IRequestParameters;
import com.lasy.dwbk.ws.RequestParameters;

/**
 * Entity for a row from {@link LayerTable}.
 * @author larss
 */
public class LayerModel extends AGtModel
{
  /**
   * Creates a new builder.
   * @return builder
   */
  public static LayerModelBuilder builder(String name)
  {
    Check.trimmedNotEmpty(name, "name");
    return new LayerModelBuilder(name);
  }
  
  public LayerModel(SimpleFeature feature)
  {
    super(feature);
  }

  public String getUri()
  {
    String name = DbRowAccess.getMandatoryValue(getFeature(), LayerTable.COL_URI, String.class);
    return name;
  }

  public void setUri(String uri)
  {
    Check.trimmedNotEmpty(uri, "uri");
    this.getFeature().setAttribute(LayerTable.COL_URI, uri);
    
    IRequestParameters requestParams = RequestParameters.fromLayerUri(uri);
    this.setRequestParameters(requestParams);
  }

  public boolean isStoreLocal()
  {
    String isStoreLocal = DbRowAccess.getMandatoryValue(getFeature(), LayerTable.COL_STORE_LOCAL, String.class);
    return DbBoolean.fromDbValue(isStoreLocal);
  }

  public void setStoreLocal(boolean storeLocal)
  {
    this.getFeature().setAttribute(LayerTable.COL_STORE_LOCAL, DbBoolean.toDbValue(storeLocal));
    if(storeLocal == false)
    {
      setLastDownloadDate(null);
    }
  }
  
  public Optional<LocalDateTime> getLastDownloadDate()
  {
    String lastChanged = DbRowAccess.getValueElseNull(getFeature(), LayerTable.COL_LAST_DL, String.class);
    if(Is.nullOrTrimmedEmpty(lastChanged))
    {
      return Optional.empty();
    }
    
    return Optional.of(LocalDateTime.parse(lastChanged));
  }
  
  public void setLastDownloadDate(LocalDateTime date)
  {
    String insertDate = date != null
      ? date.truncatedTo(ChronoUnit.SECONDS).toString()
      : null;
    this.getFeature().setAttribute(LayerTable.COL_LAST_DL, insertDate);
  }

  public boolean isVisible()
  {
    String isSaved = DbRowAccess.getMandatoryValue(getFeature(), LayerTable.COL_IS_VISIBLE, String.class);
    return DbBoolean.fromDbValue(isSaved);
  }

  public void setVisible(boolean isVisible)
  {
    this.getFeature().setAttribute(LayerTable.COL_IS_VISIBLE, DbBoolean.toDbValue(isVisible));
  }

  public Integer getBboxId()
  {
    Integer bboxId = DbRowAccess.getMandatoryValue(getFeature(), LayerTable.COL_BBOX_ID, Integer.class);
    return bboxId;
  }

  public void setBboxId(Integer bboxId)
  {
    this.getFeature().setAttribute(LayerTable.COL_BBOX_ID, bboxId);
  }

  public BboxModel getBbox()
  {
    Integer id = getBboxId();
    BboxModel bbox = DwbkServiceProvider.getInstance().getBboxService().readById(id)
      .orElseThrow(() -> new IllegalStateException(String.format("Boundingbox with ID %s not found!", id)));
    return bbox;
  }

  public Optional<String> getUser()
  {
    String user = DbRowAccess.getValueElseNull(getFeature(), LayerTable.COL_USER, String.class);
    return Optional.ofNullable(user);
  }

  public void setUser(String user)
  {
    this.getFeature().setAttribute(LayerTable.COL_USER, user);
  }

  public Optional<String> getPw()
  {
    String pw = DbRowAccess.getValueElseNull(getFeature(), LayerTable.COL_PW, String.class);
    return Optional.ofNullable(DbPasswordModifier.fromDbValue(pw));
  }

  public void setPw(String pw)
  {
    if(!Is.nullOrTrimmedEmpty(pw))
    {
      pw = DbPasswordModifier.toDbValue(pw);
    }
    this.getFeature().setAttribute(LayerTable.COL_PW, pw);
  }

  /**
   * Returns a map that links the zoom level to the meters per pixel value for that zoom.
   * @return Map: zoom level -> meters per pixel
   */
  public Map<Integer, Integer> getMetersPerPixelPerZoomLevel()
  {
    String metersPerPixel = getMetersPerPixelText();
    if(Is.nullOrTrimmedEmpty(metersPerPixel))
    {
      return Collections.emptyMap();
    }
    
    AtomicInteger index = new AtomicInteger();
    return Stream.of(metersPerPixel.split(";"))
      .map(val -> Integer.valueOf(val))
      .sorted(Comparator.reverseOrder())
      .collect(Collectors.toMap(val -> index.getAndIncrement(), val -> val,
        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
  }
  
  /**
   * Returns the pixel resolution in meters per zoom level. Values are decreasing and separated by semicolons. 
   * @return pixel resolution in meters per zoom level.
   */
  public String getMetersPerPixelText()
  {
    String metersPerPixel = DbRowAccess.getValueElseNull(getFeature(), LayerTable.COL_PIXEL_METERS, String.class);
    return metersPerPixel;
  }
  
  /**
   * Sets the meters per pixel in the correct format.
   * @param metersPerPixel integer string separated by semicolons
   */
  public void setMetersPerPixelText(String metersPerPixel)
  {
    if(metersPerPixel != null)
    {
      // ensure correct formatting
      metersPerPixel = Check.numbersSeparatedBySemicolons(metersPerPixel, "metersPerPixel");      
    }
    this.getFeature().setAttribute(LayerTable.COL_PIXEL_METERS, metersPerPixel);
  }
  
  /**
   * Returns the local name for this layer (used to identify this layer in the DB). 
   * The name is generated by the ID when the layer is created ({@link DbGeneratedLayerName}).
   * 
   * @return local name for this layer (used to identify this layer in the DB)
   */
  public String getLocalName()
  {
    String localName = DbRowAccess.getMandatoryValue(getFeature(), LayerTable.COL_LOCAL_NAME, String.class);
    return localName;
  }
  
  public void setLocalName(String localName)
  {
    this.getFeature().setAttribute(LayerTable.COL_LOCAL_NAME, localName);
  }
  
  /**
   * Returns the parts of the query as JSON-String (used in App).
   * 
   * @return parts of the query as JSON-String
   */
  public IRequestParameters getRequestParameters()
  {
    String parts = DbRowAccess.getMandatoryValue(getFeature(), LayerTable.COL_QUERY_PARTS, String.class);
    return RequestParameters.fromJson(parts);
  }
  
  private void setRequestParameters(IRequestParameters requestParams)
  {
    String parts = requestParams != null
      ? new GsonBuilder().setPrettyPrinting().create().toJson(requestParams.getParams())
      : null;
    this.getFeature().setAttribute(LayerTable.COL_QUERY_PARTS, parts);
  }

  private static final String MODEL_NAME = "Layer";

  @Override
  protected String getModelName()
  {
    return MODEL_NAME;
  }

}
