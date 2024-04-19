package amazonrev.parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * Parses raw data from json with lines to Java objects.
 * Then writes out to required database import files.
 */
public abstract class FileParser {
  final static int MAX_FAILED_LINES = 5;
  final static String separator = ""; // "\u001e", record separator, used here as postgres delimiter
  final static String quote = ""; // "\u001f", unit separator, used here as postgres quote character
  final static String nullchar = "\u0000"; // null character, can't be used in postgres fields

  // abstract
  static void parse(String dataFilename, String outputFolder) throws IOException {
    throw new UnsupportedOperationException("Unimplemented method 'parse'");
  }

  static void writeLine(BufferedWriter writer, String[] values) throws IOException {
    List<String> sanitised = new ArrayList<String>();
    for (String val: values) {
      if (val != null) {
        sanitised.add(val.replace(separator, "").replace("\n", "").replace(nullchar, ""));
      } else {
        sanitised.add("");
      }
    }
    String line = String.join(separator, sanitised) + "\n";
    writer.write(line);
  }

  static <T> String asString(T input) {
    if (input == null) {
      return "";
    }
    return String.valueOf(input);
  }

  /**
   * Escape double-quotes in values and wrap in Postgres array syntax.
   * 
   * @throws JsonProcessingException 
   */
  static String toPostgresArray(String[] values, JsonMapper mapper) throws JsonProcessingException {
    List<String> escaped = new ArrayList<String>();
    for (String val: values) {
      if (val != null) {
        escaped.add(mapper.writeValueAsString(val));
      } else {
        escaped.add("''");
      }
    }
    return quote + "{" + String.join(",", escaped) + "}" + quote;
  }

  static String toJson(HashMap<String, ?> input, JsonMapper mapper) throws JsonProcessingException {
    return mapper.writeValueAsString(input);
  }

  static void addKeyToIndexMap(Map<String, Integer> indexMap, String key) {
    Integer index;
    if (indexMap.containsKey(key)) {
      index = indexMap.get(key);
    } else {
      index = indexMap.size();
      indexMap.put(key, index);
    }
  }

  static Double getDouble(String input) {
    Double value;
    try {
      value = Double.parseDouble(input);
    } catch (Exception e) {
      value = null;
    }
    return value;
  }
}
