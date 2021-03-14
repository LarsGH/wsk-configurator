package com.lasy.dwbk.gui.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.lasy.dwbk.util.Is;
import com.lasy.dwbk.validation.impl.LayerServiceUriValidator;
import com.lasy.dwbk.validation.impl.RgbaValidator;
import com.lasy.dwbk.validation.impl.TextOnlyValidator;

/**
 * Provides functions to validate the user input.
 * @author larss
 *
 */
public class AttributeInputValidator
{

  public static final <TType extends Object> Function<TType, Optional<String>> createMandatoryInputFunction() {
    return  val -> {
      if(val == null)
      {
        return Optional.of("Pflichtfeld!");
      }
      
      if(val instanceof String
        && Is.nullOrTrimmedEmpty((String) val))
      {
        return Optional.of("Pflichtfeld!");
      }
      
      return Optional.empty();
    };
  }
  
  public static final Function<String, Optional<String>> createLayerServiceFunction() {
    return  val -> {      
      boolean isValid = new LayerServiceUriValidator().isValid(val);
      if(!isValid)
      {
        return Optional.of("Die URI muss den anzufragenden Service (WMS / WFS) spezifizieren!");
      }
      return Optional.empty();
    };
  }
  
  public static final Function<String, Optional<String>> createRgbaValidationFunction() {
    return  val -> {      
      boolean isValid = new RgbaValidator().isValid(val);
      if(!isValid)
      {
        return Optional.of("Die Werte entsprechen nicht dem erlaubten RGBA-Format! "
          + "Werte mit Semikolon separiert. "
          + "RGB: 0-255, Alpha: 0-100");
      }
      return Optional.empty();
    };
  }
  
  public static Function<String, Optional<String>> createDivisorInputFunction(int num)
  {
    return val -> {
      if(Is.nullOrTrimmedEmpty(val))
      {
        return Optional.empty();
      }
      
      String[] parts = val.split(";");
      String errorValues = Stream.of(parts)
        .map(str -> Integer.valueOf(str))
        .filter(n -> (num % n) != 0)
        .map(n -> n.toString())
        .collect(Collectors.joining(", "));
      
      if(!Is.nullOrTrimmedEmpty(errorValues))
      {
        return Optional.of(String.format("Die folgenden Werte sind keine Teiler von %s: %s", num, errorValues));
      }
      return Optional.empty();
    };
  }
  
  public static final Function<String, Optional<String>> STRING_WITHOUT_DIGITS = val -> {
    boolean isValid = new TextOnlyValidator().isValid(val);
    if(!isValid)
    {
      return Optional.of("Es sind keine Zahlen erlaubt!");
    }
    return Optional.empty();
  };
  
}
