package amazonrev.review;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Review {
  Long id;
  Integer rating;
  String title;
  String text;
  String asin;
  String parentAsin;
  @JsonProperty("user_id")
  String userID;
  // Timestamp is java.time.OffsetDateTime (vs Instant) for compatibility with GraphQL extended scalar DateTime.
  // ref: https://github.com/graphql-java/graphql-java-extended-scalars/?tab=readme-ov-file#date--time-scalars
  OffsetDateTime timestamp;
  Long helpfulVote;
  Boolean verifiedPurchase;
  ReviewImage[] images;

  public Review() {}

  public Review(
    Long id,
    Integer rating,
    String title,
    String text,
    String asin,
    String parentAsin,
    String userID,
    OffsetDateTime timestamp,
    Long helpfulVote,
    Boolean verifiedPurchase,
    ReviewImage[] images
  ) {
    this.id = id;
    this.rating = rating;
    this.title = title;
    this.text = text;
    this.asin = asin;
    this.parentAsin = parentAsin;
    this.userID = userID;
    this.timestamp = timestamp;
    this.helpfulVote = helpfulVote;
    this.verifiedPurchase = verifiedPurchase;
    this.images = images;
  }

  public Review(
    Long id,
    Integer rating,
    String title,
    String text,
    String asin,
    String parentAsin,
    String userID,
    OffsetDateTime timestamp,
    Long helpfulVote,
    Boolean verifiedPurchase
  ) {
    this.id = id;
    this.rating = rating;
    this.title = title;
    this.text = text;
    this.asin = asin;
    this.parentAsin = parentAsin;
    this.userID = userID;
    this.timestamp = timestamp;
    this.helpfulVote = helpfulVote;
    this.verifiedPurchase = verifiedPurchase;
  }

  public Long getId() {
    return this.id;
  }
  public Integer getRating() {
    return this.rating;
  }
  public String getTitle() {
    return this.title;
  }
  public String getText() {
    return this.text;
  }
  public String getAsin() {
    return this.asin;
  }
  public String getParentAsin() {
    return this.parentAsin;
  }
  public String getUserID() {
    return this.userID;
  }
  public OffsetDateTime getTimestamp() {
    return this.timestamp;
  }
  public Long getHelpfulVote() {
    return this.helpfulVote;
  }
  public Boolean getVerifiedPurchase() {
    return this.verifiedPurchase;
  }
  public ReviewImage[] getImages() {
    return this.images;
  }

  public void setId(Long id) {
    this.id = id;
  }
  public void setRating(Integer rating) {
    this.rating = rating;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public void setText(String text) {
    this.text = text;
  }
  public void setAsin(String asin) {
    this.asin = asin;
  }
  public void setParentAsin(String parentAsin) {
    this.parentAsin = parentAsin;
  }
  public void setUserID(String userID) {
    this.userID = userID;
  }
  public void setTimestamp(OffsetDateTime timestamp) {
    this.timestamp = timestamp;
  }
  public void setHelpfulVote(Long helpfulVote) {
    this.helpfulVote = helpfulVote;
  }
  public void setVerifiedPurchase(Boolean verifiedPurchase) {
    this.verifiedPurchase = verifiedPurchase;
  }
  public void setImages(ReviewImage[] images) {
    this.images = images;
  }
}
