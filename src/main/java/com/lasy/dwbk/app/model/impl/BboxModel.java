package com.lasy.dwbk.app.model.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.opengis.feature.simple.SimpleFeature;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.lasy.dwbk.app.model.AGtModel;
import com.lasy.dwbk.db.tables.BboxTable;
import com.lasy.dwbk.db.util.DbRowAccess;
import com.lasy.dwbk.db.util.DwbkBigDecimal;

/**
 * Entity for a row from {@link BboxTable}.
 * @author larss
 */
public class BboxModel extends AGtModel
{
  
  /**
   * Creates a new builder.
   * @return builder
   */
  public static BboxModelBuilder builder(String name)
  {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
    return new BboxModelBuilder(name);
  }
  
  public BboxModel(SimpleFeature feature)
  {
    super(feature);
  }

  public String getName()
  {
    String name = DbRowAccess.getMandatoryValue(getFeature(), BboxTable.COL_NAME, String.class);
    return name;
  }

  public void setName(String name)
  {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
    this.getFeature().setAttribute(BboxTable.COL_NAME, name);
  }

  public Optional<String> getDescription()
  {
    String description = DbRowAccess.getValueElseNull(getFeature(), BboxTable.COL_DESCRIPTION, String.class);
    return Optional.ofNullable(description);
  }

  public void setDescription(String description)
  {
    this.getFeature().setAttribute(BboxTable.COL_DESCRIPTION, description);
  }

  public BigDecimal getMinLon()
  {
//    String minLon = DbRowAccess.getMandatoryValue(getFeature(), BboxTable.COL_MIN_LON, String.class);
//    return new BigDecimal(minLon);
    String val = getFeature().getAttribute(BboxTable.COL_MIN_LON).toString();
    return DwbkBigDecimal.create(val);
  }

  public void setMinLon(double minLon)
  {
    this.getFeature().setAttribute(BboxTable.COL_MIN_LON, BigDecimal.valueOf(minLon));
  }

  public BigDecimal getMinLat()
  {
//    String minLat = DbRowAccess.getMandatoryValue(getFeature(), BboxTable.COL_MIN_LAT, String.class);
//    return new BigDecimal(minLat);
    String val = getFeature().getAttribute(BboxTable.COL_MIN_LAT).toString();
    return DwbkBigDecimal.create(val);
  }

  public void setMinLat(double minLat)
  {
    this.getFeature().setAttribute(BboxTable.COL_MIN_LAT, BigDecimal.valueOf(minLat));
  }

  public BigDecimal getMaxLon()
  {
//    String maxLon = DbRowAccess.getMandatoryValue(getFeature(), BboxTable.COL_MAX_LON, String.class);
//    return new BigDecimal(maxLon);
    String val = getFeature().getAttribute(BboxTable.COL_MAX_LON).toString();
    return DwbkBigDecimal.create(val);
  }

  public void setMaxLon(double maxLon)
  {
    this.getFeature().setAttribute(BboxTable.COL_MAX_LON, BigDecimal.valueOf(maxLon));
  }

  public BigDecimal getMaxLat()
  {
//    String maxLat = DbRowAccess.getMandatoryValue(getFeature(), BboxTable.COL_MAX_LAT, String.class);
//    return new BigDecimal(maxLat);
    String val = getFeature().getAttribute(BboxTable.COL_MAX_LAT).toString();
    return DwbkBigDecimal.create(val);
  }

  public void setMaxLat(double maxLat)
  {
    this.getFeature().setAttribute(BboxTable.COL_MAX_LON, BigDecimal.valueOf(maxLat));
  }
  
  @Override
  public String toString()
  {
    String description = getDescription().isPresent()
      ? " (" + getDescription().get() + ") "
      : "";
    
    return String.format("BBox-%s: %s%s [lowerLeft (%s | %s) | upperRight (%s | %s)]",
      getId(),
      getName(),
      description,
      getMinLon(),
      getMinLat(),
      getMaxLon(),
      getMaxLat());
  }

}
