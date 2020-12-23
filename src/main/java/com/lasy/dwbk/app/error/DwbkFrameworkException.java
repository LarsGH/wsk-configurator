package com.lasy.dwbk.app.error;

import java.util.Objects;
import java.util.logging.Level;

import com.lasy.dwbk.gui.util.GuiUtil;
import com.lasy.dwbk.util.Check;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Framework-Exception.
 * @author larss
 *
 */
public class DwbkFrameworkException extends RuntimeException
{
  
  private static final long serialVersionUID = 1L;
  
  private final Level errorLevel;

  private DwbkFrameworkException(Throwable err, Level errorLvl, String msg, Object... args)
  {
    super(String.format(msg, args), err);
    this.errorLevel = Check.notNull(errorLvl, "errorLvl");
  }
  
  /**
   * Returns {@code true}, if the error levels are equal.
   * @param errorLvl the error level
   * @return {@code true}, if the error levels are equal
   */
  public boolean hasLevel(Level errorLvl)
  {
    return Objects.equals(errorLevel, errorLvl);
  }
  
  /**
   * Returns {@code true}, if the error level is below the input error level.
   * @param errorLvl the error level
   * @return {@code true}, if the error level is below the input error level
   */
  public boolean hasLevelBelow(Level errorLvl)
  {
    Check.notNull(errorLvl, "errorLvl");
    return errorLevel.intValue() < errorLvl.intValue();
  }
  
  /**
   * Returns the (log-) level.
   * @return level
   */
  public Level getLevel()
  {
    return errorLevel;
  }
  
  /**
   * Returns the error alert.
   * @return error alert
   */
  public Alert getErrorAlert()
  {
    String msg = String.join(System.lineSeparator(), getMessage(), "Details können dem Log entnommen werden.");
    return GuiUtil.createOkAlert(AlertType.ERROR, "Fehler aufgetreten", msg);
  }
  
  /**
   * Creates a framework exception that will generate an alert to inform the user about the failure.
   * The exception will be logged the application will close.
   * @param err cause
   * @param msg message
   * @param args message arguments
   * @return framework exception
   */
  public static DwbkFrameworkException exitForReason(Throwable err, String msg, Object... args)
  {
    return new DwbkFrameworkException(err, Level.SEVERE, msg, args);
  }
  
  /**
   * Creates a framework exception that will generate an alert to inform the user about the failure.
   * The exception will be logged!
   * 
   * @param err cause
   * @param msg message
   * @param args message arguments
   * @return framework exception
   */
  public static DwbkFrameworkException failForReason(Throwable err, String msg, Object... args)
  {
    return new DwbkFrameworkException(err, Level.WARNING, msg, args);
  }

}
