package amazonrev.category;

public record Category(
    Long id,
    String label,
    Long itemCount,
    Long ratingNumber) {
}
