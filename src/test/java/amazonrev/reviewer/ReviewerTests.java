package amazonrev.reviewer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.web.reactive.server.WebTestClient;

import amazonrev.util.TestUtils;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
@AutoConfigureWebTestClient
public class ReviewerTests {
  @LocalServerPort
  int randomServerPort;

  @Autowired
  HttpGraphQlTester tester;

  @Autowired
  WebTestClient client;

  @Test
  void shouldGetNumReviewsPerReviewerDistribution() throws IOException {
    String document = "reviewer/numReviews";
    tester
      .document(TestUtils.documentFor(document))
      .variable("numTiles", "100")
      .execute()
      .path("$")
      .matchesJson(TestUtils.expectedResponseFor(document));
  }

  @Test
  void shouldGetTopReviewersByNumReviews() throws IOException {
    String graphqlDocument = "reviewer/topReviewers";
    String responseDocument = graphqlDocument + "ByNumReviews";
    doTopReviewersTest(graphqlDocument, responseDocument, "NUM_REVIEWS", "OTk2MTcfMjQw");
  }

  @Test
  void shouldGetTopReviewersByVotes() throws IOException {
    String graphqlDocument = "reviewer/topReviewers";
    String responseDocument = graphqlDocument + "ByVotes";
    doTopReviewersTest(graphqlDocument, responseDocument, "VOTES", "MTM2MzU4OR8yODky");
  }

  void doTopReviewersTest(String graphqlDocument, String responseDocument, String sort, String cursor) throws IOException {
    Map<String, Object> params = new HashMap<>();
    params.put("limit", 5);
    params.put("cursor", cursor);
    params.put("sort", sort);
    tester
      .document(TestUtils.documentFor(graphqlDocument))
      .variable("params", params)
      .execute()
      .path("$")
      .matchesJson(TestUtils.expectedResponseFor(responseDocument));
  }
}
