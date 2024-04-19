CREATE TABLE IF NOT EXISTS user_has_review (
  user_id BIGINT,
  review_id BIGINT,
  PRIMARY KEY (user_id, review_id),
  CONSTRAINT fk_user_has_review_user FOREIGN KEY(user_id) REFERENCES users(user_id),
  CONSTRAINT fk_user_has_review_review FOREIGN KEY(review_id) REFERENCES review(review_id)
);
COPY user_has_review FROM '/export/user_has_review.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;