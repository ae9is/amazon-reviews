package amazonrev.category;

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
public class CategoryTests {

  @LocalServerPort
  int randomServerPort;

  @Autowired
  HttpGraphQlTester tester;

  @Autowired
  WebTestClient client;

  @Test
  void shouldGetMainCategoriesByName() throws IOException {
    String graphqlDocument = "category/categories";
    String responseDocument = graphqlDocument + "ByNameMain";
    doTest(graphqlDocument, responseDocument, true, "NAME", "Nh9DYW1lcmEgJiBQaG90bw==");
  }

  @Test
  void shouldGetMainCategoriesByRatingNumber() throws IOException {
    String graphqlDocument = "category/categories";
    String responseDocument = graphqlDocument + "ByRatingNumberMain";
    doTest(graphqlDocument, responseDocument, true, "RATING_NUMBER", "MR8yNDQwNQ==");
  }

  @Test
  void shouldGetMainCategoriesByItemCount() throws IOException {
    String graphqlDocument = "category/categories";
    String responseDocument = graphqlDocument + "ByItemCountMain";
    doTest(graphqlDocument, responseDocument, true, "ITEM_COUNT", "MR8z");
  }

  @Test
  void shouldGetCategoriesByName() throws IOException {
    String graphqlDocument = "category/categories";
    String responseDocument = graphqlDocument + "ByName";
    doTest(graphqlDocument, responseDocument, false, "NAME", "NB9BcnRzLCBDcmFmdHMgJiBTZXdpbmc=");
  }

  @Test
  void shouldGetCategoriesByRatingNumber() throws IOException {
    String graphqlDocument = "category/categories";
    String responseDocument = graphqlDocument + "ByRatingNumber";
    doTest(graphqlDocument, responseDocument, false, "RATING_NUMBER", "MzQfMjE2Nzgw");
  }

  @Test
  void shouldGetCategoriesByItemCount() throws IOException {
    String graphqlDocument = "category/categories";
    String responseDocument = graphqlDocument + "ByItemCount";
    doTest(graphqlDocument, responseDocument, false, "ITEM_COUNT", "MzAfMTE=");
  }

  void doTest(String graphqlDocument, String responseDocument, boolean main, String sort, String cursor) throws IOException {
    Map<String, Object> params = new HashMap<>();
    params.put("limit", 3);
    params.put("cursor", cursor);
    params.put("sort", sort);
    tester
      .document(TestUtils.documentFor(graphqlDocument))
      .variable("main", main)
      .variable("params", params)
      .execute()
      .path("$")
      .matchesJson(TestUtils.expectedResponseFor(responseDocument));
  }

}
