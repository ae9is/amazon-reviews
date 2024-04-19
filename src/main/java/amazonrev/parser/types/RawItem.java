package amazonrev.parser.types;

import java.util.HashMap;

import amazonrev.types.item.Author;
import amazonrev.types.item.Image;
import amazonrev.types.item.Video;

// Note: Can't extend records. Modified from amazonrev.types.item.Item.
public record RawItem(
  Long id,
  String mainCategory,
  String title,
  String subtitle,
  Double averageRating,
  Long ratingNumber,
  String[] features,
  String[] description,
  String price,
  Image[] images,
  Video[] videos,
  String store,
  String[] categories,
  HashMap<String, ?> details,
  String parentAsin,
  Object boughtTogether, // Always null in dataset?
  Author author
) {}