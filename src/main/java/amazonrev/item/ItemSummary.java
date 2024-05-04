package amazonrev.item;

public record ItemSummary(
    Long id,
    String title,
    String subtitle,
    Double averageRating,
    Long ratingNumber,
    Double price,
    String store,
    String parentAsin) {
}
