package com.lasy.dwbk.db.util;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.lasy.dwbk.util.Check;

/**
 * Creates a name for generated layers (stored locally) using the layer ID.
 * Also maps the generated name back to the original layer ID.
 * <p>
 * Each ID figure is mapped to the first two characters of the according number string.
 * @author larss
 *
 */
public class DbGeneratedLayerName
{
  /**
   * Mapping from digit to generated grouping (used for generated table).
   */
  private static final Map<String, String> ID_MAPPING = Map.of(
    "0", "ze", // zero
    "1", "on", // one
    "2", "tw", // two
    "3", "th", // three
    "4", "fo", // four
    "5", "fi", // five
    "6", "si", // six
    "7", "se", // seven
    "8", "ei", // eight
    "9", "ni"); // nine
  
  /**
   * Basic prefix of the generated table.
   */
  protected static final String GENERATED_LAYER_NAME_PREFIX = "dwbk_genlayer_";
  
  /**
   * Returns the name for the generated table for the provided layer ID.
   * @param layerId layer ID
   * @return name for the generated table
   */
  public static String idToGeneratedTableName(Integer layerId)
  {
    String id = Check.notNull(layerId, "layerId").toString();
    
    return createGroupedCharacterStream(id, 1)
      .map(ID_MAPPING::get)
      .collect(Collectors.joining("", GENERATED_LAYER_NAME_PREFIX, ""));
  }
  
  /**
   * Returns the layer ID for the generated table name.
   * @param generatedTableName generated table name
   * @return layer ID
   */
  public static Integer generatedTableNameToId(String generatedTableName)
  {
    Check.trimmedNotEmpty(generatedTableName, "generatedTableName");
    
    String generatedNamepart = generatedTableName.replace(GENERATED_LAYER_NAME_PREFIX, "");
    String id = createGroupedCharacterStream(generatedNamepart, 2)
      .map(DbGeneratedLayerName::findDigitForGeneratedGroup)
      .collect(Collectors.joining());
    
    return Integer.valueOf(id);
  }
  
  private static String findDigitForGeneratedGroup(String generatedGroup)
  {    
    return ID_MAPPING.entrySet().stream()
      .filter(entry -> entry.getValue().equals(generatedGroup))
      .findFirst()
      .map(Map.Entry::getKey)
      .orElseThrow();
  }
  
  private static Stream<String> createGroupedCharacterStream(String source, int groupSize)
  {
    /* 
     * The regex (?<=\G...) matches an empty string 
     * that has the last match (\G) 
     * followed by three characters (...) before it ((?<= )) 
     */
    String regex = String.format("(?<=\\G%s)", ".".repeat(groupSize));
    String[] grouped = source.split(regex);
    return Stream.of(grouped);
  }
}
