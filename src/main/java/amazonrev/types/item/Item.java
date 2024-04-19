package amazonrev.types.item;

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
  Image[] images,
  Video[] videos,
  String store,
  String[] categories,
  HashMap<String, ?> details,
  String parentAsin,
  Object boughtTogether, // Always null in dataset?
  Author author
) {}
