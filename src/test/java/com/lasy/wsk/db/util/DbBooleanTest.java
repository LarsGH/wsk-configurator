package com.lasy.wsk.db.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.lasy.wsk.db.util.DbBoolean;

/**
 * Tests {@link DbBoolean}.
 * 
 * @author larss
 */
public class DbBooleanTest
{

  /**
   * Tests {@code true} -> "Y".
   */
  @Test
  public void testToDbTrue()
  {
    String dbValue = DbBoolean.toDbValue(true);
    Assertions.assertThat(dbValue).isEqualTo(DbBoolean.YES); 
  }
  
  /**
   * Tests {@code false} -> "N".
   */
  @Test
  public void testToDbFalse()
  {
    String dbValue = DbBoolean.toDbValue(false);
    Assertions.assertThat(dbValue).isEqualTo(DbBoolean.NO);
  }
  
  /**
   * Tests "Y" -> {@code true}.
   */
  @Test
  public void testFromDbTrue()
  {
    boolean dbValue = DbBoolean.fromDbValue(DbBoolean.YES);
    Assertions.assertThat(dbValue).isTrue();
  }
  
  /**
   * Tests "N" -> {@code false}.
   */
  @Test
  public void testFromDbFalse()
  {
    boolean dbValue = DbBoolean.fromDbValue(DbBoolean.NO);
    Assertions.assertThat(dbValue).isFalse();
  }
  
  /**
   * Tests <b> anything but "Y" </b> -> {@code false}.
   */
  @Test
  public void testFromDbFalseByAnythingElse()
  {
    boolean dbValue = DbBoolean.fromDbValue(null);
    Assertions.assertThat(dbValue).isFalse();
    
    dbValue = DbBoolean.fromDbValue("doesNotMatterBecauseIsNotY");
    Assertions.assertThat(dbValue).isFalse();
  }
  
}
