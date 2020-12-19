package com.lasy.dwbk.app.model;

import java.time.LocalDateTime;
import java.util.Optional;

import org.opengis.feature.simple.SimpleFeature;

/**
 * Common interface for all GT feature models.
 * @author larss
 */
public interface IGtModel
{

  /**
   * Returns the ID
   * @return ID
   */
  Integer getId();
  
  /**
   * Returns the feature
   * @return feature attributes
   */
  SimpleFeature getFeature();
  
  /**
   * Returns the model name.
   * @return name
   */
  String getName();
  
  /**
   * Sets the model name.
   * @param name the name
   */
  void setName(String name);

  /**
   * Returns the model description.
   * @return the description
   */
  public Optional<String> getDescription();

  /**
   * Sets the model description.
   * @param description the description
   */
  public void setDescription(String description);
  
  /**
   * Returns the last changed date.
   * @return last changed date
   */
  public LocalDateTime getLastChangedDate();
  
  /**
   * Sets the last changed date on the model.
   * @param date last changed date
   */
  public void setLastChangedDate(LocalDateTime date);
  
  /**
   * Sets the last changed date to the current system time.
   */
  public void updateLastChangedDate();
  
}
