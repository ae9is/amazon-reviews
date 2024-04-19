package amazonrev.types.item;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Video(
  long id,
  String title,
  String url,
  @JsonProperty("user_id")
  String creatorHandle
) {}
