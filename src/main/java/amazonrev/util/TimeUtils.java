package amazonrev.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * For working with datetimes.
 * 
 * Typing:
 * - Postgres: TIMESTAMPTZ
 * - Java: OffsetDateTime (or Instant)
 */
public class TimeUtils {

  // ref: https://www.postgresql.org/docs/current/datatype-datetime.html#DATATYPE-DATETIME
  // 4713 BC is Postgres min
  // 294276 AD is Postgres max
  public final static Instant POSTGRES_MIN_INSTANT = LocalDateTime.of(-4713, 1, 1, 0, 0).toInstant(ZoneOffset.UTC);
  public final static Instant POSTGRES_MAX_INSTANT = LocalDateTime.of(294276, 1, 1, 0, 0).toInstant(ZoneOffset.UTC);
  public final static OffsetDateTime POSTGRES_MIN_TIME = POSTGRES_MIN_INSTANT.atOffset(ZoneOffset.UTC);
  public final static OffsetDateTime POSTGRES_MAX_TIME = POSTGRES_MAX_INSTANT.atOffset(ZoneOffset.UTC);

  public static OffsetDateTime toOffsetDateTime(Integer year) {
    if (year == null) {
      return null;
    }
    return OffsetDateTime.of(year, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
  }
}
