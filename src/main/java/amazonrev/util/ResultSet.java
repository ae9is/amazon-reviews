package amazonrev.util;

import java.sql.Array;
import java.sql.SQLException;

/**
 * A wrapper for java.sql.ResultSet that allows returning null values from
 * getLong, getDouble, etc.
 */
public class ResultSet {
  public java.sql.ResultSet rs;

  public ResultSet(java.sql.ResultSet rs) {
    this.rs = rs;
  }

  // Methods check for null after the fact and overwrite zeros.
  // Could also rs.getString(column) and convert to type manually.

  public Long getLong(String column) throws SQLException {
    Long value = rs.getLong(column);
    if (rs.wasNull()) {
      value = null;
    }
    return value;
  }

  public Double getDouble(String column) throws SQLException {
    Double value = rs.getDouble(column);
    if (rs.wasNull()) {
      value = null;
    }
    return value;
  }

  public String getString(String column) throws SQLException {
    return rs.getString(column);
  }

  public Array getArray(String column) throws SQLException {
    return rs.getArray(column);
  }
}