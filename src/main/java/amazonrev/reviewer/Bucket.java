package amazonrev.reviewer;

/**
 * Bucket in a bucket distribution.
 * 
 * @param tile   The bucket ranking (position) within the distribution. I.e. if
 *               there are 100 buckets, the tile is the percentile. For 10, the
 *               decile. For 4, the quartile.
 * @param maxval The maximum value of the values binned into this bucket.
 */
public record Bucket(
    Integer tile,
    Number maxval) {
}
