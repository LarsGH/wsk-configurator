package com.lasy.wsk.app.error;

import java.util.function.Function;
import java.util.logging.Level;

import com.lasy.wsk.app.logging.WskLog;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Handles framework errors.
 * @author larss
 *
 */
public class ErrorModule
{
  
  /**
   * Creates a {@link WskFrameworkException}.
   * @param thrown the exception to wrap (or throw)
   * @param frameworkExceptionFunction function to transform the original error to a framework error
   */
  public static WskFrameworkException createFrameworkException(
    Throwable thrown,
    Function<Throwable, WskFrameworkException> frameworkExceptionFunction)
  {
    if(thrown instanceof WskFrameworkException)
    {
      return (WskFrameworkException) thrown;
    }
    
    return frameworkExceptionFunction.apply(thrown);
  }
  
  /**
   * Handles (framework) errors.
   * @param thrown the error to handle
   */
  public static void handleError(Throwable thrown)
  {
    if(thrown instanceof WskFrameworkException)
    {
      handleFrameworkException((WskFrameworkException) thrown);
    }
    else
    {
      WskFrameworkException frameworkException = WskFrameworkException.failForReason(thrown, "Unerwarteter Fehler");
      handleFrameworkException(frameworkException);
    }
  }
  
  private static void handleFrameworkException(WskFrameworkException frameworkException)
  {
    WskLog.logFrameworkError(frameworkException);
    showErrorAlert(frameworkException);

    if (frameworkException.hasLevel(Level.SEVERE))
    {
      System.exit( -1);
    }
  }
  
  private static void showErrorAlert(WskFrameworkException frameworkException)
  {
    try
    {
      Platform.runLater(() -> {
        Alert alert = frameworkException.getErrorAlert();
        alert.showAndWait();
      });
    }
    catch (Exception e)
    {
      // ignore! Framework GUI was not loaded...
    }
  }
}
