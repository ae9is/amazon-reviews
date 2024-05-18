package amazonrev.recommend;

import java.util.List;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import amazonrev.config.Constants;
import amazonrev.item.ItemSummary;
import amazonrev.util.Encode;
import amazonrev.util.PagedResults;

@Repository
public class RecommendRepository {

  @Autowired
  JdbcClient jdbcClient;

  @Autowired
  ModelApiClient apiClient;

  /**
   * Use natural language to get relevant item summaries.
   * Encodes query to vector embedding using model and then compares vector distance to 
   *  pre-calculated embeddings in database.
   * @param queryText Natural language to query for relevant item summaries
   * @param limit Number of results to return
   * @return Item summaries
   */
  public List<ItemSummary> getItemSummariesByQuery(String queryText, Integer limit) {
    // Convert query text into model embedding via Python api
    Embeddable body = new Embeddable(queryText);
    String embedding = apiClient.post(Constants.getEmbeddingCreatePath(), body);
    // Run vector similarity search to get relevant results
    // Supported vector distance functions in ORDER BY:
    // <-> L2 distance
    // <#> (negative) inner product
    // <=> cosine distance
    // <+> L1 distance
    // ref: https://github.com/pgvector/pgvector?tab=readme-ov-file#querying
    String query = """
        SELECT
          item.item_id AS id,
          item.title AS title,
          item.subtitle AS subtitle,
          item.average_rating AS averageRating,
          item.rating_number AS ratingNumber,
          item.price AS price,
          item.store AS store,
          item.parent_asin AS parentAsin
        FROM
          item_embed as ie,
          item
        WHERE
          ie.item_id = item.item_id
        ORDER BY
          ie.title <#> CAST(:embedding as vector) ASC
        LIMIT :limit
        """;
    List<ItemSummary> res = jdbcClient.sql(query)
        .param("embedding", embedding)
        .param("limit", limit)
        .query(ItemSummary.class)
        .list();
    return res;
  }

  /**
   * getItemSummariesByQuery(String queryText, Integer limit) but supporting full pagination.
   * Slower, since calculations of the vector distance between the arbitrary user query and 
   *  other vectors is required.
   */
  public PagedResults<ItemSummary> getItemSummariesByQuery(String queryText, VectorPagination params) {
    Embeddable body = new Embeddable(queryText);
    String embedding = apiClient.post(Constants.getEmbeddingCreatePath(), body);
    if (params == null) {
      params = new VectorPagination();
    }
    Long idCursor = params.getIdCursor();
    Integer limit = params.getLimit();
    Object sortCursor = params.getSortCursor();
    String query = """
        WITH dist AS (
          SELECT
            item_id,
            ie.title <#> CAST(:embedding as vector) as distance
          FROM
            item_embed as ie
        )
        SELECT
          item.item_id AS id,
          item.title AS title,
          item.subtitle AS subtitle,
          item.average_rating AS averageRating,
          item.rating_number AS ratingNumber,
          item.price AS price,
          item.store AS store,
          item.parent_asin AS parentAsin,
          dist.distance as distance
        FROM
          dist,
          item
        WHERE
          dist.item_id = item.item_id
          AND (dist.distance, dist.item_id) > (:sort_cursor, :id_cursor)
        ORDER BY
          dist.distance ASC,
          dist.item_id ASC
        LIMIT :limit
        """;
    List<RecommendItem> res = jdbcClient.sql(query)
        .param("embedding", embedding)
        .param("sort_cursor", sortCursor)
        .param("id_cursor", idCursor)
        .param("limit", limit)
        .query(RecommendItem.class)
        .list();
    RecommendItem last = null;
    String newIdCursor = null;
    String newSortCursor = null;
    if (res.size() > 0) {
      last = res.get(res.size() - 1);
      if (last.getId() != null) {
        newIdCursor = String.valueOf(last.getId());
      }
      if (last.getDistance() != null) {
        newSortCursor = String.valueOf(last.getDistance());
      }
    }
    String encodedCursor = Encode.encodeCursorArray(newIdCursor, newSortCursor);
    List<ItemSummary> summariesOnly = new ArrayList<ItemSummary>(res);
    return new PagedResults<ItemSummary>(summariesOnly, encodedCursor);
  }
}
