package amazonrev.item;

import amazonrev.util.Pagination;

public class ItemPagination extends Pagination {
  ItemSort sort = ItemSort.RATING_NUMBER;

  public ItemPagination() {
    super();
  }

  public ItemPagination(String cursor, Integer limit, ItemSort sort) {
    super(cursor, limit);
    this.sort = sort;
  }

  public void setSort(ItemSort sort) {
    if (sort != null) {
      this.sort = sort;
    }
  }

  public ItemSort getSort() {
    return this.sort;
  }

  public Object getSortCursor() {
    if (sort.equals(ItemSort.RATING_NUMBER)) {
      return getNthCursorAs(Long.valueOf(0), 1, CursorDirection.DESC);
    } else if (sort.equals(ItemSort.AVG_RATING)) {
      return getNthCursorAs(Double.valueOf(0), 1, CursorDirection.DESC);
    } else if (sort.equals(ItemSort.LOW_PRICE)) {
      return getNthCursorAs(Double.valueOf(0), 1, CursorDirection.ASC);
    }
    throw new UnsupportedOperationException("Not implemented for " + sort.toString());
  }
}
