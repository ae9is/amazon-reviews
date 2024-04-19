package amazonrev.parser;

public class FileHeaders {
  String separator = "";

  public FileHeaders(String separator) {
    this.separator = separator;
  }

  final String[] categoryHasItem = new String[] { "category_id", "item_id", "is_item_main_category" };
  final String[] category = new String[] { "category_id", "label" };
  final String[] itemHasItemImage = new String[] { "item_id", "itemimage_id" };
  final String[] itemHasItemVideo = new String[] { "item_id", "itemvideo_id" };
  final String[] itemHasItemAuthor = new String[] { "item_id", "itemauthor_id" };
  final String[] item = new String[] { "item_id", "title", "subtitle", "average_rating", "rating_number", "features",
      "descriptions", "price", "store", "details", "parent_asin" };
  final String[] itemImage = new String[] { "itemimage_id", "thumb", "large", "variant", "hires" };
  final String[] itemVideo = new String[] { "itemvideo_id", "title", "uri", "creator_handle" };
  final String[] itemAuthor = new String[] { "itemauthor_id", "avatar", "name", "about" };
  final String[] reviewHasReviewImage = new String[] { "review_id", "reviewimage_id" };
  final String[] review = new String[] { "review_id", "rating", "title", "bodytext", "asin", "parent_asin", "username",
      "time_stamp", "helpful_vote", "verified_purchase" };
  final String[] reviewImage = new String[] { "reviewimage_id", "small_image_url", "medium_image_url",
      "large_image_url", "attachment_type" };
  final String[] users = new String[] { "user_id", "username" };

  public String getCategoryHasItem() {
    return String.join(this.separator, categoryHasItem) + "\n";
  }

  public String getCategory() {
    return String.join(this.separator, category) + "\n";
  }

  public String getItemHasItemImage() {
    return String.join(this.separator, itemHasItemImage) + "\n";
  }

  public String getItemHasItemVideo() {
    return String.join(this.separator, itemHasItemVideo) + "\n";
  }

  public String getItemHasItemAuthor() {
    return String.join(this.separator, itemHasItemAuthor) + "\n";
  }

  public String getItem() {
    return String.join(this.separator, item) + "\n";
  }

  public String getItemImage() {
    return String.join(this.separator, itemImage) + "\n";
  }

  public String getItemVideo() {
    return String.join(this.separator, itemVideo) + "\n";
  }

  public String getItemAuthor() {
    return String.join(this.separator, itemAuthor) + "\n";
  }

  public String getReviewHasReviewImage() {
    return String.join(this.separator, reviewHasReviewImage) + "\n";
  }

  public String getReview() {
    return String.join(this.separator, review) + "\n";
  }

  public String getReviewImage() {
    return String.join(this.separator, reviewImage) + "\n";
  }

  public String getUsers() {
    return String.join(this.separator, users) + "\n";
  }
}
