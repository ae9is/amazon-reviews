package amazonrev.config;

import java.util.Map;

public class Constants {
  private static final Map<String, String> vars = System.getenv();
  private static final String pythonEndpoint = vars.get("PYTHON_ENDPOINT") != null ? vars.get("PYTHON_ENDPOINT") : "http://localhost:5000";
  private static final String embeddingCreatePath = "/v1/embedding/create";

  public static String getPythonEndpoint() {
    return pythonEndpoint;
  }

  public static String getEmbeddingCreatePath() {
    return embeddingCreatePath;
  }
}
