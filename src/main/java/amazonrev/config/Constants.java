package amazonrev.config;

import java.util.Map;

public class Constants {
  private static final Map<String, String> vars = System.getenv();
  private static final String modelApiUrl = vars.get("MODEL_API_URL") != null ? vars.get("MODEL_API_URL") : "http://localhost:5000";
  private static final boolean modelApiEnabled = vars.get("MODEL_API_ENABLED") != null ? Boolean.parseBoolean(vars.get("MODEL_API_ENABLED")) : false;
  private static final String embeddingCreatePath = "/v1/embedding/create";
  private static final String itemMetaDataFilename = vars.get("ITEM_META_FILE") != null ? vars.get("ITEM_META_FILE") : "data/import/meta_Musical_Instruments.jsonl";
  private static final String reviewsDataFilename = vars.get("REVIEWS_FILE") != null ? vars.get("REVIEWS_FILE") : "data/import/Musical_Instruments.jsonl";

  public static String getModelApiUrl() {
    return modelApiUrl;
  }

  public static boolean isModelApiEnabled() {
    return modelApiEnabled;
  }

  public static String getEmbeddingCreatePath() {
    return embeddingCreatePath;
  }

  public static String getItemMetaDataFilename() {
    return itemMetaDataFilename;
  }

  public static String getReviewsDataFilename() {
    return reviewsDataFilename;
  }
}
