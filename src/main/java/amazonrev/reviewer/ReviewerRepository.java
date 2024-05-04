package amazonrev.reviewer;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import amazonrev.util.Encode;
import amazonrev.util.PagedResults;
import amazonrev.util.TimeUtils;

@Repository
public class ReviewerRepository {

  @Autowired
  JdbcClient client;

  public PagedResults<Reviewer> getTopReviewers(Integer year, ReviewerPagination params) {
    if (params == null) {
      params = new ReviewerPagination();
    }
    Long idCursor = params.getIdCursor();
    Object sortCursor = params.getSortCursor();
    Integer limit = params.getLimit();
    ReviewerSort sort = params.getSort();
    boolean allTime = (year == null) ? true : false; // Whether to format queries for a specific year or for all time
    String query;
    if (sort.equals(ReviewerSort.NUM_REVIEWS)) {
      query = getNumReviewsQuery(allTime);
    } else if (sort.equals(ReviewerSort.VOTES)) {
      query = getNumVotesQuery(allTime);
    } else {
      throw new UnsupportedOperationException("Not implemented for " + sort.toString());
    }
    OffsetDateTime yearODT = TimeUtils.toOffsetDateTime(year);
    List<Reviewer> res = client.sql(query)
        .param("year", yearODT)
        .param("sort_cursor", sortCursor)
        .param("id_cursor", idCursor)
        .param("limit", limit)
        .query(ReviewerRowMapper.getInstance())
        .list();
    Reviewer last = null;
    String newIdCursor = null;
    String newSortCursor = null;
    if (res.size() > 0) {
      last = res.get(res.size() - 1);
      if (last.user() != null && last.user().id() != null) {
        newIdCursor = String.valueOf(last.user().id());
      }
      if (sort.equals(ReviewerSort.NUM_REVIEWS)) {
        if (last.numReviews() != null) {
          newSortCursor = String.valueOf(last.numReviews());
        }
      } else if (sort.equals(ReviewerSort.VOTES)) {
        if (last.votes() != null) {
          newSortCursor = String.valueOf(last.votes());
        }
      } else {
        throw new UnsupportedOperationException("Not implemented for " + sort.toString());
      }
    }
    String encodedCursor = Encode.encodeCursorArray(newIdCursor, newSortCursor);
    return new PagedResults<Reviewer>(res, encodedCursor);
  }

  final String yearCondition = """
    AND review.time_stamp >= :year
    AND review.time_stamp < :year + INTERVAL '1 year'
  """;

  String getNumReviewsQuery(boolean allTime) {
    String query = """
      WITH num_reviews AS (
        SELECT
          uhr.user_id,
          count(uhr.review_id)
        FROM
          user_has_review as uhr,
          review
        WHERE
          uhr.review_id = review.review_id
    """;
    if (!allTime) {
      query += yearCondition;
    }
    query += """
        GROUP BY
          user_id
      )
      SELECT
        nr.user_id AS id,
        users.amzn_uid AS amazonUID,
        nr.count AS numReviews,
        NULL AS votes
      FROM
        num_reviews AS nr,
        users
      WHERE
        nr.user_id = users.user_id
        AND (nr.count, nr.user_id) < (:sort_cursor, :id_cursor)
      GROUP BY
        nr.user_id,
        users.amzn_uid,
        nr.count
      ORDER BY nr.count DESC, nr.user_id DESC
      LIMIT :limit;
    """;
    return query;
  }

  String getNumVotesQuery(boolean allTime) {
    String query = """
      WITH user_votes AS (
        SELECT
          uhr.user_id,
          sum(review.helpful_vote) AS votes
        FROM
          user_has_review AS uhr,
          review
        WHERE
          uhr.review_id = review.review_id
    """;
    if (!allTime) { 
      query += yearCondition;
    }
    query += """
        GROUP BY
          uhr.user_id
      )
      SELECT
        uv.user_id AS id,
        users.amzn_uid AS amazonUID,
        uv.votes,
        NULL AS numReviews
      FROM
        user_votes as uv,
        users
      WHERE
        uv.user_id = users.user_id
        AND (uv.votes, uv.user_id) < (:sort_cursor, :id_cursor)
      GROUP BY
        uv.user_id,
        users.amzn_uid,
        uv.votes
      ORDER BY uv.votes DESC, uv.user_id DESC
      LIMIT :limit;
    """;
    return query;
  }
}
