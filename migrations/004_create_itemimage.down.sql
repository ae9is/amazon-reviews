COPY itemimage to '/export/itemimage.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
DROP TABLE IF EXISTS itemimage;