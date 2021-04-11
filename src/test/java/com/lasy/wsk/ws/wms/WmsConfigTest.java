package com.lasy.wsk.ws.wms;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.lasy.wsk.util.IWskJson;
import com.lasy.wsk.ws.wms.WmsConfig;

/**
 * Tests {@link WmsConfig}.
 * @author larss
 *
 */
public class WmsConfigTest
{

private static final String WMS_CONFIG_JSON = "wmsConfig.json";
  
  /**
   * Tests, that the configuration can be created from a JSON string.
   * Also tests that the properties are set correctly.
   */
  @Test
  public void testCreateFromJson()
  {
    String json = loadFromResource(WMS_CONFIG_JSON);
    
    WmsConfig sut = IWskJson.createFromJson(WmsConfig.class, json);
    
    Assertions.assertThat(sut.getFormat()).isEqualTo("jpg");
    Assertions.assertThat(sut.getLayer()).isEqualTo("testLayer");
    Assertions.assertThat(sut.getMetersPerPixel()).containsExactly(1,5,10);
    Assertions.assertThat(sut.getRequestEpsg()).isEqualTo(25832);
    Assertions.assertThat(sut.getStyles()).isEqualTo("testStyle");
    
    Assertions.assertThat(sut.asJson()).isEqualToIgnoringWhitespace(json);
  }
  
  /**
   * Tests, that the meters per pixel String is parse correctly.
   */
  @Test
  public void testSetMetersPerPixel()
  {
    WmsConfig sut = new WmsConfig();
    
    sut.setMetersPerPixel("20;1;10");
    
    Assertions.assertThat(sut.getMetersPerPixel()).containsExactly(1,10,20);
    Assertions.assertThat(sut.getMetersPerPixelAsText()).isEqualTo("1;10;20");
  }
  
  /**
   * Tests, that the meters per pixel are created correctly per zoomlevel.
   */
  @Test
  public void testGetMetersPerPixelPerZoomLevel()
  {
    WmsConfig sut = new WmsConfig();
    
    sut.setMetersPerPixel("1;5;10;20");
    
    Assertions.assertThat(sut.getMetersPerPixel()).containsExactly(1,5,10,20);
    Assertions.assertThat(sut.getMetersPerPixelAsText()).isEqualTo("1;5;10;20");
    
    Assertions.assertThat(sut.getMetersPerPixelPerZoomLevel()).containsExactly(
      Map.entry(0, 20),
      Map.entry(1, 10),
      Map.entry(2, 5),
      Map.entry(3, 1));
  }
  
  private String loadFromResource(String resourceName)
  {
    InputStream inputStream = WmsConfigTest.class.getResourceAsStream(resourceName);
    String text = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
      .lines()
      .collect(Collectors.joining(System.lineSeparator()));
    return text;
  }
  
}
