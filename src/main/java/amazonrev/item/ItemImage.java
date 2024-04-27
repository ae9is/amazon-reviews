package amazonrev.item;

public record ItemImage(
  Long id,
  String thumb,
  String large,
  String variant,
  String hiRes
) {}
