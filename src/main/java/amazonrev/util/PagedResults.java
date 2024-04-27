package amazonrev.util;

import java.util.List;

public record PagedResults<T>(List<T> list, String cursor) {}
