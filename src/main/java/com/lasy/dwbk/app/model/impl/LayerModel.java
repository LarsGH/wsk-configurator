package com.lasy.dwbk.app.model.impl;

import java.util.Optional;

import org.opengis.feature.simple.SimpleFeature;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.lasy.dwbk.app.DwbkServiceProvider;
import com.lasy.dwbk.app.model.AGtModel;
import com.lasy.dwbk.db.tables.LayerTable;
import com.lasy.dwbk.db.util.DbBoolean;
import com.lasy.dwbk.db.util.DbRowAccess;

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
    Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
    return new LayerModelBuilder(name);
  }
  
  public LayerModel(SimpleFeature feature)
  {
    super(feature);
  }

  public String getName()
  {
    String name = DbRowAccess.getMandatoryValue(getFeature(), LayerTable.COL_NAME, String.class);
    return name;
  }

  public void setName(String name)
  {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
    this.getFeature().setAttribute(LayerTable.COL_NAME, name);
  }

  public String getUri()
  {
    String name = DbRowAccess.getMandatoryValue(getFeature(), LayerTable.COL_NAME, String.class);
    return name;
  }

  public void setUri(String uri)
  {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(uri));
    this.getFeature().setAttribute(LayerTable.COL_URI, uri);
  }

  public boolean isStoreLocal()
  {
    String isStoreLocal = DbRowAccess.getMandatoryValue(getFeature(), LayerTable.COL_STORE_LOCAL, String.class);
    return DbBoolean.fromDbValue(isStoreLocal);
  }

  public void setStoreLocal(boolean storeLocal)
  {
    this.getFeature().setAttribute(LayerTable.COL_STORE_LOCAL, DbBoolean.toDbValue(storeLocal));
  }

  public Optional<Integer> getBboxId()
  {
    Integer bboxId = DbRowAccess.getValueElseNull(getFeature(), LayerTable.COL_BBOX_ID, Integer.class);
    return Optional.ofNullable(bboxId);
  }

  public void setBboxId(Integer bboxId)
  {
    this.getFeature().setAttribute(LayerTable.COL_BBOX_ID, bboxId);
  }
  
  public Optional<BboxModel> getBbox()
  {
    if(getBboxId().isPresent())
    {
      Integer id = getBboxId().get();
      BboxModel bbox = DwbkServiceProvider.getInstance()
        .getBboxService()
        .readById(id)
        .orElseThrow(() -> new IllegalStateException(String.format("Boundingbox with ID %s not found!", id)));
      return Optional.of(bbox);
    }
    return Optional.empty();
  }
  
  public Optional<String> getUser()
  {
    String user = DbRowAccess.getMandatoryValue(getFeature(), LayerTable.COL_USER, String.class);
    return Optional.ofNullable(user);
  }

  public void setUser(String user)
  {
    this.getFeature().setAttribute(LayerTable.COL_USER, user);
  }

  public Optional<String> getPw()
  {
    String pw = DbRowAccess.getMandatoryValue(getFeature(), LayerTable.COL_PW, String.class);
    return Optional.ofNullable(pw);
  }

  public void setPw(String pw)
  {
    this.getFeature().setAttribute(LayerTable.COL_PW, pw);
  }
  
  @Override
  public String toString()
  {
    String localInfo = this.isStoreLocal() 
      ? "[LOCAL]" 
      : "[REMOTE]";
    
    return String.format("Layer-%s-%s: %s | Bbox: %s | ConInfo: %s",
      getId(),
      localInfo,
      getName(),
      getBboxId(),
      getConnectionInfo());
  }

  private String getConnectionInfo()
  {
    String info = "[";
    
    if(getUser().isPresent());
    {
      info += "(WITH_LOGIN) | ";
    }
    info += "uri: " + getUri() + "]";
    return info;
  }

}
