package amazonrev.item;

public record Image(
  Long id,
  String thumb,
  String large,
  String variant,
  String hiRes
) {}
