package amazonrev.recommend;

import amazonrev.util.Pagination;

/**
 * Pagination, with an additional sort by vector distance.
 */
public class VectorPagination extends Pagination {

  public VectorPagination() {
    super();
  }

  public VectorPagination(String cursor, Integer limit) {
    super(cursor, limit);
  }

  public Object getSortCursor() {
    return getNthCursorAs(Double.valueOf(0), 1, CursorDirection.ASC);
  }
}
