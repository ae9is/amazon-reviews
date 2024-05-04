package amazonrev.sales;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;

import amazonrev.item.ItemSummary;

public class ItemSalesRowMapper implements RowMapper<ItemSales> {
  static final ItemSalesRowMapper INSTANCE = new ItemSalesRowMapper();
  final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
  final JsonMapper mapper = new JsonMapper();

  ItemSalesRowMapper() {
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
  }

  public static ItemSalesRowMapper getInstance() {
    return INSTANCE;
  }

  @Override
  public ItemSales mapRow(@Nullable ResultSet rs, int rowNum) throws SQLException {
    if (rs == null) {
      return null;
    }
    Long id = rs.getLong("id");
    String title = rs.getString("title");
    String subtitle = rs.getString("subtitle");
    Double averageRating = rs.getDouble("averageRating");
    Long ratingNumber = rs.getLong("ratingNumber");
    Double price = rs.getDouble("price");
    String store = rs.getString("store");
    String parentAsin = rs.getString("parentAsin");
    ItemSummary itemSummary = new ItemSummary(id, title, subtitle, averageRating, ratingNumber, price, store,
        parentAsin);
    Long numVerifiedReviews = rs.getLong("numVerifiedReviews");
    Double sales = rs.getDouble("sales");
    return new ItemSales(itemSummary, numVerifiedReviews, sales);
  }
}
