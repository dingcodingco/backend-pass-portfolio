package ding.co.backendportfolio.chapter5improved._4_async_operation;

import ding.co.backendportfolio.chapter5._4_async_operation.Movie;
import ding.co.backendportfolio.chapter5._4_async_operation.MovieDetailResult;
import ding.co.backendportfolio.chapter5._4_async_operation.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class MovieImprovedService {

    private final MovieRepository movieRepository;
    private final MovieImprovedBookingClient bookingClient;
    private final MovieImprovedRecommendClient recommendClient;
    private final MovieImprovedRankingClient rankingClient;
    private final MovieImprovedInterestEmitter interestEmitter;
    private final MovieImprovedPushNotificationClient moviePushNotificationClient;

    // 영화 상세 조회
    public MovieDetailResult getMovieDetail(Long userNo, Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow();

        CompletableFuture<Integer> ranking = rankingClient.getRanking(movieId);
        CompletableFuture<Boolean> isAvailable = bookingClient.isAvailable(movieId);
        CompletableFuture<List<Long>> recommendedMovieIds = recommendClient.getRecommendedMovieIds(userNo);

        interestEmitter.emitUserInterest(userNo, movieId);

        return MovieDetailResult.builder()
                .movieId(movie.getId())
                .title(movie.getTitle())
                .ranking(ranking.join())
                .isAvailable(isAvailable.join())
                .recommendedMovieIds(recommendedMovieIds.join())
                .build();
    }

    // 예매 확정 푸시 알림 전송
    public void sendBookingConfirmationPush(Map<Long, Long> bookingIdByUserNo) {
        List<CompletableFuture<Void>> futures = bookingIdByUserNo.entrySet()
                .stream()
                .map(entry -> moviePushNotificationClient.sendBookingConfirmation(entry.getKey(), entry.getValue())
                        .exceptionally(ex -> {
                            log.warn("푸시 알림 실패", ex);
                            return null;
                        }))
                .toList();

        // 모든 비동기 작업이 끝날 때까지 대기
        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();
    }
}
