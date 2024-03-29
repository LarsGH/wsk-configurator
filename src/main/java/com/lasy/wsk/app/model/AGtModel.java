package com.lasy.wsk.app.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opengis.feature.simple.SimpleFeature;

import com.lasy.wsk.db.tables.IWskTable;
import com.lasy.wsk.db.util.DbRowAccess;
import com.lasy.wsk.util.Check;

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
    this.feature = Check.notNull(feature, "feature");
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
  public String getName()
  {
    String name = DbRowAccess.getMandatoryValue(getFeature(), IWskTable.COL_NAME, String.class);
    return name;
  }

  @Override
  public void setName(String name)
  {
    Check.trimmedNotEmpty(name, "name");
    this.getFeature().setAttribute(IWskTable.COL_NAME, name);
  }

  @Override
  public Optional<String> getDescription()
  {
    String description = DbRowAccess.getValueElseNull(getFeature(), IWskTable.COL_DESCRIPTION, String.class);
    return Optional.ofNullable(description);
  }

  @Override
  public void setDescription(String description)
  {
    this.getFeature().setAttribute(IWskTable.COL_DESCRIPTION, description);
  }
  
  @Override
  public LocalDateTime getLastChangedDate()
  {
    String lastChanged = DbRowAccess.getMandatoryValue(getFeature(), IWskTable.COL_LAST_CHANGED, String.class);
    return LocalDateTime.parse(lastChanged);
  }
  
  @Override
  public void setLastChangedDate(LocalDateTime date)
  {
    Check.notNull(date, "date");
    this.getFeature().setAttribute(IWskTable.COL_LAST_CHANGED, date.truncatedTo(ChronoUnit.SECONDS).toString());
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
  
  @Override
  public String toString()
  {
    String attributeInfo = feature.getFeatureType().getAttributeDescriptors()
      .stream()
      .map(attrDesc -> {
        String attributeName = attrDesc.getLocalName();
        Object attributeValue = feature.getAttribute(attributeName);
        return attributeName + ": " + attributeValue;
      })
      .collect(Collectors.joining(System.lineSeparator()));
    return String.format("## %s:%n%s", getModelName(), attributeInfo);
  }

  protected abstract String getModelName();

}
