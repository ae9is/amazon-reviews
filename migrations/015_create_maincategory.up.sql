CREATE OR REPLACE VIEW maincategory AS
  SELECT
    DISTINCT(category.category_id),
    category.label
  FROM
    category,
    category_has_item as chi
  WHERE
    chi.category_id = category.category_id
    AND chi.is_item_main_category = true
    ORDER BY category.label ASC;