COPY item_has_itemvideo to '/export/item_has_itemvideo.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
DROP TABLE IF EXISTS item_has_itemvideo;