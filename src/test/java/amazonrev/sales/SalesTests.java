package amazonrev.sales;

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
public class SalesTests {
  @LocalServerPort
  int randomServerPort;

  @Autowired
  HttpGraphQlTester tester;

  @Autowired
  WebTestClient client;

  @Test
  void shouldGetTopItemSalesByRatingNumber() throws IOException {
    String graphqlDocument = "sales/topItemSales";
    String responseDocument = graphqlDocument + "ByRatingNumber";
    doTopReviewersTest(graphqlDocument, responseDocument, "RATING_NUMBER", "NjAxNh82MDkwNjMwLjA=");
  }

  @Test
  void shouldGetTopItemSalesByVerifPurchase() throws IOException {
    String graphqlDocument = "sales/topItemSales";
    String responseDocument = graphqlDocument + "ByVerifPurchase";
    doTopReviewersTest(graphqlDocument, responseDocument, "VERIF_PURCHASE", null);
  }

  void doTopReviewersTest(String graphqlDocument, String responseDocument, String sort, String cursor) throws IOException {
    Map<String, Object> params = new HashMap<>();
    params.put("limit", 3);
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
