package com.lasy.wsk.util;

import org.assertj.core.api.Assertions;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.lasy.wsk.app.model.impl.BboxModel;
import com.lasy.wsk.util.BboxUtil;
import com.lasy.wsk.ws.wms.TileMatrixParams;

/**
 * Tests {@link BboxUtil}.
 * @author larss
 *
 */
public class BboxUtilTest
{
  
  /**
   * Tests, that the EPSG code can be read from the CRS.
   */
  @Test
  public void testGetEpsgCodeFromCrs()
  {
    // EPSG 3857
    CoordinateReferenceSystem crs = BboxUtil.getCrsForEpsgCode(BboxUtil.EPSG_3857);
    int epsgCode = BboxUtil.getEpsgCodeFromCrs(crs);
    Assertions.assertThat(epsgCode).isEqualTo(BboxUtil.EPSG_3857);
    
    // EPSG 4326
    crs = BboxUtil.getCrsForEpsgCode(BboxUtil.EPSG_4326);
    epsgCode = BboxUtil.getEpsgCodeFromCrs(crs);
    Assertions.assertThat(epsgCode).isEqualTo(BboxUtil.EPSG_4326);
  }
  
  /**
   * Tests, that the boundingbox envelopes are created correctly with and without transformation.
   */
  @Test
  public void testEnvelopesForBbox()
  {
    BboxModel bbox = createTestBbox();
    ReferencedEnvelope envelope4326 = BboxUtil.getEnvelopeForBbox(bbox);
    int epsg = BboxUtil.getEpsgCodeFromCrs(envelope4326.getCoordinateReferenceSystem());
    Assertions.assertThat(epsg).isEqualTo(BboxUtil.EPSG_4326);
    
    ReferencedEnvelope envelope3857 = BboxUtil.getEnvelopeForBboxInCrs(bbox, BboxUtil.getCrsForEpsgCode(BboxUtil.EPSG_3857));
    epsg = BboxUtil.getEpsgCodeFromCrs(envelope3857.getCoordinateReferenceSystem());
    Assertions.assertThat(epsg).isEqualTo(BboxUtil.EPSG_3857);
    
    // overlapping envelopes have a distance of 0!
    Assertions.assertThat(envelope4326.covers(envelope3857));
    Assertions.assertThat(envelope3857.covers(envelope4326));
  }
  
  /**
   * Tests, that the bbox envelope will be resized.
   */
  @Test
  public void testGetFittingBboxEnvelope()
  {
    BboxModel bbox = createTestBbox();
    CoordinateReferenceSystem crs3857 = BboxUtil.getCrsForEpsgCode(BboxUtil.EPSG_3857);
    ReferencedEnvelope envelope3857 = BboxUtil.getEnvelopeForBboxInCrs(bbox, crs3857);
    
    ReferencedEnvelope fittingEnvelope3857 = BboxUtil.getFittingBboxEnvelopeInEpsg3857(bbox, TileMatrixParams.DEFAULT_TILE_LENGTH);
    int epsg = BboxUtil.getEpsgCodeFromCrs(fittingEnvelope3857.getCoordinateReferenceSystem());
    Assertions.assertThat(epsg).isEqualTo(BboxUtil.EPSG_3857);
    
    Assertions.assertThat(fittingEnvelope3857.getWidth())
      .isGreaterThan(envelope3857.getWidth())
      .isLessThan(envelope3857.getWidth() + TileMatrixParams.DEFAULT_TILE_LENGTH);
    
    Assertions.assertThat(fittingEnvelope3857.getHeight())
      .isGreaterThan(envelope3857.getHeight())
      .isLessThan(envelope3857.getHeight() + TileMatrixParams.DEFAULT_TILE_LENGTH);
  }
  
  /**
   * Tests, that the envelope is created without decimals.
   */
  @Test
  public void testGetEnvelopeWithoutDecimals()
  {
    double minX = 1.123456789;
    double maxX = 11.9874321;
    double minY = 9.987321321654;
    double maxY = 99.98721332135;
    
    ReferencedEnvelope envelope = new ReferencedEnvelope(
      minX, 
      maxX, 
      minY, 
      maxY, 
      BboxUtil.getCrsForEpsgCode(BboxUtil.EPSG_3857));
    
    ReferencedEnvelope envelopeWithoutDecimals = BboxUtil.getEnvelopeWithoutDecimals(envelope);
    Assertions.assertThat(envelopeWithoutDecimals.getMinX()).isEqualTo(1);
    Assertions.assertThat(envelopeWithoutDecimals.getMaxX()).isEqualTo(12);
    Assertions.assertThat(envelopeWithoutDecimals.getMinY()).isEqualTo(9);
    Assertions.assertThat(envelopeWithoutDecimals.getMaxY()).isEqualTo(100);
  }
  
  @Test
  public void testGetEpsgStringForCode()
  {
    String epsgStr = BboxUtil.getEpsgStringForCode(BboxUtil.EPSG_3857);
    
    Assertions.assertThat(epsgStr).isEqualTo("EPSG:3857");
  }

  /**
   * Creates a test bbox in EPSG:4326.
   * @return bbox
   */
  private BboxModel createTestBbox()
  {
    BboxModel bbox = BboxModel.builder("test")
      .withEpsg(BboxUtil.EPSG_4326)
      .withMinLon("6.922229")
      .withMinLat("51.673266")
      .withMaxLon("6.947550")
      .withMaxLat("51.677132")
      .build();
    return bbox;
  }
  
}
