package amazonrev.recommend;

import amazonrev.item.ItemSummary;

public class RecommendItem extends ItemSummary {
  Double distance; // Vector distance (similarity score) to a search query

  public RecommendItem() {}

  public RecommendItem(
      Long id,
      String title,
      String subtitle,
      Double averageRating,
      Long ratingNumber,
      Double price,
      String store,
      String parentAsin,
      Double distance) {
    super(id, title, subtitle, averageRating, ratingNumber, price, store, parentAsin);
    this.distance = distance;
  }

  public Double getDistance() {
    return distance;
  }

  public void setDistance(Double distance) {
    this.distance = distance;
  }
}
