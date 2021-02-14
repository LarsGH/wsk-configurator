package com.lasy.dwbk.ws.wms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.geotools.ows.wms.response.GetMapResponse;

import com.lasy.dwbk.app.error.DwbkFrameworkException;

/**
 * WMS tile response wrapper.
 * @author larss
 *
 */
public class WmsTileResponse
{

  private final GetMapResponse response;
  private final int zoom;
  private final int row;
  private final int col;

  public WmsTileResponse(GetMapResponse response, int zoom, int row, int col)
  {
    this.response = response;
    this.zoom = zoom;
    this.row = row;
    this.col = col;
  }

  private GetMapResponse checkResponse(GetMapResponse response)
  {
    String contentType = response.getContentType();
    if(!contentType.contains("image"))
    {
      throw DwbkFrameworkException.failForReason(createIllegalStateException(response), 
        "Fehlerhafte Antwort von GetMap-Request (Content type: '%s'). "
        + "Erwartet wird ein 'image/*' Content type.", contentType);
    }
    return response;
  }

  private IllegalStateException createIllegalStateException(GetMapResponse response)
  {
    if (response.getContentType().contains("text"))
    {
      String responseContent = new BufferedReader(new InputStreamReader(response.getInputStream(), StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining(System.lineSeparator()));
      return new IllegalStateException(responseContent);
    }
    return new IllegalStateException();
  }

  public GetMapResponse getResponse()
  {
    return checkResponse(response);
  }
  
  public int getZoom()
  {
    return zoom;
  }

  public int getRow()
  {
    return row;
  }

  public int getCol()
  {
    return col;
  }  

}
