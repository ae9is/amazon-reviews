CREATE TABLE IF NOT EXISTS category_has_item (
  category_id BIGINT,
  item_id BIGINT,
  is_item_main_category BOOLEAN,
  PRIMARY KEY (category_id, item_id),
  CONSTRAINT fk_category_has_item_category FOREIGN KEY(category_id) REFERENCES category(category_id),
  CONSTRAINT fk_category_has_item_item FOREIGN KEY(item_id) REFERENCES item(item_id)
);
COPY category_has_item FROM '/export/category_has_item.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;