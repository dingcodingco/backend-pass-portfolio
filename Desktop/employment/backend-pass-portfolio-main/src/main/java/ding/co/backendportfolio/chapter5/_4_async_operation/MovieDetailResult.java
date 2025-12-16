package ding.co.backendportfolio.chapter5._4_async_operation;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MovieDetailResult {
    private final long movieId;
    private final String title;
    private final int ranking;
    private final boolean isAvailable;
    private final List<Long> recommendedMovieIds;

    @Builder
    public MovieDetailResult(Long movieId, String title, int ranking, boolean isAvailable, List<Long> recommendedMovieIds) {
        this.movieId = movieId;
        this.title = title;
        this.ranking = ranking;
        this.isAvailable = isAvailable;
        this.recommendedMovieIds = recommendedMovieIds;
    }
}
