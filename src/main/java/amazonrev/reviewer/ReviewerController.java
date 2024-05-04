package amazonrev.reviewer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import amazonrev.util.PagedResults;

@Controller
public class ReviewerController {

  @Autowired
  ReviewerRepository repo;

  @QueryMapping
  public PagedResults<Reviewer> topReviewers(@Argument("year") Integer year, @Argument("params") ReviewerPagination params) {
    return repo.getTopReviewers(year, params);
  }
}
