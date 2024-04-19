COPY item_has_itemimage to '/export/item_has_itemimage.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
DROP TABLE IF EXISTS item_has_itemimage;