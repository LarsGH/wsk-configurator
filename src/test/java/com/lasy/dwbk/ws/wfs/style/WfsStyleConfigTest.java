package com.lasy.dwbk.ws.wfs.style;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link WfsStyleConfig}.
 * @author larss
 */
public class WfsStyleConfigTest
{

  private WfsStyleConfig sut;

  /**
   * Setup.
   */
  @Before
  public void setUp()
  {
    sut = new WfsStyleConfig("point", "0,0,0,0", "255,255,255,100");
  }

  /**
   * Tests, that the geometry configuration is detected ignoring case.
   */
  @Test
  public void testGeometryPoint()
  {
    sut.setGeomType("point");
    Assertions.assertThat(sut.getGeomType()).isEqualTo(EWfsStyleGeometry.POINT);

    sut.setGeomType("PoInt");
    Assertions.assertThat(sut.getGeomType()).isEqualTo(EWfsStyleGeometry.POINT);
  }

  /**
   * Tests, that the geometry configuration is detected ignoring case.
   */
  @Test
  public void testGeometryLine()
  {
    sut.setGeomType("line");
    Assertions.assertThat(sut.getGeomType()).isEqualTo(EWfsStyleGeometry.LINE);

    sut.setGeomType("LiNe");
    Assertions.assertThat(sut.getGeomType()).isEqualTo(EWfsStyleGeometry.LINE);
  }

  /**
   * Tests, that the geometry configuration is detected ignoring case.
   */
  @Test
  public void testGeometryPolygon()
  {
    sut.setGeomType("polygon");
    Assertions.assertThat(sut.getGeomType()).isEqualTo(EWfsStyleGeometry.POLYGON);

    sut.setGeomType("PolYgOn");
    Assertions.assertThat(sut.getGeomType()).isEqualTo(EWfsStyleGeometry.POLYGON);
  }
}
