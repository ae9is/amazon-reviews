package amazonrev.sales;

import amazonrev.item.ItemSummary;

public record ItemSales(
    ItemSummary item,
    Long numVerifiedReviews,
    Double sales) {
}
