package amazonrev.types.review;

public record Image(
  long id,
  String smallImageURL,
  String mediumImageURL,
  String largeImageURL,
  String attachmentType
) {}
