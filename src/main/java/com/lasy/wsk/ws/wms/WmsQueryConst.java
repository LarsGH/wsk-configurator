package com.lasy.wsk.ws.wms;

import com.lasy.wsk.ws.QueryConst;

/**
 * WMS query constants. (https://docs.geoserver.org/stable/en/user/services/wms/reference.html)
 * @author larss
 *
 */
public class WmsQueryConst
{
  /**
   * WMS operation constants.
   */
  public static class Operations extends com.lasy.wsk.ws.QueryConst.Operations {
    
    /**
     * GetMap.
     */
    public static final String GET_MAP = "GetMap";
    
    /**
     * GetFeatureInfo.
     */
    public static final String GET_FEATURE_INFO = "GetFeatureInfo";
    
    /**
     * DescribeLayer .
     */
    public static final String DESCRIBE_LAYER = "DescribeLayer ";
    
    /**
     * GetLegendGraphic.
     */
    public static final String GET_LEGEND_GRAPHIC = "GetLegendGraphic";
  }

  /**
   * GetCapabilities constants.
   * @author larss
   *
   */
  public static class GetCapabilities extends QueryConst {
    
    /**
     * limits response to layers in a given namespace.
     */
    public static final String NAMESPACE = "namespace";
    
    /**
     * request the capabilities document in a certain format
     */
    public static final String FORMAT = "format";
    
    /**
     * Flag to enable/disable the standard Root top level Layer element. 
     * Values are true or false. <p/>
     * When false, the Root element will be included only if there are multiple top level layers, 
     * if there is only one, it will be the root layer itself. 
     * When specified, will override the global WMS setting or layer / group setting for the same behaviour.
     */
    public static final String ROOTLAYER = "rootLayer";
  }

  /**
   * GetMap constants.
   * @author larss
   *
   */
  public static class GetMap extends QueryConst {
    
    /**
     * Layers to display on map. Value is a comma-separated list of layer names. (mandatory)
     */
    public static final String LAYERS = "layers";
    
    /**
     *  Styles in which layers are to be rendered. 
     *  Value is a comma-separated list of style names, or empty if default styling is required.
     *  Style names may be empty in the list, to use default layer styling. (mandatory)
     */
    public static final String STYLES = "styles";
    
    /**
     * Spatial Reference System for map output. Value is in form EPSG:nnn. (mandatory)
     * <p/><b>Use CRS for version 1.3.0!</b>
     */
    public static final String SRS = "srs";
    
    /**
     * Spatial Reference System for map output. Value is in form EPSG:nnn. (mandatory)
     * <p/><b>Just for version 1.3.0! Otherwise use SRS!</b>
     */
    public static final String CRS = "crs";
    
    /**
     * Bounding box for map extent. Value is minx,miny,maxx,maxy in units of the SRS. (mandatory)
     */
    public static final String BBOX = "bbox";
    
    /**
     * Width of map output, in pixels. (mandatory)
     */
    public static final String WIDTH = "width";
    
    /**
     * Height of map output, in pixels. (mandatory)
     */
    public static final String HEIGHT = "height";
    
    /**
     * Format for the map output. (mandatory)
     * See WMS output formats for supported values: 
     * https://docs.geoserver.org/stable/en/user/services/wms/outputformats.html#wms-output-formats
     */
    public static final String FORMAT = "format";
    
    /**
     * Whether the map background should be transparent. Values are true or false. Default is false.
     */
    public static final String TRANSPARENT = "transparent";
    
    /**
     * Background color for the map image. Value is in the form RRGGBB. Default is FFFFFF (white).
     */
    public static final String BGCOLOR = "bgcolor";
    
    /**
     * Format in which to report exceptions. Default value is application/vnd.ogc.se_xml.
     */
    public static final String EXCEPTIONS = "exceptions";
    
    /**
     * Time value or range for map data. See Time Support in GeoServer WMS for more information:
     * https://docs.geoserver.org/stable/en/user/services/wms/time.html#wms-time
     */
    public static final String TIME = "time";
    
    /**
     * A URL referencing a StyledLayerDescriptor XML file which controls or enhances map layers and styling:
     * https://docs.geoserver.org/stable/en/user/styling/index.html#styling
     */
    public static final String SLD = "sld";
    
    /**
     * A URL-encoded StyledLayerDescriptor XML document which controls or enhances map layers and styling:
     * https://docs.geoserver.org/stable/en/user/styling/index.html#styling
     */
    public static final String SLD_BODY = "sld_body";
    
  }
  
  
  
  
}
