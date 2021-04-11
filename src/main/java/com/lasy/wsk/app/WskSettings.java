package com.lasy.wsk.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import com.lasy.wsk.app.error.WskFrameworkException;
import com.lasy.wsk.app.logging.WskLog;
import com.lasy.wsk.util.IWskJson;

/**
 * The user settings. The settings will be written to a file {@link WskSettings#SETTINGS_FILE_NAME}.
 * @author larss
 *
 */
public class WskSettings implements IWskJson
{
  /** 
   * The name of the setting file. The file will be stored relative to {@link WskEnvironment#getConfigDirectory()}. 
   */
  public static final String SETTINGS_FILE_NAME = "wsk_settings.json";
  
  /**
   * Creates the settings from the settings file content.
   * @param settingFileContent the file content
   * @return settings
   */
  public static WskSettings createFromFileContent(String settingFileContent)
  {
    try
    {
      WskSettings settings = IWskJson.createFromJson(WskSettings.class, settingFileContent);
      settings.validateAllFields();
      return settings;
    }
    catch (WskFrameworkException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw WskFrameworkException.exitForReason(e, "Settings ('%s') konnten nicht erstellt werden. Inhalt: %n%s", 
        SETTINGS_FILE_NAME, settingFileContent);
    }
  }

  private boolean allowSavePassword = true;
  private String logLevel = Level.INFO.getName();
  private int wmsMaxThreads = 8;
  
  /**
   * Returns {@code true} if the password is allowed to be stored in the database.
   * @return {@code true} if the password is allowed to be stored in the database
   */
  public boolean isSavePasswordAllowed()
  {
    return allowSavePassword;
  }
  
  /**
   * Returns the log level.
   * @return log level
   */
  public Level getLogLevel()
  {
    return Level.parse(logLevel);
  }
  
  /**
   * Returns the maximum number of threads to use for WMS requests.
   * @return maximum number of threads to use for WMS requests
   */
  public int getWmsMaxThreads()
  {
    return wmsMaxThreads;
  }
  
  /**
   * Validates all fields. 
   * Exits the application with failure logs if fields cannot be validated.
   */
  private void validateAllFields()
  {
    List<String> invalidSettings = new ArrayList<>();
    
    getLogLevelValidationError().ifPresent(invalidSettings::add);
    getMaxWmsThreadsValidationError().ifPresent(invalidSettings::add);
    
    if(invalidSettings.isEmpty())
    {
      WskLog.log(Level.INFO, "Settings ('%s') erfolgreich validiert: %n%s", SETTINGS_FILE_NAME, this.toString());
    }
    else
    {
      String errors = String.join(System.lineSeparator(), invalidSettings);
      throw WskFrameworkException.exitForReason(new IllegalStateException("Fehlerhafte Settings."), 
        "Settings ('%s') sind fehlerhaft: %n%s", SETTINGS_FILE_NAME, errors);
    }
  }
  
  private Optional<String> getMaxWmsThreadsValidationError()
  {
    if(this.wmsMaxThreads < 1)
    {
      return Optional.of(createValidationError("wmsMaxThreads", this.wmsMaxThreads, "Bitte geben Sie eine Ganzzahl > 0 ein."));
    }
    return Optional.empty();
  }
  
  private Optional<String> getLogLevelValidationError()
  {
    try
    {
      Level lvl = Level.parse(this.logLevel.toUpperCase());
      this.logLevel = lvl.getName();
      return Optional.empty();
    }
    catch (Exception e)
    {
      String expectedMsg = "Das Log-Level muss einem der folgenden Werte entsprechen: 'SEVERE', 'WARNING', 'INFO', 'CONFIG', 'FINE', 'FINER', 'FINEST'";
      return Optional.of(createValidationError("logLevel", this.logLevel, expectedMsg));
    }
  }
  
  private String createValidationError(String attributeName, Object attributeValue, String expectedMsg)
  {
    return String.format("Konfigurationsfehler [%s=%s]. Anforderungen an das Attribut '%s': %s", 
      attributeName, attributeValue, attributeName, expectedMsg);
  }

  @Override
  public String toString()
  {
    return asJson();
  }

}
