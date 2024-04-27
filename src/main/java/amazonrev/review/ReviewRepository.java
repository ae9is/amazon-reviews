package amazonrev.review;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import amazonrev.util.Encode;
import amazonrev.util.Pagination;
import amazonrev.util.PagedResults;

@Repository
public class ReviewRepository {

  @Autowired
  JdbcClient client;

  public PagedResults<Review> getByParentAsin(String parentAsin, ReviewPagination params) {
    if (params == null) {
      params = new ReviewPagination();
    }
    Long idCursor = params.getIdCursor();
    Object sortCursor = params.getSortCursor();
    Integer limit = params.getLimit();
    ReviewSort sort = params.getSort();
    String query = """
        SELECT
          review.review_id as id,
          review.rating as rating,
          review.title title,
          review.bodytext as text,
          review.asin as asin,
          review.parent_asin as parentAsin,
          review.time_stamp as timestamp,
          review.helpful_vote as helpfulVote,
          review.verified_purchase as verifiedPurchase,
          users.amzn_uid as userID
        FROM
          review,
          users,
          user_has_review as userrev
        WHERE
          review.parent_asin = :parent_asin
          AND review.review_id = userrev.review_id
          AND users.user_id = userrev.user_id
        """;
    String conditionString = "";
    String sortString;
    if (sort.equals(ReviewSort.VOTES)) {
      conditionString += " AND (review.helpful_vote, review.review_id) <";
      sortString = " review.helpful_vote DESC, review.review_id DESC";
    } else if (sort.equals(ReviewSort.NEWEST)) {
      conditionString += " AND (review.time_stamp, review.review_id) <";
      sortString = " review.time_stamp DESC, review.review_id DESC";
    } else {
      throw new UnsupportedOperationException("Not implemented for " + sort.toString());
    }
    conditionString += " (:sort_cursor, :id_cursor) ORDER BY" + sortString + " LIMIT :limit";
    query += conditionString;
    List<Review> res = client.sql(query)
        .param("parent_asin", parentAsin)
        .param("sort_cursor", sortCursor)
        .param("id_cursor", idCursor)
        .param("limit", limit)
        .query(Review.class)
        .list();
    Review last = null;
    String newIdCursor = null;
    String newSortCursor = null;
    if (res.size() > 0) {
      last = res.get(res.size() - 1);
      if (last.getId() != null) {
        newIdCursor = String.valueOf(last.getId());
      }
      if (sort.equals(ReviewSort.NEWEST)) {
        if (last.getTimestamp() != null) {
          newSortCursor = String.valueOf(last.getTimestamp());
        }
      } else if (sort.equals(ReviewSort.VOTES)) {
        if (last.getHelpfulVote() != null) {
          newSortCursor = String.valueOf(last.getHelpfulVote());
        }
      } else {
        throw new UnsupportedOperationException("Not implemented for " + sort.toString());
      }
    }
    String encodedCursor = Encode.encodeCursorArray(newIdCursor, newSortCursor);
    return new PagedResults<Review>(res, encodedCursor);
  }

  public List<ReviewImage> getImagesByReviewId(Long id) {
    PagedResults<ReviewImage> results = getImagesByReviewId(id, null);
    return results.list();
  }

  public PagedResults<ReviewImage> getImagesByReviewId(Long id, Pagination params) {
    if (params == null) {
      params = new ReviewPagination();
    }
    Long cursor = params.getIdCursor();
    Integer limit = params.getLimit();
    String query = """
          SELECT
            image.reviewimage_id as id,
            image.small_image_url as smallImageUrl,
            image.medium_image_url as mediumImageUrl,
            image.large_image_url as largeImageUrl,
            image.attachment_type as attachmentType
          FROM
            reviewimage as image,
            review_has_reviewimage
          WHERE
            review_has_reviewimage.review_id = :id
            AND review_has_reviewimage.reviewimage_id = image.reviewimage_id
            AND image.reviewimage_id > :cursor
          ORDER BY image.reviewimage_id ASC LIMIT :limit
        """;
    List<ReviewImage> res = client.sql(query)
        .param("id", id)
        .param("cursor", cursor)
        .param("limit", limit)
        .query(ReviewImage.class)
        .list();
    ReviewImage last;
    String newIdCursor = null;
    if (res.size() > 0) {
      last = res.get(res.size() - 1);
      if (last.id() != null) {
        newIdCursor = String.valueOf(last.id());
      }
    }
    String encodedCursor = Encode.encodeCursorArray(newIdCursor);
    return new PagedResults<ReviewImage>(res, encodedCursor);
  }

  public Review getById(Long id) {
    Optional<Review> res = client.sql("""
        SELECT
          review.review_id as id,
          review.rating as rating,
          review.title title,
          review.bodytext as text,
          review.asin as asin,
          review.parent_asin as parentAsin,
          review.time_stamp as timestamp,
          review.helpful_vote as helpfulVote,
          review.verified_purchase as verifiedPurchase,
          users.amzn_uid as userID
        FROM
          review,
          users,
          user_has_review as userrev
        WHERE
          review.review_id = :review_id
          AND userrev.review_id = review.review_id
          AND userrev.user_id = users.user_id
        LIMIT 1
        """)
        .param("review_id", id)
        .query(Review.class)
        .optional();
    return res.isPresent() ? res.get() : null;
  }
}
