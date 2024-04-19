COPY itemvideo to '/export/itemvideo.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
DROP TABLE IF EXISTS itemvideo;