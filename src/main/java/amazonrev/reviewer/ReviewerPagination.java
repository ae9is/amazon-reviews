package amazonrev.reviewer;

import amazonrev.util.Pagination;
import amazonrev.util.exception.BadRequestException;

public class ReviewerPagination extends Pagination {
  ReviewerSort sort = ReviewerSort.VOTES;

  public ReviewerPagination() {
    super();
  }

  public ReviewerPagination(String cursor, Integer limit, ReviewerSort sort) {
    super(cursor, limit);
    this.sort = sort;
  }

  public void setSort(ReviewerSort sort) {
    if (sort != null) {
      this.sort = sort;
    }
  }

  public ReviewerSort getSort() {
    return this.sort;
  }

  public Object getSortCursor() {
    if (sort.equals(ReviewerSort.VOTES)) {
      return getNthCursorAs(Long.valueOf(0), 1, CursorDirection.DESC);
    } else if (sort.equals(ReviewerSort.NUM_REVIEWS)) {
      return getNthCursorAs(Long.valueOf(0), 1, CursorDirection.DESC);
    }
    throw new BadRequestException("Not implemented for " + sort.toString());
  }
}
