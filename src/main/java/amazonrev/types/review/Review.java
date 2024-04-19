package amazonrev.types.review;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Review(
  long id,
  int rating,
  String title,
  String text,
  Image[] images,
  String asin,
  String parentAsin,
  @JsonProperty("user_id")
  String userID,
  long timestamp,
  int helpfulVote,
  boolean verifiedPurchase
) {}
