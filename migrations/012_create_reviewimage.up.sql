CREATE TABLE IF NOT EXISTS reviewimage (
  reviewimage_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  small_image_url TEXT,
  medium_image_url TEXT,
  large_image_url TEXT,
  attachment_type TEXT
);
COPY reviewimage FROM '/export/reviewimage.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;