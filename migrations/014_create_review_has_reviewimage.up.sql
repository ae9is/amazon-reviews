CREATE TABLE IF NOT EXISTS review_has_reviewimage (
  review_id BIGINT,
  reviewimage_id BIGINT,
  PRIMARY KEY (review_id, reviewimage_id),
  CONSTRAINT fk_review_has_reviewimage_review FOREIGN KEY(review_id) REFERENCES review(review_id),
  CONSTRAINT fk_review_has_reviewimage_reviewimage FOREIGN KEY(reviewimage_id) REFERENCES reviewimage(reviewimage_id)
);
COPY review_has_reviewimage FROM '/export/review_has_reviewimage.csv' WITH CSV DELIMITER E'\x1e' QUOTE E'\x1f' NULL AS '' HEADER;