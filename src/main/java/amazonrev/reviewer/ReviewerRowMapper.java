package amazonrev.reviewer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class ReviewerRowMapper implements RowMapper<Reviewer> {
  static final ReviewerRowMapper INSTANCE = new ReviewerRowMapper();
  final TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
  final JsonMapper mapper = new JsonMapper();

  ReviewerRowMapper() {
    mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
  }

  public static ReviewerRowMapper getInstance() {
    return INSTANCE;
  }

  @Override
  public Reviewer mapRow(@Nullable ResultSet resultSet, int rowNum) throws SQLException {
    if (resultSet == null) {
      return null;
    }
    amazonrev.util.ResultSet rs = new amazonrev.util.ResultSet(resultSet);
    Long id = rs.getLong("id");
    String amazonUID = rs.getString("amazonUID");
    User user = new User(id, amazonUID);
    Long numReviews = rs.getLong("numReviews");
    Long votes = rs.getLong("votes");
    return new Reviewer(user, numReviews, votes);
  }
}
