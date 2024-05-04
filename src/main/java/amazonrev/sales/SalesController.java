package amazonrev.sales;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import amazonrev.util.PagedResults;

@Controller
public class SalesController {

  @Autowired
  SalesRepository repo;

  @QueryMapping
  public PagedResults<ItemSales> topItemSales(@Argument("params") SalesPagination params) {
    return repo.getTopItemSales(params);
  }
}
