package amazonrev.recommend;

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
public class RecommendTests {

  @LocalServerPort
  int randomServerPort;

  @Autowired
  HttpGraphQlTester tester;

  @Autowired
  WebTestClient client;

  @Test
  void shouldGetItemSummariesByQuery() throws IOException {
    String document = "recommend/itemSummariesLimit";
    String queryText = "I need a quiet instrument that outputs to a MIDI interface";
    int limit = 5;
    tester
      .document(TestUtils.documentFor(document))
      .variable("queryText", queryText)
      .variable("limit", limit)
      .execute()
      .path("$")
      .matchesJson(TestUtils.expectedResponseFor(document));
  }

  @Test
  void shouldGetItemSummariesByQueryPaginated() throws IOException {
    String document = "recommend/itemSummariesPage";
    String queryText = "Something to spice up my wall";
    String cursor = "MjE5NzQfLTAuODgxNTcyNDI1MzY1NDQ4";
    int limit = 3;
    Map<String, Object> params = new HashMap<>();
    params.put("cursor", cursor);
    params.put("limit", limit);
    // Note: the cursor implementation for this endpoint contains a computed floating point value,
    //  so we can't check against an expected cursor value in this test.
    tester
      .document(TestUtils.documentFor(document))
      .variable("queryText", queryText)
      .variable("params", params)
      .execute()
      .path("$")
      .matchesJson(TestUtils.expectedResponseFor(document));
  }
}
