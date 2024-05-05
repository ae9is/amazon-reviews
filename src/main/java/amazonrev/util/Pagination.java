package amazonrev.util;

import java.time.OffsetDateTime;

import amazonrev.util.exception.BadRequestException;

/**
 * Logic for cursor-based pagination.
 */
public class Pagination {
  final static long DEFAULT_ID_CURSOR = -1;
  final static int MIN_LIMIT = 1;
  final static int DEFAULT_LIMIT = 10;
  final static int MAX_LIMIT = 50;

  String cursor; // An encoded string containing one or more cursor values.
  Integer limit = DEFAULT_LIMIT;

  public enum CursorDirection {
    ASC,
    DESC,
  }

  public Pagination() {}

  public Pagination(String cursor, Integer limit) {
    setCursor(cursor);
    setLimit(limit);
  }

  public Long getIdCursor() {
    return (Long) getNthCursorAs(Long.valueOf(0), 0, CursorDirection.ASC);
  }

  /**
   * Retrieve n-th cursor as type from encoded cursor array string.
   * One encoded string is used for all cursors to simplify things for the client.
   * 
   * @param <T>  Supported types: Long, Double, Integer, OffsetDateTime, String
   * @param type Arbitrary instance of target type of cursor to return.
   * @param n    Which cursor to return. First cursor is n = 0.
   * @param dir  Cursor direction. Used to set initial cursor value.
   * @return Decoded cursor of target type as Object.
   */
  protected <T> Object getNthCursorAs(T type, int n, CursorDirection dir) {
    Object minValue;
    Object maxValue;
    if (type instanceof Long) {
      minValue = Long.MIN_VALUE;
      maxValue = Long.MAX_VALUE;
    } else if (type instanceof Double) {
      minValue = Double.MIN_VALUE;
      maxValue = Double.MAX_VALUE;
    } else if (type instanceof Integer) {
      minValue = Integer.MIN_VALUE;
      maxValue = Integer.MAX_VALUE;
    } else if (type instanceof OffsetDateTime) {
      minValue = TimeUtils.POSTGRES_MIN_TIME;
      maxValue = TimeUtils.POSTGRES_MAX_TIME;
    } else if (type instanceof String) {
      minValue = "";
      maxValue = "";
    } else {
      throw new BadRequestException("Not implemented for " + type.getClass().getName());
    }
    Object defaultValue = dir.equals(CursorDirection.ASC) ? minValue : maxValue;
    Object typedCursor = defaultValue;
    if (cursor != null) {
      String[] decoded = Encode.decodeCursorArray(cursor);
      String decodedCursor = decoded.length > n ? decoded[n] : null;
      try {
        if (decodedCursor != null) {
          if (type instanceof Long) {
            typedCursor = Long.parseLong(decodedCursor);
          } else if (type instanceof Double) {
            typedCursor = Double.parseDouble(decodedCursor);
          } else if (type instanceof Integer) {
            typedCursor = Integer.parseInt(decodedCursor);
          } else if (type instanceof OffsetDateTime) {
            typedCursor = OffsetDateTime.parse(decodedCursor);
          } else if (type instanceof String) {
            typedCursor = decodedCursor;
          }
        }
      } catch (Exception e) {
        typedCursor = defaultValue;
      }
    }
    return typedCursor;
  }

  public void setLimit(Integer limit) {
    if (limit == null) {
      this.limit = DEFAULT_LIMIT;
    } else if (limit > MAX_LIMIT) {
      this.limit = MAX_LIMIT;
    } else if (limit < MIN_LIMIT) {
      this.limit = MIN_LIMIT;
    } else {
      this.limit = limit;
    }
  }

  public int getLimit() {
    return limit;
  }

  public void setCursor(String cursor) {
    this.cursor = cursor;
  }

  public String getCursor() {
    return cursor;
  }
}
