CREATE TABLE IF NOT EXISTS item_has_itemimage (
  item_id BIGINT,
  itemimage_id BIGINT,
  PRIMARY KEY (item_id, itemimage_id),
  CONSTRAINT fk_item_has_itemimage_item FOREIGN KEY(item_id) REFERENCES item(item_id),
  CONSTRAINT fk_item_has_itemimage_itemimage FOREIGN KEY(itemimage_id) REFERENCES itemimage(itemimage_id)
);
COPY item_has_itemimage FROM '/export/item_has_itemimage.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;