package amazonrev.review;

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
public class ReviewTests {

  @LocalServerPort
  int randomServerPort;

  @Autowired
  HttpGraphQlTester tester;

  @Autowired
  WebTestClient client;

  @Test
  void shouldGetReviewById() throws IOException {
    String document = "review/reviewById";
    tester
      .document(TestUtils.documentFor(document))
      .variable("id", "58")
      .execute()
      .path("$")
      .matchesJson(TestUtils.expectedResponseFor(document));
  }

  @Test
  void shouldGetReviewsByNewest() throws IOException {
    String graphqlDocument = "review/reviews";
    String responseDocument = graphqlDocument + "ByNewest";
    doReviewsTest(graphqlDocument, responseDocument, "NEWEST", "Mjk3MjYzNR8yMDIzLTA4LTIxVDE5OjE4OjE2LjE3NVo=");
  }

  @Test
  void shouldGetReviewsByVotes() throws IOException {
    String graphqlDocument = "review/reviews";
    String responseDocument = graphqlDocument + "ByVotes";
    doReviewsTest(graphqlDocument, responseDocument, "VOTES", "MTU2NDM2Nx8xMjk=");
  }

  void doReviewsTest(String graphqlDocument, String responseDocument, String sort, String cursor) throws IOException {
    Map<String, Object> params = new HashMap<>();
    params.put("limit", 3);
    params.put("cursor", cursor);
    params.put("sort", sort);
    tester
      .document(TestUtils.documentFor(graphqlDocument))
      .variable("asin", "B0BSGM6CQ9")
      .variable("params", params)
      .execute()
      .path("$")
      .matchesJson(TestUtils.expectedResponseFor(responseDocument));
  }

}
