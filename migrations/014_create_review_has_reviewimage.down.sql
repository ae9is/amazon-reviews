COPY review_has_reviewimage to '/export/review_has_reviewimage.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
DROP TABLE IF EXISTS review_has_reviewimage;