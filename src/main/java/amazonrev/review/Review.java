package amazonrev.review;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Review(
  Long id,
  Integer rating,
  String title,
  String text,
  ReviewImage[] images,
  String asin,
  String parentAsin,
  @JsonProperty("user_id")
  String userID,
  Date timestamp,
  Long helpfulVote,
  Boolean verifiedPurchase
) {}
