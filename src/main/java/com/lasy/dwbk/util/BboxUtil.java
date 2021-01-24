package com.lasy.dwbk.util;

import java.util.Objects;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.lasy.dwbk.app.error.DwbkFrameworkException;
import com.lasy.dwbk.app.model.impl.BboxModel;

/**
 * Boundingbox helper.
 * @author larss
 *
 */
public class BboxUtil
{
  /** EPSG code 3857. (Default for local storage).  */
  public static final int EPSG_3857 = 3857; 
  
  /** EPSG code 4326 (WGS84). Standard geographical CRS. */
  public static final int EPSG_4326 = 4326; 

  /**
   * Returns the envelope for the boundingbox.
   * @param bbox the boundingbox
   * @param targetCrs the target CRS
   * @param defaultTileLength the default tile length in pixels
   * @return envelope
   */
  public static ReferencedEnvelope getFittingBboxEnvelopeInEpsg3857(
    BboxModel bbox,
    int defaultTileLength)
  {
    Check.notNull(bbox, "bbox");
    
    CoordinateReferenceSystem crs3857 = getCrsForEpsgCode(EPSG_3857);
    ReferencedEnvelope envelope = getEnvelopeForBboxInCrs(bbox, crs3857);
    
    // ignore decimal places in coordinates (metric system)
    envelope = getEnvelopeWithoutDecimals(envelope);
    
    int originalWidth = (int) envelope.getWidth();
    int originalHeight = (int) envelope.getHeight();
    
    double deltaX = getDelta(originalWidth, defaultTileLength);
    double deltaY = getDelta(originalHeight, defaultTileLength);
    envelope.expandBy(deltaX, deltaY);
    
    return envelope;
  }
  
  private static double getDelta(double originalLength, int defaultTileLength)
  {
    double rest = (originalLength % defaultTileLength);
    return (rest == 0)
      ? 0
      : (defaultTileLength - rest) / 2;
  }
  
  /**
   * Creates a new envelope without decimals. (Rounding: Minimum coordinates -> floor | Maximum coordinates -> ceiling)
   * @param envelope the input envelope
   * @return new envelope without decimals
   */
  public static ReferencedEnvelope getEnvelopeWithoutDecimals(ReferencedEnvelope envelope)
  {
    return new ReferencedEnvelope(
      Math.floor(envelope.getMinX()), 
      Math.ceil(envelope.getMaxX()), 
      Math.floor(envelope.getMinY()), 
      Math.ceil(envelope.getMaxY()),
      envelope.getCoordinateReferenceSystem());
  }
  
  /**
   * Returns the boundingbox envelope in the target CRS.
   * @param bbox the boundingbox
   * @param targetCrs the target CRS
   * @return boundingbox envelope
   */
  public static ReferencedEnvelope getEnvelopeForBboxInCrs(BboxModel bbox, CoordinateReferenceSystem targetCrs)
  {
    ReferencedEnvelope bboxEnvelope = getEnvelopeForBbox(bbox);
    
    int targetEpsgCode = getEpsgCodeFromCrs(targetCrs);
    if(!Objects.equals(bbox.getEpsg(), targetEpsgCode))
    {
      // transform bbox envelope
      try
      {
        bboxEnvelope = bboxEnvelope.transform(targetCrs, true);
      }
      catch (Exception e)
      {
        String msg = String.format("Cannot transform bbox from %s to %s", bbox.getEpsg(), targetEpsgCode);
        throw new IllegalStateException(msg);
      }
    }
    return bboxEnvelope;
  }
  
  /**
   * Retruns the EPSG code for the CRS.
   * @param crs the coordinate reference system
   * @return EPSG code
   */
  public static int getEpsgCodeFromCrs(CoordinateReferenceSystem crs)
  {
    Check.notNull(crs, "crs");
    
    try
    {
      String code = CRS.toSRS(crs, true);
      return Integer.valueOf(code);
    }
    catch (Exception e)
    {
      String msg = String.format("Cannot read EPSG code from CRS '%s'", crs.getName());
      throw new IllegalArgumentException(msg, e);
    }
  }
  
  /**
   * Returns the GT ReferencedEnvelope for the boundingbox.
   * @param bbox the boundingbox
   * @return referenced envelope
   */
  public static ReferencedEnvelope getEnvelopeForBbox(BboxModel bbox)
  {
    CoordinateReferenceSystem bboxCrs = getCrsForEpsgCode(bbox.getEpsg());
    ReferencedEnvelope envelope = new ReferencedEnvelope(
      Double.valueOf(bbox.getMinLon()), 
      Double.valueOf(bbox.getMaxLon()), 
      Double.valueOf(bbox.getMinLat()), 
      Double.valueOf(bbox.getMaxLat()), 
      bboxCrs);
    return envelope;
  }
  
  /**
   * Returns the CRS for the provided EPSG code.
   * @param code the EPSG code
   * @return CRS
   */
  public static CoordinateReferenceSystem getCrsForEpsgCode(int code)
  {
    try
    {
      return CRS.decode("EPSG:" + code);
    }
    catch (Exception e)
    {
      throw DwbkFrameworkException.failForReason(e, "Koordinatensystem für EPSG:%s konnte nicht erstellt werden!", code);
    }
  }
  
  /**
   * Returns the EPSG string. Example: 'EPSG:3857'.
   * @param code the EPSG code
   * @return EPSG string
   */
  public static String getEpsgStringForCode(int code)
  {
    return String.format("EPSG:%s", code);
  }
  
}
