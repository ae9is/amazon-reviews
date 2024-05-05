package amazonrev.sales;

import amazonrev.util.Pagination;
import amazonrev.util.exception.BadRequestException;

public class SalesPagination extends Pagination {
  SalesSort sort = SalesSort.RATING_NUMBER;

  public SalesPagination() {
    super();
  }

  public SalesPagination(String cursor, Integer limit, SalesSort sort) {
    super(cursor, limit);
    this.sort = sort;
  }

  public void setSort(SalesSort sort) {
    if (sort != null) {
      this.sort = sort;
    }
  }

  public SalesSort getSort() {
    return this.sort;
  }

  public Object getSortCursor() {
    if (sort.equals(SalesSort.RATING_NUMBER)) {
      return getNthCursorAs(Double.valueOf(0), 1, CursorDirection.DESC);
    } else if (sort.equals(SalesSort.VERIF_PURCHASE)) {
      return getNthCursorAs(Double.valueOf(0), 1, CursorDirection.DESC);
    }
    throw new BadRequestException("Not implemented for " + sort.toString());
  }
}
