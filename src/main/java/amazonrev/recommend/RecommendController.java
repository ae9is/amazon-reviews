package amazonrev.recommend;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import amazonrev.item.ItemSummary;
import amazonrev.util.PagedResults;

@Controller
public class RecommendController {

  @Autowired
  RecommendRepository repo;

  @QueryMapping
  public List<ItemSummary> itemSummariesByQuery(@Argument("queryText") String queryText,
      @Argument("limit") Integer limit) {
    return repo.getItemSummariesByQuery(queryText, limit);
  }

  @QueryMapping
  public PagedResults<ItemSummary> itemSummariesByQueryPaginated(@Argument("queryText") String queryText,
      @Argument("params") VectorPagination params) {
    return repo.getItemSummariesByQuery(queryText, params);
  }
}
