package com.lasy.dwbk.ws.wms;

import org.geotools.ows.wms.response.GetMapResponse;

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

  public GetMapResponse getResponse()
  {
    return response;
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
