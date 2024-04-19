package amazonrev.types.item;

public record Author(
  long id,
  String avatar,
  String name,
  String[] about
) {}
