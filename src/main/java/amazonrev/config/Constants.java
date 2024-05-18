package amazonrev.config;

import java.util.Map;

public class Constants {
  private static final Map<String, String> vars = System.getenv();
  private static final String modelApiUrl = vars.get("MODEL_API_URL") != null ? vars.get("MODEL_API_URL") : "http://localhost:5000";
  private static final String embeddingCreatePath = "/v1/embedding/create";

  public static String getModelApiUrl() {
    return modelApiUrl;
  }

  public static String getEmbeddingCreatePath() {
    return embeddingCreatePath;
  }
}
