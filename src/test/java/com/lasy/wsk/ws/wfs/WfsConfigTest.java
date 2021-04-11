package com.lasy.wsk.ws.wfs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.lasy.wsk.util.IWskJson;
import com.lasy.wsk.ws.wfs.WfsConfig;

/**
 * Tests {@link WfsConfig}.
 * @author larss
 *
 */
public class WfsConfigTest
{
  private static final String WFS_CONFIG_JSON = "wfsConfig.json";
  
  /**
   * Tests, that the configuration can be created from a JSON string.
   * Also tests that the properties are set correctly.
   */
  @Test
  public void testCreateFromJson()
  {
    String json = loadFromResource(WFS_CONFIG_JSON);
    
    WfsConfig sut = IWskJson.createFromJson(WfsConfig.class, json);
    
    Assertions.assertThat(sut.getTypeNames()).isEqualTo("testLayer");
    Assertions.assertThat(sut.asJson()).isEqualToIgnoringWhitespace(json);
  }
  
  private String loadFromResource(String resourceName)
  {
    InputStream inputStream = WfsConfigTest.class.getResourceAsStream(resourceName);
    String text = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
      .lines()
      .collect(Collectors.joining(System.lineSeparator()));
    return text;
  }
}
