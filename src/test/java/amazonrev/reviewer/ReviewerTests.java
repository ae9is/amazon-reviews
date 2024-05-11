package amazonrev.reviewer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.graphql.test.tester.GraphQlTester.EntityList;
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
  void sanityCheckNumReviewsPerReviewerDistribution() throws IOException {
    String document = "reviewer/numReviews";
    int size = 10;
    EntityList<Bucket> res = tester
      .document(TestUtils.documentFor(document))
      .variable("numTiles", size)
      .execute()
      .errors()
      .verify()
      .path("data.numReviewsPerReviewerDistribution")
      .entityList(Bucket.class);
    res
      .hasSize(size);
    boolean monotonic = true;
    long prev = Long.MIN_VALUE;
    int i = 0;
    for (Bucket bucket : res.get()) {
      // Check that bucket tiles are ordered and equal to 1, 2, ..., size
      i++;
      int tile = bucket.tile();
      assertEquals(tile, i);
      try {
        // Check that max value is monotonically increasing
        long current = Long.parseLong(bucket.maxval().toString());
        if (prev > current) {
          monotonic = false;
          break;
        }
        prev = current;
      } catch (NumberFormatException nfe) {
        monotonic = false;
        Assertions.fail("Could not parse bucket maxval " + bucket.maxval() + " as Long");
      }
    }
    assertTrue(monotonic, "Bucket values should be monotonically increasing");
  }

  @Test
  void shouldGetTopReviewersByNumReviews() throws IOException {
    String graphqlDocument = "reviewer/topReviewers";
    String responseDocument = graphqlDocument + "ByNumReviews";
    doTopReviewersTest(graphqlDocument, responseDocument, "NUM_REVIEWS", "OTk2MTcfMjQw", null);
  }

  @Test
  void shouldGetTopReviewersByNumReviewsFor2023() throws IOException {
    String graphqlDocument = "reviewer/topReviewers";
    String responseDocument = graphqlDocument + "ByNumReviews2023";
    doTopReviewersTest(graphqlDocument, responseDocument, "NUM_REVIEWS", "MTIxODk2MR80NA==", 2023);
  }

  @Test
  void shouldGetTopReviewersByVotes() throws IOException {
    String graphqlDocument = "reviewer/topReviewers";
    String responseDocument = graphqlDocument + "ByVotes";
    doTopReviewersTest(graphqlDocument, responseDocument, "VOTES", "MTM2MzU4OR8yODky", null);
  }

  @Test
  void shouldGetTopReviewersByVotesFor2000() throws IOException {
    String graphqlDocument = "reviewer/topReviewers";
    String responseDocument = graphqlDocument + "ByVotes2000";
    doTopReviewersTest(graphqlDocument, responseDocument, "VOTES", "MTIyNDA4Nx82NQ==", 2000);
  }

  void doTopReviewersTest(String graphqlDocument, String responseDocument, String sort, String cursor, Integer year) throws IOException {
    Map<String, Object> params = new HashMap<>();
    params.put("limit", 5);
    params.put("cursor", cursor);
    params.put("sort", sort);
    tester
      .document(TestUtils.documentFor(graphqlDocument))
      .variable("year", year)
      .variable("params", params)
      .execute()
      .path("$")
      .matchesJson(TestUtils.expectedResponseFor(responseDocument));
  }
}
