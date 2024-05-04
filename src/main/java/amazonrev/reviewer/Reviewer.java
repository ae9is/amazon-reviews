package amazonrev.reviewer;

public record Reviewer(
    User user,
    Long numReviews,
    Long votes) {
}
