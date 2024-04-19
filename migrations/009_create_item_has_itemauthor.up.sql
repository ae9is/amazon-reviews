CREATE TABLE IF NOT EXISTS item_has_itemauthor (
  item_id BIGINT,
  itemauthor_id BIGINT,
  PRIMARY KEY (item_id, itemauthor_id),
  CONSTRAINT fk_item_has_itemauthor_item FOREIGN KEY(item_id) REFERENCES item(item_id),
  CONSTRAINT fk_item_has_itemauthor_itemauthor FOREIGN KEY(itemauthor_id) REFERENCES itemauthor(itemauthor_id)
);
COPY item_has_itemauthor FROM '/export/item_has_itemauthor.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;