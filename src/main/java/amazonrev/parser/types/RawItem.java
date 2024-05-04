package amazonrev.parser.types;

import java.util.HashMap;

import amazonrev.item.ItemAuthor;
import amazonrev.item.ItemImage;
import amazonrev.item.ItemVideo;

// Note: Can't extend records. Modified from amazonrev.item.Item.
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
    ItemImage[] images,
    ItemVideo[] videos,
    String store,
    String[] categories,
    HashMap<String, ?> details,
    String parentAsin,
    Object boughtTogether, // Always null in dataset?
    ItemAuthor author) {
}
