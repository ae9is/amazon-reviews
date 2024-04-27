package amazonrev.review;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import amazonrev.util.PagedResults;

@Controller
public class ReviewController {

  @Autowired
  ReviewRepository repo;

  @QueryMapping
  public PagedResults<Review> reviewsByAsin(@Argument("asin") String asin, @Argument("params") ReviewPagination params) {
    return repo.getByParentAsin(asin, params);
  }

  @QueryMapping
  public Review reviewById(@Argument("id") Long id) {
    return repo.getById(id);
  }

  @SchemaMapping(typeName="Review", field="images")
  public List<ReviewImage> images(Review review) {
    return repo.getImagesByReviewId(review.getId());
  }
}
