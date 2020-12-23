package com.lasy.dwbk.db.util;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Tests {@link DbGeneratedLayerName}.
 * @author larss
 *
 */
public class DbGeneratedLayerNameTest
{
  /**
   * Tests ID to generated table name and back.
   */
  @Test
  public void testId0()
  {
    assertThatIdIsTranslatedToNameAndBack(0, genertedNameWithSuffix("ze"));
  }
  
  /**
   * Tests ID to generated table name and back.
   */
  @Test
  public void testId1()
  {
    assertThatIdIsTranslatedToNameAndBack(1, genertedNameWithSuffix("on"));
  }
  
  /**
   * Tests ID to generated table name and back.
   */
  @Test
  public void testId2()
  {
    assertThatIdIsTranslatedToNameAndBack(2, genertedNameWithSuffix("tw"));
  }
  
  /**
   * Tests ID to generated table name and back.
   */
  @Test
  public void testId3()
  {
    assertThatIdIsTranslatedToNameAndBack(3, genertedNameWithSuffix("th"));
  }
  
  /**
   * Tests ID to generated table name and back.
   */
  @Test
  public void testId4()
  {
    assertThatIdIsTranslatedToNameAndBack(4, genertedNameWithSuffix("fo"));
  }
  
  /**
   * Tests ID to generated table name and back.
   */
  @Test
  public void testId5()
  {
    assertThatIdIsTranslatedToNameAndBack(5, genertedNameWithSuffix("fi"));
  }
  
  /**
   * Tests ID to generated table name and back.
   */
  @Test
  public void testId6()
  {
    assertThatIdIsTranslatedToNameAndBack(6, genertedNameWithSuffix("si"));
  }
  
  /**
   * Tests ID to generated table name and back.
   */
  @Test
  public void testId7()
  {
    assertThatIdIsTranslatedToNameAndBack(7, genertedNameWithSuffix("se"));
  }
  
  /**
   * Tests ID to generated table name and back.
   */
  @Test
  public void testId8()
  {
    assertThatIdIsTranslatedToNameAndBack(8, genertedNameWithSuffix("ei"));
  }
  
  /**
   * Tests ID to generated table name and back.
   */
  @Test
  public void testId9()
  {
    assertThatIdIsTranslatedToNameAndBack(9, genertedNameWithSuffix("ni"));
  }
  
  /**
   * Tests ID to generated table name and back.
   */
  @Test
  public void testId42()
  {
    assertThatIdIsTranslatedToNameAndBack(1234, genertedNameWithSuffix("ontwthfo"));
  }
  
  private static String genertedNameWithSuffix(String suffix)
  {
    return DbGeneratedLayerName.GENERATED_LAYER_NAME_PREFIX + suffix;
  }
  
  private static void assertThatIdIsTranslatedToNameAndBack(Integer id, String expectedName)
  {
    String generatedTableName = DbGeneratedLayerName.idToGeneratedTableName(id);
    Assertions.assertThat(generatedTableName).isEqualTo(expectedName);
    
    Integer generatedId = DbGeneratedLayerName.generatedTableNameToId(generatedTableName);
    Assertions.assertThat(generatedId).isEqualTo(id);
  }
  
}
