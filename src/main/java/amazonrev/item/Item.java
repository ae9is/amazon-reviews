package amazonrev.item;

import java.util.HashMap;

public record Item(
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
}
