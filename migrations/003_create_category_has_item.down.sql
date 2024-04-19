COPY category_has_item TO '/export/category_has_item.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
DROP TABLE IF EXISTS category_has_item;