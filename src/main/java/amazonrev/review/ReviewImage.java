package amazonrev.review;

public record ReviewImage(
  Long id,
  String smallImageURL,
  String mediumImageURL,
  String largeImageURL,
  String attachmentType
) {}
