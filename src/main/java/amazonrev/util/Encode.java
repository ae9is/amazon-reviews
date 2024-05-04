package amazonrev.util;

import java.util.Base64;

public class Encode {
  final static String delim = "";

  /**
   * Decode provided string, split by delimiter, and return string array.
   */
  public static String[] decodeCursorArray(String encoded) {
    if (encoded == null || encoded.equals("")) {
      return new String[] {};
    }
    String decoded = new String(Base64.getUrlDecoder().decode(encoded));
    String[] cursors = decoded.split(delim);
    return cursors;
  }

  /**
   * Join string arguments with delimiter and return encoded single string.
   */
  public static String encodeCursorArray(String... cursors) {
    StringBuilder sb = new StringBuilder();
    for (String cursor : cursors) {
      if (cursor != null) {
        sb = sb.append(cursor);
      }
      // Null or empty cursors and their ordering are still preserved in output
      sb = sb.append(delim);
    }
    String s = sb.toString();
    if (s == null) {
      s = "";
    }
    int end = 0;
    if (s.length() > 0) {
      // Remove trailing delimiter
      end = s.length() - 1;
    }
    s = s.substring(0, end);
    String encoded = Base64.getUrlEncoder().encodeToString(s.getBytes());
    return encoded;
  }
}
