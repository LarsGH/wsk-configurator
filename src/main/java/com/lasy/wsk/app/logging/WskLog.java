package com.lasy.wsk.app.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.lasy.wsk.app.WskEnvironment;
import com.lasy.wsk.app.error.WskFrameworkException;

/**
 * Framework logger.
 * @author larss
 *
 */
public class WskLog
{
  /**
   * Logs the message.
   * @param level Log Level
   * @param msg message
   * @param args message arguments
   */
  public static void log(Level level, String msg, Object... args)
  {
    String message = String.format(msg, args);
    getInstance().logger.log(level, message);
  }
  
  public static void logFrameworkError(WskFrameworkException exception)
  {
    String message = String.format("Framework-Fehler: %s", exception.getMessage());
    getInstance().logger.log(exception.getLevel(), message, exception);
  }
  
  /**
   * Returns the logger.
   * @return logger
   */
  public static WskLog getInstance()
  {
    return instance;
  }
  
  private static WskLog instance = new WskLog();
  
  private final Logger logger = Logger.getLogger(WskLog.class.getSimpleName());
  
  private WskLog()
  {
    try
    {
      System.setProperty("java.util.logging.SimpleFormatter.format", createLogFormat());
      
      logger.addHandler(createFileHandler());
      // Default level
      logger.setLevel(Level.INFO);
    }
    catch (Exception e)
    {
      throw WskFrameworkException.failForReason(e, "Logger konnte nicht konfiguriert werden!");
    }
  }
  
  /**
   * Sets the log level.
   * @param level log level
   */
  public void setLogLevel(Level level)
  {
    this.logger.setLevel(level);
    WskLog.log(Level.INFO, "Neues Log-Level: %s", level.getName());
  }

  private String createLogFormat()
  {
    String timestamp = "%1$tF %1$tT";
    String level = "%4$s";
    String message = "%5$s";
    String exception = "%6$s";
    
    return String.format("[%s] [%s]: %s %s%n", timestamp, level, message, exception);
  }

  private FileHandler createFileHandler() throws IOException
  {
    int maxBytes = 30_000;
    int maxLogFiles = 1;
    FileHandler fileHandler = new FileHandler(getLogFile().getAbsolutePath(), maxBytes, maxLogFiles);  
    fileHandler.setFormatter(new SimpleFormatter());
    return fileHandler;
  }
  
  public static File getLogFile()
  {
    File logFile = new File(WskEnvironment.getConfigDirectory(), "wsk.log");
    if(!logFile.exists())
    {
      try
      {
        logFile.getParentFile().mkdirs();
        logFile.createNewFile();
      }
      catch (IOException e)
      {
        throw WskFrameworkException.exitForReason(e, "Log ('%s') konnte nicht erstellt werden!", logFile.getAbsolutePath());
      }
    }
    return logFile;
  }

  public void close()
  {
    for(Handler handler : logger.getHandlers())
    {
      // removes .lck files
      handler.close();
    }
  }
  
}
