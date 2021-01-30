package com.lasy.dwbk.app.error;

import java.util.function.Function;
import java.util.logging.Level;

import com.lasy.dwbk.app.logging.DwbkLog;

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
   * Creates a {@link DwbkFrameworkException}.
   * @param thrown the exception to wrap (or throw)
   * @param frameworkExceptionFunction function to transform the original error to a framework error
   */
  public static DwbkFrameworkException createFrameworkException(
    Throwable thrown,
    Function<Throwable, DwbkFrameworkException> frameworkExceptionFunction)
  {
    if(thrown instanceof DwbkFrameworkException)
    {
      return (DwbkFrameworkException) thrown;
    }
    
    return frameworkExceptionFunction.apply(thrown);
  }
  
  /**
   * Handles (framework) errors.
   * @param thrown the error to handle
   */
  public static void handleError(Throwable thrown)
  {
    if(thrown instanceof DwbkFrameworkException)
    {
      handleFrameworkException((DwbkFrameworkException) thrown);
    }
    else
    {
      DwbkFrameworkException frameworkException = DwbkFrameworkException.failForReason(thrown, "Unerwarteter Fehler");
      handleFrameworkException(frameworkException);
    }
  }
  
  private static void handleFrameworkException(DwbkFrameworkException frameworkException)
  {
    DwbkLog.logFrameworkError(frameworkException);
    showErrorAlert(frameworkException);

    if (frameworkException.hasLevel(Level.SEVERE))
    {
      System.exit( -1);
    }
  }
  
  private static void showErrorAlert(DwbkFrameworkException frameworkException)
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
