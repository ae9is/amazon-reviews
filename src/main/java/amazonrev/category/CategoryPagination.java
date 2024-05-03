package amazonrev.category;

import amazonrev.util.Pagination;

public class CategoryPagination extends Pagination {
  CategorySort sort = CategorySort.NAME;

  public CategoryPagination() {
    super();
  }

  public CategoryPagination(String cursor, Integer limit, CategorySort sort) {
    super(cursor, limit);
    this.sort = sort;
  }

  public void setSort(CategorySort sort) {
    if (sort != null) {
      this.sort = sort;
    }
  }

  public CategorySort getSort() {
    return this.sort;
  }

  public Object getSortCursor() {
    if (sort.equals(CategorySort.NAME)) {
      return getNthCursorAs("", 1, CursorDirection.ASC);
    } else if (sort.equals(CategorySort.RATING_NUMBER)) {
      return getNthCursorAs(Long.valueOf(0), 1, CursorDirection.DESC);
    } else if (sort.equals(CategorySort.ITEM_COUNT)) {
      return getNthCursorAs(Long.valueOf(0), 1, CursorDirection.DESC);
    }
    throw new UnsupportedOperationException("Not implemented for " + sort.toString());
  }
}
