package amazonrev.item;

public record ItemAuthor(
    Long id,
    String avatar,
    String name,
    String[] about) {
}
