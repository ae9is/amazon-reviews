CREATE TABLE IF NOT EXISTS itemvideo (
  itemvideo_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
  title TEXT,
  uri TEXT,
  creator_handle TEXT
);
COPY itemvideo FROM '/export/itemvideo.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;