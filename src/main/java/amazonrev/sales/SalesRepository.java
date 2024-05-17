package amazonrev.sales;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import amazonrev.util.Encode;
import amazonrev.util.PagedResults;
import amazonrev.util.exception.BadRequestException;

@Repository
public class SalesRepository {

  @Autowired
  JdbcClient client;

  /**
   * Note that sales figures are gross underestimates just for rough comparisons between items,
   *  as most customers will not leave a rating or product review.
   */
  public PagedResults<ItemSales> getTopItemSales(SalesPagination params) {
    if (params == null) {
      params = new SalesPagination();
    }
    Long idCursor = params.getIdCursor();
    Object sortCursor = params.getSortCursor();
    Integer limit = params.getLimit();
    SalesSort sort = params.getSort();
    String query;
    if (sort.equals(SalesSort.RATING_NUMBER)) {
      query = ratingNumberQuery;
    } else if (sort.equals(SalesSort.VERIF_PURCHASE)) {
      query = verifiedPurchaseQuery;
    } else {
      throw new BadRequestException("Not implemented for " + sort.toString());
    }
    List<ItemSales> res = client.sql(query)
        .param("sort_cursor", sortCursor)
        .param("id_cursor", idCursor)
        .param("limit", limit)
        .query(ItemSalesRowMapper.getInstance())
        .list();
    ItemSales last = null;
    String newIdCursor = null;
    String newSortCursor = null;
    if (res.size() > 0) {
      last = res.get(res.size() - 1);
      if (last.item() != null && last.item().getId() != null) {
        newIdCursor = String.valueOf(last.item().getId());
      }
      if (last.sales() != null) {
        newSortCursor = String.valueOf(last.sales());
      }
    }
    String encodedCursor = Encode.encodeCursorArray(newIdCursor, newSortCursor);
    return new PagedResults<ItemSales>(res, encodedCursor);
  }

  final String ratingNumberQuery = """
    WITH item_sales AS (
      SELECT
        item_id,
        title,
        subtitle,
        average_rating,
        rating_number,
        price,
        store,
        parent_asin,
        rating_number * price AS sales
      FROM
        item
      WHERE
        price IS NOT NULL
    )
    SELECT
      item_id AS id,
      title,
      subtitle,
      average_rating AS averageRating,
      rating_number AS ratingNumber,
      price,
      store,
      parent_asin AS parentAsin,
      NULL AS numVerifiedReviews,
      sales
    FROM
      item_sales
    WHERE
      (sales, item_id)  < (:sort_cursor, :id_cursor)
    ORDER BY sales DESC
    LIMIT :limit;
  """;

  final String verifiedPurchaseQuery = """
    WITH item_sales AS (
      SELECT
        item.item_id,
        item.title,
        item.subtitle,
        item.average_rating,
        item.rating_number,
        item.price,
        item.store,
        item.parent_asin,
        count(review.review_id) AS num_reviews,
        item.price * count(review.review_id) AS sales
      FROM
        item
      LEFT JOIN
        review
      ON
        item.parent_asin = review.parent_asin
      WHERE
        item.price IS NOT NULL
        AND review.verified_purchase = true
      GROUP BY
        item.item_id
    )
    SELECT
      item_id AS id,
      title,
      subtitle,
      average_rating AS averageRating,
      rating_number AS ratingNumber,
      price,
      store,
      parent_asin AS parentAsin,
      num_reviews AS numVerifiedReviews,
      sales
    FROM
      item_sales
    WHERE
      (sales, item_id)  < (:sort_cursor, :id_cursor)
    ORDER BY sales DESC
    LIMIT :limit;
  """;
}
