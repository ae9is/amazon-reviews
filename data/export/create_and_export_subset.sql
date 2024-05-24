-- For preparing test data from database with "Musical Instruments" category data loaded

DROP TABLE IF EXISTS item_sub;
DROP TABLE IF EXISTS item_embed_sub;
DROP TABLE IF EXISTS category_has_item_sub;
DROP TABLE IF EXISTS category_sub;
DROP TABLE IF EXISTS item_has_itemauthor_sub;
DROP TABLE IF EXISTS item_has_itemimage_sub;
DROP TABLE IF EXISTS item_has_itemvideo_sub;
DROP TABLE IF EXISTS itemauthor_sub;
DROP TABLE IF EXISTS itemimage_sub;
DROP TABLE IF EXISTS itemvideo_sub;
DROP TABLE IF EXISTS review_sub;
DROP TABLE IF EXISTS review_has_reviewimage_sub;
DROP TABLE IF EXISTS reviewimage_sub;
DROP TABLE IF EXISTS user_has_review_sub;
DROP TABLE IF EXISTS users_sub;

CREATE TABLE item_sub AS SELECT * FROM item WHERE item_id IN (0, 45232, 24415, 23349, 6016, 106727, 8091, 51625, 213586, 213580, 213579, 213578, 213572, 213571, 6796, 9445, 91593, 3252, 93144, 15099, 115917, 153111, 96802, 112674, 123086, 157796, 74104, 13297, 21974, 127498, 33374, 136816, 58100, 125318, 127781, 46486, 22051, 57668, 157833, 114629, 112424, 209669, 90173, 9368);
CREATE TABLE item_embed_sub AS SELECT * FROM item_embed WHERE item_id IN (0, 45232, 24415, 23349, 6016, 106727, 8091, 51625, 213586, 213580, 213579, 213578, 213572, 213571, 6796, 9445, 91593, 3252, 93144, 15099, 115917, 153111, 96802, 112674, 123086, 157796, 74104, 13297, 21974, 127498, 33374, 136816, 58100, 125318, 127781, 46486, 22051, 57668, 157833, 114629, 112424, 209669, 90173, 9368);
CREATE TABLE category_has_item_sub AS SELECT * FROM category_has_item WHERE item_id IN (0, 45232, 24415, 23349, 6016, 106727, 8091, 51625, 213586, 213580, 213579, 213578, 213572, 213571, 6796, 9445, 91593, 3252, 93144, 15099, 115917, 153111, 96802, 112674, 123086, 157796, 74104, 13297, 21974, 127498, 33374, 136816, 58100, 125318, 127781, 46486, 22051, 57668, 157833, 114629, 112424, 209669, 90173, 9368);
CREATE TABLE category_sub AS SELECT cat.* FROM category AS cat, category_has_item_sub AS rel WHERE cat.category_id = rel.category_id GROUP BY cat.category_id;
CREATE TABLE item_has_itemauthor_sub AS SELECT auth.* FROM item_has_itemauthor AS auth, item_sub WHERE auth.item_id = item_sub.item_id;
CREATE TABLE item_has_itemimage_sub AS SELECT img.* FROM item_has_itemimage AS img, item_sub WHERE img.item_id = item_sub.item_id;
CREATE TABLE item_has_itemvideo_sub AS SELECT vid.* FROM item_has_itemvideo AS vid, item_sub WHERE vid.item_id = item_sub.item_id;
CREATE TABLE itemauthor_sub AS SELECT i.* FROM itemauthor AS i, item_has_itemauthor_sub AS rel WHERE i.itemauthor_id = rel.itemauthor_id;
CREATE TABLE itemimage_sub AS SELECT i.* FROM itemimage AS i, item_has_itemimage_sub AS rel WHERE i.itemimage_id = rel.itemimage_id;
CREATE TABLE itemvideo_sub AS SELECT i.* FROM itemvideo AS i, item_has_itemvideo_sub AS rel WHERE i.itemvideo_id = rel.itemvideo_id;
CREATE TABLE review_sub AS SELECT * FROM review WHERE review_id IN (58, 1303539, 321591, 1564367, 506788, 1563864, 771801, 2977979, 2948320, 2972635, 2941096, 2929351, 2951451);
CREATE TABLE review_has_reviewimage_sub AS SELECT rhi.* FROM review_has_reviewimage AS rhi, review_sub AS rev WHERE rhi.review_id = rev.review_id;
CREATE TABLE reviewimage_sub AS SELECT img.* FROM reviewimage AS img, review_has_reviewimage_sub AS rhi WHERE img.reviewimage_id = rhi.reviewimage_id;
CREATE TABLE user_has_review_sub AS SELECT uhr.* FROM user_has_review AS uhr, review_sub AS rev WHERE uhr.review_id = rev.review_id;
CREATE TABLE users_sub AS SELECT users.* FROM users, user_has_review_sub AS uhr WHERE users.user_id = uhr.user_id GROUP BY users.user_id;

-- Data from reviews data parser requires some more postprocessing in SQL and this reverts that
ALTER TABLE review_sub ADD COLUMN time_stamp_millis BIGINT;
UPDATE review_sub SET time_stamp_millis = EXTRACT(EPOCH FROM time_stamp) * 1000;
ALTER TABLE review_sub DROP COLUMN time_stamp;

COPY category_sub TO '/export/category_sub.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
COPY item_sub TO '/export/item_sub.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
COPY category_has_item_sub TO '/export/category_has_item_sub.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
COPY itemimage_sub to '/export/itemimage_sub.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
COPY itemvideo_sub to '/export/itemvideo_sub.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
COPY itemauthor_sub to '/export/itemauthor_sub.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
COPY item_has_itemimage_sub to '/export/item_has_itemimage_sub.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
COPY item_has_itemvideo_sub to '/export/item_has_itemvideo_sub.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
COPY item_has_itemauthor_sub to '/export/item_has_itemauthor_sub.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
COPY users_sub TO '/export/users_sub.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
-- Export columns in the order the import migration SQL script expects
COPY review_sub (review_id, rating, title, bodytext, asin, parent_asin, time_stamp_millis, helpful_vote, verified_purchase) to '/export/review_sub.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
COPY reviewimage_sub to '/export/reviewimage_sub.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
COPY user_has_review_sub to '/export/user_has_review_sub.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
COPY review_has_reviewimage_sub to '/export/review_has_reviewimage_sub.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
COPY item_embed_sub TO '/export/item_embed_sub.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;
