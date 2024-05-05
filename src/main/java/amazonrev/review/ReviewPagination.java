package amazonrev.review;

import java.time.OffsetDateTime;

import amazonrev.util.Pagination;
import amazonrev.util.exception.BadRequestException;

public class ReviewPagination extends Pagination {
  ReviewSort sort = ReviewSort.NEWEST;

  public ReviewPagination() {
    super();
  }

  public ReviewPagination(String cursor, Integer limit, ReviewSort sort) {
    super(cursor, limit);
    this.sort = sort;
  }

  public void setSort(ReviewSort sort) {
    if (sort != null) {
      this.sort = sort;
    }
  }

  public ReviewSort getSort() {
    return this.sort;
  }

  public Object getSortCursor() {
    if (sort.equals(ReviewSort.VOTES)) {
      Long arbitrary = Long.valueOf(0);
      return getNthCursorAs(arbitrary, 1, CursorDirection.DESC);
    } else if (sort.equals(ReviewSort.NEWEST)) {
      OffsetDateTime arbitrary = OffsetDateTime.MIN;
      return getNthCursorAs(arbitrary, 1, CursorDirection.DESC);
    }
    throw new BadRequestException("Not implemented for " + sort.toString());
  }
}
