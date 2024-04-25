package amazonrev.item;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Video(
  Long id,
  String title,
  String url,
  @JsonProperty("user_id")
  String creatorHandle
) {}
