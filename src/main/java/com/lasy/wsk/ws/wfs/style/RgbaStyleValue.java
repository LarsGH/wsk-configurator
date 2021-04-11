package com.lasy.wsk.ws.wfs.style;

import java.util.Optional;

import com.lasy.wsk.util.Check;

public class RgbaStyleValue
{
  /**
   * Creates a new RGBA style value.
   * @param configString the configured value (RGBA semicolon separated)
   * @return RGBA style value
   */
  public static Optional<RgbaStyleValue> fromConfigString(String configString)
  {
    try {
      String[] rgba = configString.split(";");
      Integer r = Integer.valueOf(rgba[0].trim());
      Integer g = Integer.valueOf(rgba[1].trim());
      Integer b = Integer.valueOf(rgba[2].trim());
      Integer a = Integer.valueOf(rgba[3].trim());
      return Optional.of(new RgbaStyleValue(r, g, b, a));
    } catch (Exception e)
    {
      return Optional.empty();      
    }
  }
  
  private int red;
  private int green;
  private int blue;
  private int alpha;
  
  private RgbaStyleValue(int r, int g, int b, int a)
  {
    this.red = Check.validRgbValue(r);
    this.green = Check.validRgbValue(g);
    this.blue = Check.validRgbValue(b);
    this.alpha = Check.validAplhaValue(a);
  }

  public int getRed()
  {
    return red;
  }

  public int getGreen()
  {
    return green;
  }

  public int getBlue()
  {
    return blue;
  }

  public int getAlpha()
  {
    return alpha;
  }
  
  /**
   * Returns the configuration string for RGBA values.
   * @return configuration string for RGBA values (semicolon separated)
   */
  public String getConfigString()
  {
    return String.format("%s; %s; %s; %s", getRed(), getGreen(), getBlue(), getAlpha());
  }
  
}
