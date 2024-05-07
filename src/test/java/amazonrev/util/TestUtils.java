package amazonrev.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TestUtils {

  public static String documentFor(String documentName) throws IOException {
    File queryFile = new File("src/test/resources/graphql-test/" + documentName + ".graphql");
    String query = Files.readString(queryFile.toPath());
    return query;
  }

  public static String expectedResponseFor(String documentName) throws IOException {
    File expectedResponseFile = new File("src/test/resources/graphql-test/" + documentName + ".json");
    String expectedResponse = Files.readString(expectedResponseFile.toPath());
    return expectedResponse;
  }
}
