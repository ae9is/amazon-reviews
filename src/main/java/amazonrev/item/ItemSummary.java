package amazonrev.item;

public class ItemSummary {
  Long id;
  String title;
  String subtitle;
  Double averageRating;
  Long ratingNumber;
  Double price;
  String store;
  String parentAsin;

  public ItemSummary() {}

  public ItemSummary(
    Long id,
    String title,
    String subtitle,
    Double averageRating,
    Long ratingNumber,
    Double price,
    String store,
    String parentAsin
  ) {
    this.id = id;
    this.title = title;
    this.subtitle = subtitle;
    this.averageRating = averageRating;
    this.ratingNumber = ratingNumber;
    this.price = price;
    this.store = store;
    this.parentAsin = parentAsin;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public Double getAverageRating() {
    return averageRating;
  }

  public Long getRatingNumber() {
    return ratingNumber;
  }

  public Double getPrice() {
    return price;
  }

  public String getStore() {
    return store;
  }

  public String getParentAsin() {
    return parentAsin;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }

  public void setAverageRating(Double averageRating) {
    this.averageRating = averageRating;
  }

  public void setRatingNumber(Long ratingNumber) {
    this.ratingNumber = ratingNumber;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public void setStore(String store) {
    this.store = store;
  }

  public void setParentAsin(String parentAsin) {
    this.parentAsin = parentAsin;
  }
}
