package amazonrev.item;

import java.util.HashMap;

public class Item extends ItemSummary {
  String mainCategory;
  String[] features;
  String[] description;
  ItemImage[] images;
  ItemVideo[] videos;
  String[] categories;
  HashMap<String, Object> details;
  ItemAuthor author;

  public Item() {
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
      ItemImage[] images,
      ItemVideo[] videos,
      String store,
      String[] categories,
      HashMap<String, Object> details,
      String parentAsin,
      ItemAuthor author) {
    super(id, title, subtitle, averageRating, ratingNumber, price, store, parentAsin);
    this.mainCategory = mainCategory;
    this.features = features;
    this.description = description;
    this.images = images;
    this.videos = videos;
    this.categories = categories;
    this.details = details;
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
      String parentAsin) {
    super(id, title, subtitle, averageRating, ratingNumber, price, store, parentAsin);
    this.mainCategory = mainCategory;
    this.features = features;
    this.description = description;
    this.categories = categories;
    this.details = details;
  }

  public String getMainCategory() {
    return mainCategory;
  }

  public String[] getFeatures() {
    return features;
  }

  public String[] getDescription() {
    return description;
  }

  public ItemImage[] getImages() {
    return images;
  }

  public ItemVideo[] getVideos() {
    return videos;
  }

  public String[] getCategories() {
    return categories;
  }

  public HashMap<String, Object> getDetails() {
    return details;
  }

  public ItemAuthor getAuthor() {
    return author;
  }

  public void setMainCategory(String mainCategory) {
    this.mainCategory = mainCategory;
  }

  public void setFeatures(String[] features) {
    this.features = features;
  }

  public void setDescription(String[] description) {
    this.description = description;
  }

  public void setImages(ItemImage[] images) {
    this.images = images;
  }

  public void setVideos(ItemVideo[] videos) {
    this.videos = videos;
  }

  public void setCategories(String[] categories) {
    this.categories = categories;
  }

  public void getDetails(HashMap<String, Object> details) {
    this.details = details;
  }

  public void setAuthor(ItemAuthor author) {
    this.author = author;
  }
}
