package com.lasy.dwbk.app;

import java.io.File;

import com.lasy.dwbk.app.error.DwbkFrameworkException;
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
  
  private static final DwbkEnvironment env = new DwbkEnvironment();
  
  private final File configDirectory;
  
  private DwbkEnvironment()
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
  
}
