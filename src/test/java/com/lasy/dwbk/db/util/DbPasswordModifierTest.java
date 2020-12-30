package com.lasy.dwbk.db.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Tests {@link DbPasswordModifier}.
 * @author larss
 *
 */
public class DbPasswordModifierTest
{
  
  /**
   * Tests the Modifier.
   */
  @Test
  public void testEncryptDecrypt()
  {
    String pw = "Th1sIs4T3st!4212";
    
    String encrypted = DbPasswordModifier.toDbValue(pw);
    Assertions.assertThat(encrypted).isNotEqualTo(pw);
    
    String decrypted = DbPasswordModifier.fromDbValue(encrypted);
    Assertions.assertThat(decrypted).isEqualTo(pw);
  }
  
  /**
   * Tests the Modifier.
   */
  @Test
  public void testToDbValueWithNull()
  {
    String encrypted = DbPasswordModifier.toDbValue(null);
    Assertions.assertThat(encrypted).isNull();
  }
  
  /**
   * Tests the Modifier.
   */
  @Test
  public void testFromDbValueWithNull()
  {
    String decrypted = DbPasswordModifier.fromDbValue(null);
    Assertions.assertThat(decrypted).isNull();
  }
}
