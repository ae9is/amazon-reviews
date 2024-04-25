package amazonrev.item;

public record Author(
  Long id,
  String avatar,
  String name,
  String[] about
) {
  public Author {
  }
}
