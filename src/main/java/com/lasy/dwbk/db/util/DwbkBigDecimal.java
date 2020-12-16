package com.lasy.dwbk.db.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.assertj.core.data.Offset;

/**
 * Create and access {@link BigDecimal}.
 * @author larss
 *
 */
public class DwbkBigDecimal
{

  public static BigDecimal create(String num)
  {
    BigDecimal n = new BigDecimal(num);
    return n.setScale(6, RoundingMode.CEILING);
  }
  
  public static final Offset<BigDecimal> TEST_OFFSET = Offset.offset(new BigDecimal("0.000002"));
}
