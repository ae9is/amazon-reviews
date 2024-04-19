CREATE TABLE IF NOT EXISTS item (
  item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  title TEXT,
  subtitle TEXT,
  average_rating NUMERIC(2, 1),
  rating_number BIGINT,
  features TEXT[],
  descriptions TEXT[],
  price NUMERIC,
  store TEXT,
  details JSONB,
  parent_asin TEXT
);
COPY item FROM '/export/item.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
