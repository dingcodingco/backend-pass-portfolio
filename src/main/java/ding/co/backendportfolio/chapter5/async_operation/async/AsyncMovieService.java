package ding.co.backendportfolio.chapter5.async_operation.async;

import ding.co.backendportfolio.chapter5.async_operation.Movie;
import ding.co.backendportfolio.chapter5.async_operation.MovieDetailResult;
import ding.co.backendportfolio.chapter5.async_operation.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class AsyncMovieService {

    private final MovieRepository movieRepository;
    private final AsyncMovieBookingClient bookingClient;
    private final AsyncMovieRecommendClient recommendClient;
    private final AsyncMovieRankingClient rankingClient;
    private final AsyncMovieInterestEmitter interestEmitter;

    public MovieDetailResult getMovieDetail(Long userNo, Long movieId) throws InterruptedException {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        CompletableFuture<Integer> ranking = rankingClient.getRanking(movieId, 1L);
        CompletableFuture<Boolean> isAvailable = bookingClient.isAvailable(movieId, 1L);
        CompletableFuture<List<Long>> recommendedMovieIds = recommendClient.getRecommendedMovieIds(userNo, 1L);

        interestEmitter.emitUserInterest(userNo, movieId, 1L);

        return new MovieDetailResult(movie.getId(), movie.getTitle(), ranking.join(), isAvailable.join(), recommendedMovieIds.join());
    }
}
