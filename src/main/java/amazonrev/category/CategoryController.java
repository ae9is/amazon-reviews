package amazonrev.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import amazonrev.util.PagedResults;

@Controller
public class CategoryController {

  @Autowired
  CategoryRepository repo;

  @QueryMapping
  public PagedResults<Category> categories(@Argument("main") Boolean main,
      @Argument("params") CategoryPagination params) {
    return repo.getCategories(main, params);
  }
}
