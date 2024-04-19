package amazonrev.types.item;

public record Image(
  long id,
  String thumb,
  String large,
  String variant,
  String hiRes
) {}
