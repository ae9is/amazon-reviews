package amazonrev.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import amazonrev.util.Encode;
import amazonrev.util.PagedResults;

@Repository
public class CategoryRepository {

  @Autowired
  JdbcClient client;

  public PagedResults<Category> getCategories(Boolean main, CategoryPagination params) {
    if (params == null) {
      params = new CategoryPagination();
    }
    Long idCursor = params.getIdCursor();
    Object sortCursor = params.getSortCursor();
    Integer limit = params.getLimit();
    CategorySort sort = params.getSort();
    String query;
    if (sort.equals(CategorySort.NAME)) {
      query = getLabelsQuery(main);
    } else if (sort.equals(CategorySort.ITEM_COUNT)) {
      query = getNumItemsQuery(main);
    } else if (sort.equals(CategorySort.RATING_NUMBER)) {
      query = getNumRatingsQuery(main);
    } else {
      throw new UnsupportedOperationException("Not implemented for " + sort.toString());
    }
    List<Category> res = client.sql(query)
        .param("sort_cursor", sortCursor)
        .param("id_cursor", idCursor)
        .param("limit", limit)
        .query(Category.class)
        .list();
    Category last = null;
    String newIdCursor = null;
    String newSortCursor = null;
    if (res.size() > 0) {
      last = res.get(res.size() - 1);
      if (last.id() != null) {
        newIdCursor = String.valueOf(last.id());
      }
      if (sort.equals(CategorySort.NAME)) {
        // Could remove, sort cursor shouldn't be needed as categories should be unique
        if (last.label() != null) {
          newSortCursor = String.valueOf(last.label());
        }
      } else if (sort.equals(CategorySort.ITEM_COUNT)) {
        if (last.itemCount() != null) {
          newSortCursor = String.valueOf(last.itemCount());
        }
      } else if (sort.equals(CategorySort.RATING_NUMBER)) {
        if (last.ratingNumber() != null) {
          newSortCursor = String.valueOf(last.ratingNumber());
        }
      } else {
        throw new UnsupportedOperationException("Not implemented for " + sort.toString());
      }
    }
    String encodedCursor = Encode.encodeCursorArray(newIdCursor, newSortCursor);
    return new PagedResults<Category>(res, encodedCursor);
  }

  // Note that the count is inclusive, i.e. items in multiple categories count for all.
  String getNumItemsQuery(boolean main) {
    String tableName = main ? "maincategory" : "category";
    String query = """
      WITH category_item_count AS (
        SELECT
          chi.category_id,
          count(chi.item_id)
        FROM
          category_has_item as chi
        GROUP BY
          chi.category_id
      )
      SELECT
        cat.category_id as id,
        cat.label,
        cic.count as itemCount,
        NULL as ratingNumber
      FROM
        """ + tableName + " as cat, " + """
        category_item_count as cic
      WHERE
        cat.category_id = cic.category_id
        AND (cic.count, cat.category_id) < (:sort_cursor, :id_cursor)
      GROUP BY
        cat.category_id,
        cat.label,
        cic.count
      ORDER BY cic.count DESC, cat.category_id DESC
      LIMIT :limit; 
      """;
    return query;
  }

  String getLabelsQuery(boolean main) {
    String tableName = main ? "maincategory" : "category";
    String query = """
      SELECT
        category_id as id,
        label,
        NULL as itemCount,
        NULL as ratingNumber
      FROM
    """ + tableName + """
      WHERE
        (label, category_id) > (:sort_cursor, :id_cursor)
      ORDER BY label ASC, category_id ASC
      LIMIT :limit;
    """;
    return query;
  }

  String getNumRatingsQuery(boolean main) {
    String tableName = main ? "maincategory" : "category";
    String query = """
      WITH cat_rating AS (
        SELECT
          cat.category_id,
          cat.label,
          sum(item.rating_number)
        FROM
      """ + tableName + " as cat, " + """
          category_has_item as chi,
          item
        WHERE
          cat.category_id = chi.category_id
          AND chi.item_id = item.item_id
        GROUP BY
          cat.category_id,
          cat.label
      )
      SELECT
        cat_rating.category_id as id,
        cat_rating.label,
        cat_rating.sum as ratingNumber,
        NULL as itemCount
      FROM
        cat_rating
      WHERE
        (cat_rating.sum, cat_rating.category_id) < (:sort_cursor, :id_cursor)
      GROUP BY
        cat_rating.category_id,
        cat_rating.label,
        cat_rating.sum
      ORDER BY cat_rating.sum DESC, cat_rating.category_id DESC
      LIMIT :limit;
        """;
    return query;
  }
}
