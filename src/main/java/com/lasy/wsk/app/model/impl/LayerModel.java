package com.lasy.wsk.app.model.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

import org.opengis.feature.simple.SimpleFeature;

import com.lasy.wsk.app.WskServiceProvider;
import com.lasy.wsk.app.model.AGtModel;
import com.lasy.wsk.db.tables.impl.LayerTable;
import com.lasy.wsk.db.util.DbBoolean;
import com.lasy.wsk.db.util.DbGeneratedLayerName;
import com.lasy.wsk.db.util.DbPasswordModifier;
import com.lasy.wsk.db.util.DbRowAccess;
import com.lasy.wsk.util.Check;
import com.lasy.wsk.util.IWskJson;
import com.lasy.wsk.util.Is;
import com.lasy.wsk.ws.EWebServiceType;
import com.lasy.wsk.ws.QueryConst;
import com.lasy.wsk.ws.wfs.WfsConfig;
import com.lasy.wsk.ws.wms.WmsConfig;

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

  /**
   * Returns the GET_CAPABILITIES request.
   * @return GET_CAPABILITIES request
   */
  public String getRequest()
  {
    String request = DbRowAccess.getMandatoryValue(getFeature(), LayerTable.COL_REQUEST, String.class); 
    return request;
  }

  /**
   * Sets the GET_CAPABILITIES request.
   * @param request GET_CAPABILITIES request
   */
  public void setRequest(String request)
  {
    Check.trimmedNotEmpty(request, "request");
    this.getFeature().setAttribute(LayerTable.COL_REQUEST, request);
    
    this.setWebServiceTypeByCapabilitiesRequest(request);
  }
  
  /**
   * Returns the base request URL until the first parameter (ending with '?'). 
   * @return base request URL
   */
  public String getBaseRequest()
  {
    String capabilitiesRequest = getRequest();
    String baseRequest = capabilitiesRequest.substring(0, (capabilitiesRequest.lastIndexOf("?")+1) );
    return baseRequest;
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
    BboxModel bbox = WskServiceProvider.getInstance().getBboxService().readById(id)
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

  private static final String MODEL_NAME = "Layer";

  @Override
  protected String getModelName()
  {
    return MODEL_NAME;
  }
  
  public EWebServiceType getWebServiceType()
  {
    String service = DbRowAccess.getMandatoryValue(getFeature(), LayerTable.COL_SERVICE, String.class);
    return EWebServiceType.valueOf(service);
  }
  
  private void setWebServiceType(EWebServiceType service)
  {
    this.getFeature().setAttribute(LayerTable.COL_SERVICE, service.toString());
  }
  
  private void setWebServiceTypeByCapabilitiesRequest(String request)
  {
    String serviceVal = getQueryParameterValue(QueryConst.SERVICE);
    EWebServiceType service = EWebServiceType.valueOf(serviceVal);
    
    setWebServiceType(service);
  }
  
  private String getServiceConfigJson()
  {
    String serviceConfig = DbRowAccess.getMandatoryValue(getFeature(), LayerTable.COL_SERVICE_CONFIG, String.class);
    return serviceConfig;
  }
  
  private void setServiceConfig(String serviceConfig)
  {
    this.getFeature().setAttribute(LayerTable.COL_SERVICE_CONFIG, serviceConfig);
  }
  
  public WmsConfig getWmsConfig()
  {
    EWebServiceType webServiceType = getWebServiceType();
    if(Objects.equals(webServiceType, EWebServiceType.WMS))
    {
      String configJson = getServiceConfigJson();
      WmsConfig wmsConfig = IWskJson.createFromJson(WmsConfig.class, configJson);
      return wmsConfig;
    }
    String msg = String.format("WMS configuration not available! layer type was: '%s'", webServiceType);
    throw new IllegalStateException(msg);
  }
  
  public void setWmsConfig(WmsConfig config)
  {
    String json = config.asJson();
    this.setServiceConfig(json);
  }
  
  public WfsConfig getWfsConfig()
  {
    EWebServiceType webServiceType = getWebServiceType();
    if(Objects.equals(webServiceType, EWebServiceType.WFS))
    {
      String configJson = getServiceConfigJson();
      WfsConfig wfsConfig = IWskJson.createFromJson(WfsConfig.class, configJson);
      return wfsConfig;
    }
    String msg = String.format("WFS configuration not available! layer type was: '%s'", webServiceType);
    throw new IllegalStateException(msg);
  }
  
  public void setWfsConfig(WfsConfig config)
  {
    String json = config.asJson();
    this.setServiceConfig(json);
  }
  
  /**
   * Returns the web service version.
   * @return web service version
   */
  public String getWebServiceVersion()
  {
    return getQueryParameterValue(QueryConst.VERSION);
  }
  
  private String getQueryParameterValue(String parameterName)
  {
    parameterName = parameterName.toUpperCase();
    String capabilitiesRequest = getRequest().toUpperCase();
    int paramStartIndex = capabilitiesRequest.lastIndexOf(parameterName);
    String restStartingWithParamName = capabilitiesRequest.substring(paramStartIndex);
    
    int nextParamStartIndex = restStartingWithParamName.indexOf("&");
    if(nextParamStartIndex > 0)
    {
      restStartingWithParamName = restStartingWithParamName.substring(0, nextParamStartIndex);
    }
    String val = restStartingWithParamName.replace((parameterName + "="), "");
    return val;
  }

}
