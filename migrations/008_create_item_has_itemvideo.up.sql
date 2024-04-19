CREATE TABLE IF NOT EXISTS item_has_itemvideo (
  item_id BIGINT,
  itemvideo_id BIGINT,
  PRIMARY KEY (item_id, itemvideo_id),
  CONSTRAINT fk_item_has_itemvideo_item FOREIGN KEY(item_id) REFERENCES item(item_id),
  CONSTRAINT fk_item_has_itemvideo_itemvideo FOREIGN KEY(itemvideo_id) REFERENCES itemvideo(itemvideo_id)
);
COPY item_has_itemvideo FROM '/export/item_has_itemvideo.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;