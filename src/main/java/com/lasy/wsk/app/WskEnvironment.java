package com.lasy.wsk.app;

import java.io.File;

import com.lasy.wsk.app.error.WskFrameworkException;
import com.lasy.wsk.util.Is;

/**
 * Access environment variables.
 * @author larss
 *
 */
public class WskEnvironment
{
  /**
   * The config directory (used for the DB and logs).
   */
  private static final String CONFIG_DIRECTORY = "WSK_CONFIG_DIRECTORY";
  
  private static final WskEnvironment env = new WskEnvironment();
  
  private final File configDirectory;
  
  private WskEnvironment()
  {
    this.configDirectory = getValidatedConfigDirectory();   
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
  
  private WskFrameworkException exitForReason(String envName, String value, String reason)
  {
    return WskFrameworkException.exitForReason(null, 
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
  
}
