package amazonrev.item;

import java.util.HashMap;

public class Item {
  Long id;
  String mainCategory;
  String title;
  String subtitle;
  Double averageRating;
  Long ratingNumber;
  String[] features;
  String[] description;
  Double price;
  ItemImage[] images;
  ItemVideo[] videos;
  String store;
  String[] categories;
  HashMap<String, Object> details;
  String parentAsin;
  ItemAuthor author;

  public Item() {}

  public Item(
    Long id,
    String mainCategory,
    String title,
    String subtitle,
    Double averageRating,
    Long ratingNumber,
    String[] features,
    String[] description,
    Double price,
    ItemImage[] images,
    ItemVideo[] videos,
    String store,
    String[] categories,
    HashMap<String, Object> details,
    String parentAsin,
    ItemAuthor author
  ) {
    this.id = id;
    this.mainCategory = mainCategory;
    this.title = title;
    this.subtitle = subtitle;
    this.averageRating = averageRating;
    this.ratingNumber = ratingNumber;
    this.features = features;
    this.description = description;
    this.price = price;
    this.images = images;
    this.videos = videos;
    this.store = store;
    this.categories = categories;
    this.details = details;
    this.parentAsin = parentAsin;
    this.author = author;
  }

  public Item(
    Long id,
    String mainCategory,
    String title,
    String subtitle,
    Double averageRating,
    Long ratingNumber,
    String[] features,
    String[] description,
    Double price,
    String store,
    String[] categories,
    HashMap<String, Object> details,
    String parentAsin
  ) {
    this.id = id;
    this.mainCategory = mainCategory;
    this.title = title;
    this.subtitle = subtitle;
    this.averageRating = averageRating;
    this.ratingNumber = ratingNumber;
    this.features = features;
    this.description = description;
    this.price = price;
    this.store = store;
    this.categories = categories;
    this.details = details;
    this.parentAsin = parentAsin;
  }

  public Long getId() {
    return id;
  }
  public String getMainCategory() {
    return mainCategory;
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
  public String[] getFeatures() {
    return features;
  }
  public String[] getDescription() {
    return description;
  }
  public Double getPrice() {
    return price;
  }
  public ItemImage[] getImages() {
    return images;
  }
  public ItemVideo[] getVideos() {
    return videos;
  }
  public String getStore() {
    return store;
  }
  public String[] getCategories() {
    return categories;
  }
  public HashMap<String, Object> getDetails() {
    return details;
  }
  public String getParentAsin() {
    return parentAsin;
  }
  public ItemAuthor getAuthor() {
    return author;
  }

  public void setId(Long id) {
    this.id = id;
  }
  public void setMainCategory(String mainCategory) {
    this.mainCategory = mainCategory;
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
  public void setFeatures(String[] features) {
    this.features = features;
  }
  public void setDescription(String[] description) {
    this.description = description;
  }
  public void setPrice(Double price) {
    this.price = price;
  }
  public void setImages(ItemImage[] images) {
    this.images = images;
  }
  public void setVideos(ItemVideo[] videos) {
    this.videos = videos;
  }
  public void setStore(String store) {
    this.store = store;
  }
  public void setCategories(String[] categories) {
    this.categories = categories;
  }
  public void getDetails(HashMap<String, Object> details) {
    this.details = details;
  }
  public void setParentAsin(String parentAsin) {
    this.parentAsin = parentAsin;
  }
  public void setAuthor(ItemAuthor author) {
    this.author = author;
  }
}
