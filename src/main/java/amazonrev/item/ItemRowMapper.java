package amazonrev.item;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;

/**
 * Custom row mapper for Item in order to support mapping Postgres Array to String[]
 *  and Postgres JSONB to HashMap<String, Object>.
 */
public class ItemRowMapper implements RowMapper<Item> {
  static final ItemRowMapper INSTANCE = new ItemRowMapper();
  final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
  final JsonMapper mapper = new JsonMapper();

  ItemRowMapper() {
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
  }

  public static ItemRowMapper getInstance() {
    return INSTANCE;
  }

  @Override
  public Item mapRow(@Nullable ResultSet resultSet, int rowNum) throws SQLException {
    if (resultSet == null) {
      return null;
    }
    amazonrev.util.ResultSet rs = new amazonrev.util.ResultSet(resultSet);
    String detailsString = rs.getString("details");
    HashMap<String, Object> details;
    try {
      details = mapper.readValue(detailsString, typeRef);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      details = null;
    }
    Array featuresArray = rs.getArray("features");
    Array descriptionArray = rs.getArray("description");
    Array categoriesArray = rs.getArray("categories");
    String[] features = (String[]) featuresArray.getArray();
    String[] description = (String[]) descriptionArray.getArray();
    String[] categories = (String[]) categoriesArray.getArray();
    return new Item(
      rs.getLong("id"),
      rs.getString("mainCategory"),
      rs.getString("title"),
      rs.getString("subtitle"),
      rs.getDouble("averageRating"),
      rs.getLong("ratingNumber"),
      features,
      description,
      rs.getDouble("price"),
      rs.getString("store"),
      categories,
      details,
      rs.getString("parentAsin")
    );
  }
}
