package amazonrev.types.item;

import java.io.IOException;

public enum MainCategory {
  ALL_BEAUTY,
  ALL_ELECTRONICS,
  AMAZON_FASHION,
  AMAZON_HOME,
  ARTS_CRAFTS_SEWING,
  AUTOMOTIVE,
  CAMERA_PHOTO,
  CAR_ELECTRONICS,
  CELL_PHONES_ACCESSORIES,
  COMPUTERS,
  HEALTH_PERSONAL_CARE,
  HOME_AUDIO_THEATER,
  INDUSTRIAL_SCIENTIFIC,
  MUSICAL_INSTRUMENTS,
  OFFICE_PRODUCTS,
  SOFTWARE,
  SPORTS_OUTDOORS,
  TOOLS_HOME_IMPROVEMENT,
  TOYS_GAMES;

  public String toValue() {
    switch (this) {
      case ALL_BEAUTY: return "All Beauty";
      case ALL_ELECTRONICS: return "All Electronics";
      case AMAZON_FASHION: return "AMAZON FASHION";
      case AMAZON_HOME: return "Amazon Home";
      case ARTS_CRAFTS_SEWING: return "Arts, Crafts & Sewing";
      case AUTOMOTIVE: return "Automotive";
      case CAMERA_PHOTO: return "Camera & Photo";
      case CAR_ELECTRONICS: return "Car Electronics";
      case CELL_PHONES_ACCESSORIES: return "Cell Phones & Accessories";
      case COMPUTERS: return "Computers";
      case HEALTH_PERSONAL_CARE: return "Health & Personal Care";
      case HOME_AUDIO_THEATER: return "Home Audio & Theater";
      case INDUSTRIAL_SCIENTIFIC: return "Industrial & Scientific";
      case MUSICAL_INSTRUMENTS: return "Musical Instruments";
      case OFFICE_PRODUCTS: return "Office Products";
      case SOFTWARE: return "Software";
      case SPORTS_OUTDOORS: return "Sports & Outdoors";
      case TOOLS_HOME_IMPROVEMENT: return "Tools & Home Improvement";
      case TOYS_GAMES: return "Toys & Games";
    }
    return null;
  }

  // For database relations
  public int intValue() {
    switch (this) {
      case ALL_BEAUTY: return 0;
      case ALL_ELECTRONICS: return 1;
      case AMAZON_FASHION: return 2;
      case AMAZON_HOME: return 3;
      case ARTS_CRAFTS_SEWING: return 4;
      case AUTOMOTIVE: return 5;
      case CAMERA_PHOTO: return 6;
      case CAR_ELECTRONICS: return 7;
      case CELL_PHONES_ACCESSORIES: return 8;
      case COMPUTERS: return 9;
      case HEALTH_PERSONAL_CARE: return 10;
      case HOME_AUDIO_THEATER: return 11;
      case INDUSTRIAL_SCIENTIFIC: return 12;
      case MUSICAL_INSTRUMENTS: return 13;
      case OFFICE_PRODUCTS: return 14;
      case SOFTWARE: return 15;
      case SPORTS_OUTDOORS: return 16;
      case TOOLS_HOME_IMPROVEMENT: return 17;
      case TOYS_GAMES: return 18;
    }
    return -1;
  }

  public static MainCategory forValue(String value) throws IOException {
    if (value.equals("All Beauty")) return ALL_BEAUTY;
    if (value.equals("All Electronics")) return ALL_ELECTRONICS;
    if (value.equals("AMAZON FASHION")) return AMAZON_FASHION;
    if (value.equals("Amazon Home")) return AMAZON_HOME;
    if (value.equals("Arts, Crafts & Sewing")) return ARTS_CRAFTS_SEWING;
    if (value.equals("Automotive")) return AUTOMOTIVE;
    if (value.equals("Camera & Photo")) return CAMERA_PHOTO;
    if (value.equals("Car Electronics")) return CAR_ELECTRONICS;
    if (value.equals("Cell Phones & Accessories")) return CELL_PHONES_ACCESSORIES;
    if (value.equals("Computers")) return COMPUTERS;
    if (value.equals("Health & Personal Care")) return HEALTH_PERSONAL_CARE;
    if (value.equals("Home Audio & Theater")) return HOME_AUDIO_THEATER;
    if (value.equals("Industrial & Scientific")) return INDUSTRIAL_SCIENTIFIC;
    if (value.equals("Musical Instruments")) return MUSICAL_INSTRUMENTS;
    if (value.equals("Office Products")) return OFFICE_PRODUCTS;
    if (value.equals("Software")) return SOFTWARE;
    if (value.equals("Sports & Outdoors")) return SPORTS_OUTDOORS;
    if (value.equals("Tools & Home Improvement")) return TOOLS_HOME_IMPROVEMENT;
    if (value.equals("Toys & Games")) return TOYS_GAMES;
    throw new IOException("Cannot deserialize MainCategory");
  }
}
