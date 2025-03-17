package ding.co.backendportfolio.chapter5.async_operation;

import lombok.Getter;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Getter
public class MovieDetailResult {
    private final long movieId;
    private final String title;
    private final int ranking;
    private final boolean isAvailable;
    private final List<Long> recommendedMovieIds;

    public MovieDetailResult(Long movieId, String title, int ranking, boolean isAvailable, List<Long> recommendedMovieIds) {
        requireNonNull(movieId, "movieId is null");
        requireNonNull(recommendedMovieIds, "recommendedMovieIds is null");

        this.movieId = movieId;
        this.title = title;
        this.ranking = ranking;
        this.isAvailable = isAvailable;
        this.recommendedMovieIds = recommendedMovieIds;
    }
}
