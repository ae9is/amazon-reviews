COPY item_embed TO '/export/item_embed.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
DROP TABLE IF EXISTS item_embed;