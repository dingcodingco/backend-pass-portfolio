package ding.co.backendportfolio.chapter5.async_operation.sync;

import ding.co.backendportfolio.chapter5.async_operation.Movie;
import ding.co.backendportfolio.chapter5.async_operation.MovieDetailResult;
import ding.co.backendportfolio.chapter5.async_operation.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SynchronousMovieService {

    private final MovieRepository movieRepository;
    private final SynchronousMovieBookingClient bookingClient;
    private final SynchronousMovieRecommendClient recommendClient;
    private final SynchronousMovieRankingClient rankingClient;

    private final SynchronousMovieInterestEmitter interestEmitter;

    public MovieDetailResult getMovieDetail(Long userNo, Long movieId) throws InterruptedException {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new RuntimeException("Movie not found"));

        int ranking = rankingClient.getRanking(movieId, 1L);
        boolean isAvailable = bookingClient.isAvailable(movieId, 1L);
        List<Long> recommendedMovieIds = recommendClient.getRecommendedMovieIds(userNo, 1L);

        interestEmitter.emitUserInterest(userNo, movieId, 1L);

        return new MovieDetailResult(movie.getId(), movie.getTitle(), ranking, isAvailable, recommendedMovieIds);
    }


}
