package com.lasy.dwbk.app;

import java.io.File;
import java.util.logging.Level;

import com.lasy.dwbk.app.error.DwbkFrameworkException;
import com.lasy.dwbk.app.logging.DwbkLog;
import com.lasy.dwbk.util.Is;

/**
 * Access environment variables.
 * @author larss
 *
 */
public class DwbkEnvironment
{
  /**
   * The config directory (used for the DB and logs).
   */
  private static final String CONFIG_DIRECTORY = "DWBK_CONFIG_DIRECTORY";
  
  /**
   * The debug level (see logging {@link Level}).
   */
  private static final String CONFIG_DEBUG_LEVEL = "DWBK_CONFIG_DEBUG_LEVEL";
  
  private static final DwbkEnvironment env = new DwbkEnvironment();
  
  private final File configDirectory;
  private final Level configDebugLevel;
  
  private DwbkEnvironment()
  {
    this.configDirectory = getValidatedConfigDirectory();
    this.configDebugLevel = getValidatedDebugLevel();
  }
  
  private Level getValidatedDebugLevel()
  {
    String debugLevel = System.getenv(CONFIG_DEBUG_LEVEL);
    if(Is.nullOrTrimmedEmpty(debugLevel))
    {
      // Use default INFO
      Level fallback = Level.INFO;
      DwbkLog.log(Level.INFO, "Die Umgebungsvariable '%s' wurde nicht definiert. Benutze Fallback: '%s'.", fallback);
      return fallback;
    }
    
    try
    {
      return Level.parse(debugLevel);
    }
    catch (Exception e)
    {
      throw exitForReason(CONFIG_DEBUG_LEVEL, debugLevel, "Invalider Wert.");
    }
  }

  private File getValidatedConfigDirectory()
  {
    String path = System.getenv(CONFIG_DIRECTORY);
    if (Is.nullOrTrimmedEmpty(path))
    {
      throw exitForReason(CONFIG_DIRECTORY, path, "das Zielverzeichnis ist zwingend erforderlich!");
    }
    
    File configDirectory = new File(path);
    if(!configDirectory.isDirectory())
    {
      throw exitForReason(CONFIG_DIRECTORY, path, "das Zielverzeichnis muss ein Ordner sein!");
    }
    
    if(!configDirectory.canRead())
    {
      throw exitForReason(CONFIG_DIRECTORY, path, "Leserechte für das Zielverzeichnis sind zwingend erforderlich!");
    }
    if(!configDirectory.canWrite())
    {
      throw exitForReason(CONFIG_DIRECTORY, path, "Schreibrechte für das Zielverzeichnis sind zwingend erforderlich!");
    }
    
    return configDirectory;
  }
  
  private DwbkFrameworkException exitForReason(String envName, String value, String reason)
  {
    return DwbkFrameworkException.exitForReason(null, 
      "Umgebungsvariable-Fehlkonfiguration (%s=%s): %s", envName, value, reason); 
  }
  
  /**
   * Returns the config directory.
   * @return config directory
   */
  public static File getConfigDirectory()
  {
    return env.configDirectory;
  }
  
  /**
   * Returns the configured debug level (or INFO as default).
   * @return configured debug level
   */
  public static Level getDebugLevel()
  {
    return (env.configDebugLevel);
  }

  public static String getConfiguredInfo()
  {
    String dirInfo = String.format("%s = %s", CONFIG_DIRECTORY, getConfigDirectory().getAbsolutePath());
    String debugInfo = String.format("%s = %s", CONFIG_DEBUG_LEVEL, getDebugLevel());
    
    return String.join(System.lineSeparator(), dirInfo, debugInfo);
  }

}
