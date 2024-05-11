package amazonrev.item;

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
public class ItemTests {

  @LocalServerPort
  int randomServerPort;

  @Autowired
  HttpGraphQlTester tester;

  @Autowired
  WebTestClient client;

  /*
  @BeforeEach
  void setup(ApplicationContext context) {
    // Alternative to @Autowired, bind to given server
    client = WebTestClient.bindToServer().baseUrl("http://localhost:" + randomServerPort + "/graphql").build();
    tester = HttpGraphQlTester.create(client);
  }
  */

  @Test
  void shouldGetItemById() throws IOException {
    String document = "item/itemById";
    tester
      .document(TestUtils.documentFor(document))
      .variable("id", "0")
      .execute()
      .path("$")
      .matchesJson(TestUtils.expectedResponseFor(document));
  }

  @Test
  void shouldGetItemByAsin() throws IOException {
    String document = "item/itemByAsin";
    tester
      .document(TestUtils.documentFor(document))
      .variable("asin", "B01M4HO6RK")
      .execute()
      .path("$")
      .matchesJson(TestUtils.expectedResponseFor(document));
  }

  @Test
  void shouldGetItemSummariesByAvgRating() throws IOException {
    String graphqlDocument = "item/itemSummaries";
    String responseDocument = graphqlDocument + "ByAvgRating";
    doSummariesTest(graphqlDocument, responseDocument, "AVG_RATING", "MjEzNTc5HzUuMA==");
  }

  @Test
  void shouldGetItemSummariesByRatingNumber() throws IOException {
    String graphqlDocument = "item/itemSummaries";
    String responseDocument = graphqlDocument + "ByRatingNumber";
    doSummariesTest(graphqlDocument, responseDocument, "RATING_NUMBER", "OTE1OTMfNjg3MDg=");
  }

  @Test
  void shouldGetItemSummariesByLowPrice() throws IOException {
    String graphqlDocument = "item/itemSummaries";
    String responseDocument = graphqlDocument + "ByLowPrice";
    doSummariesTest(graphqlDocument, responseDocument, "LOW_PRICE", "MTIzMDg2HzAuMDE=");
  }

  void doSummariesTest(String graphqlDocument, String responseDocument, String sort, String cursor) throws IOException {
    Map<String, Object> params = new HashMap<>();
    params.put("limit", 3);
    params.put("cursor", cursor);
    params.put("sort", sort);
    tester
      .document(TestUtils.documentFor(graphqlDocument))
      .variable("category", "Musical Instruments")
      .variable("params", params)
      .execute()
      .path("$")
      .matchesJson(TestUtils.expectedResponseFor(responseDocument));
  }

}
