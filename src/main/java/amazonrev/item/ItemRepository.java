package amazonrev.item;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import amazonrev.util.Encode;
import amazonrev.util.Pagination;
import amazonrev.util.PagedResults;

@Repository
public class ItemRepository {

  @Autowired
  JdbcClient client;

  public PagedResults<ItemSummary> getItemSummariesByCategory(String category, ItemPagination params) {
    if (params == null) {
      params = new ItemPagination();
    }
    Long idCursor = params.getIdCursor();
    Object sortCursor = params.getSortCursor();
    Integer limit = params.getLimit();
    ItemSort sort = params.getSort();
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
          item,
          category,
          category_has_item
        WHERE
          category.label = :category
          AND category.category_id = category_has_item.category_id
          AND item.item_id = category_has_item.item_id
        """;
    String conditionString = "";
    String sortString;
    if (sort.equals(ItemSort.AVG_RATING)) {
      conditionString += " AND (item.average_rating, item.item_id) <";
      sortString = " item.average_rating DESC, item.item_id DESC";
    } else if (sort.equals(ItemSort.LOW_PRICE)) {
      conditionString += " AND (item.price, item.item_id) >";
      sortString = " item.price ASC, item.item_id ASC";
    } else if (sort.equals(ItemSort.RATING_NUMBER)) {
      conditionString += " AND (item.rating_number, item.item_id) <";
      sortString = " item.rating_number DESC, item.item_id DESC";
    } else {
      throw new UnsupportedOperationException("Not implemented for " + sort.toString());
    }
    conditionString += " (:sort_cursor, :id_cursor) ORDER BY" + sortString + " LIMIT :limit";
    query += conditionString;
    List<ItemSummary> res = client.sql(query)
        .param("category", category)
        .param("sort_cursor", sortCursor)
        .param("id_cursor", idCursor)
        .param("limit", limit)
        .query(ItemSummary.class)
        .list();
    ItemSummary last = null;
    String newIdCursor = null;
    String newSortCursor = null;
    if (res.size() > 0) {
      last = res.get(res.size() - 1);
      if (last.id() != null) {
        newIdCursor = String.valueOf(last.id());
      }
      if (sort.equals(ItemSort.AVG_RATING)) {
        if (last.averageRating() != null) {
          newSortCursor = String.valueOf(last.averageRating());
        }
      } else if (sort.equals(ItemSort.LOW_PRICE)) {
        if (last.price() != null) {
          newSortCursor = String.valueOf(last.price());
        }
      } else if (sort.equals(ItemSort.RATING_NUMBER)) {
        if (last.ratingNumber() != null) {
          newSortCursor = String.valueOf(last.ratingNumber());
        }
      } else {
        throw new UnsupportedOperationException("Not implemented for " + sort.toString());
      }
    }
    String encodedCursor = Encode.encodeCursorArray(newIdCursor, newSortCursor);
    return new PagedResults<ItemSummary>(res, encodedCursor);
  }

  public Item getById(Long id) {
    return getByIdOrParentAsin(id, null);
  }

  public Item getByParentAsin(String asin) {
    return getByIdOrParentAsin(null, asin);
  }

  private Item getByIdOrParentAsin(Long id, String parentAsin) {
    Object key;
    String conditionString;
    if (id != null) {
      key = id;
      conditionString = " item.item_id = :key ";
    } else if (parentAsin != null) {
      key = parentAsin;
      conditionString = " item.parent_asin = :key ";
    } else {
      throw new UnsupportedOperationException("Either id or parentAsin must be defined");
    }
    String query = """
        WITH item_categories AS (
          SELECT
            array_agg(label) AS categories
          FROM
            item,
            category,
            category_has_item
          WHERE
        """;
    query += conditionString;
    query += """
            AND item.item_id = category_has_item.item_id
            AND category.category_id = category_has_item.category_id
        )
        SELECT
          item.item_id AS id,
          category.label AS mainCategory,
          item.title AS title,
          item.subtitle AS subtitle,
          item.average_rating AS averageRating,
          item.rating_number AS ratingNumber,
          item.features AS features,
          item.descriptions AS description,
          item.price AS price,
          item.store AS store,
          item.details AS details,
          item.parent_asin AS parentAsin,
          item_categories.categories AS categories
        FROM
          item,
          category,
          category_has_item,
          item_categories
        WHERE
        """;
    query += conditionString;
    query += """
            AND item.item_id = category_has_item.item_id
            AND category.category_id = category_has_item.category_id
            AND category_has_item.is_item_main_category = true
          LIMIT 1
        """;
    Optional<Item> res = client.sql(query)
        .param("key", key)
        .query(ItemRowMapper.getInstance())
        .optional();
    return res.isPresent() ? res.get() : null;
  }

  public ItemAuthor getAuthorByItemId(Long id) {
    Optional<ItemAuthor> res = client.sql("""
          SELECT
            auth.itemauthor_id AS id,
            auth.avatar AS avatar,
            auth.fullname AS "name",
            auth.about AS about
          FROM
            itemauthor AS auth,
            item_has_itemauthor
          WHERE
            item_has_itemauthor.item_id = :id
            AND item_has_itemauthor.itemauthor_id = auth.itemauthor_id
          LIMIT 1
        """)
        .param("id", id)
        .query(ItemAuthor.class)
        .optional();
    return res.isPresent() ? res.get() : null;
  }

  public List<ItemImage> getImagesByItemId(Long id) {
    PagedResults<ItemImage> results = getImagesByItemId(id, null);
    return results.list();
  }

  public PagedResults<ItemImage> getImagesByItemId(Long id, Pagination params) {
    if (params == null) {
      params = new Pagination();
    }
    Long cursor = params.getIdCursor();
    Integer limit = params.getLimit();
    String query = """
          SELECT
            image.itemimage_id AS id,
            image.thumb AS thumb,
            image.large AS large,
            image.variant AS variant,
            image.hires AS hires
          FROM
            itemimage AS image,
            item_has_itemimage
          WHERE
            item_has_itemimage.item_id = :id
            AND item_has_itemimage.itemimage_id = image.itemimage_id
            AND image.itemimage_id > :cursor
          ORDER BY image.itemimage_id ASC LIMIT :limit
        """;
    List<ItemImage> res = client.sql(query)
        .param("id", id)
        .param("cursor", cursor)
        .param("limit", limit)
        .query(ItemImage.class)
        .list();
    ItemImage last;
    String newIdCursor = null;
    if (res.size() > 0) {
      last = res.get(res.size() - 1);
      if (last.id() != null) {
        newIdCursor = String.valueOf(last.id());
      }
    }
    String encodedCursor = Encode.encodeCursorArray(newIdCursor);
    return new PagedResults<ItemImage>(res, encodedCursor);
  }

  public List<ItemVideo> getVideosByItemId(Long id) {
    PagedResults<ItemVideo> results = getVideosByItemId(id, null);
    return results.list();
  }

  public PagedResults<ItemVideo> getVideosByItemId(Long id, Pagination params) {
    if (params == null) {
      params = new Pagination();
    }
    Long cursor = params.getIdCursor();
    Integer limit = params.getLimit();
    String query = """
          SELECT
            video.itemvideo_id AS id,
            video.title AS title,
            video.uri AS url,
            video.creator_handle AS creatorHandle
          FROM
            itemvideo AS video,
            item_has_itemvideo
          WHERE
            item_has_itemvideo.item_id = :id
            AND item_has_itemvideo.itemvideo_id = video.itemvideo_id
            AND video.itemvideo_id > :cursor
          ORDER BY video.itemvideo_id ASC LIMIT :limit
        """;
    List<ItemVideo> res = client.sql(query)
        .param("id", id)
        .param("cursor", cursor)
        .param("limit", limit)
        .query(ItemVideo.class)
        .list();
    ItemVideo last;
    String newIdCursor = null;
    if (res.size() > 0) {
      last = res.get(res.size() - 1);
      if (last.id() != null) {
        newIdCursor = String.valueOf(last.id());
      }
    }
    String encodedCursor = Encode.encodeCursorArray(newIdCursor);
    return new PagedResults<ItemVideo>(res, encodedCursor);
  }
}
