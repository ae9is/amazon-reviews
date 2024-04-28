package amazonrev.item;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import amazonrev.util.PagedResults;

@Controller
public class ItemController {

  @Autowired
  ItemRepository repo;

  @QueryMapping
  public PagedResults<ItemSummary> itemSummariesByCategory(@Argument("category") String category, @Argument("params") ItemPagination params) {
    return repo.getItemSummariesByCategory(category, params);
  }

  @QueryMapping
  public Item itemById(@Argument("id") Long id) {
    return repo.getById(id);
  }

  @QueryMapping
  public Item itemByAsin(@Argument("asin") String asin) {
    return repo.getByParentAsin(asin);
  }

  @SchemaMapping(typeName="Item", field="author")
  public ItemAuthor author(Item item) {
    return repo.getAuthorByItemId(item.getId());
  }

  @SchemaMapping(typeName="Item", field="images")
  public List<ItemImage> images(Item item) {
    return repo.getImagesByItemId(item.getId());
  }

  @SchemaMapping(typeName="Item", field="videos")
  public List<ItemVideo> videos(Item item) {
    return repo.getVideosByItemId(item.getId());
  }
}
