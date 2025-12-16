package ding.co.backendportfolio.chapter5._4_async_operation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieBookingClient bookingClient;
    private final MovieRecommendClient recommendClient;
    private final MovieRankingClient rankingClient;
    private final MovieInterestEmitter interestEmitter;
    private final MoviePushNotificationClient moviePushNotificationClient;

    // 영화 상세 조회
    public MovieDetailResult getMovieDetail(Long userNo, Long movieId) {
        Movie movie = movieRepository.findById(movieId).orElseThrow();

        int ranking = rankingClient.getRanking(movieId);
        boolean isAvailable = bookingClient.isAvailable(movieId);
        List<Long> recommendedMovieIds = recommendClient.getRecommendedMovieIds(userNo);

        interestEmitter.emitUserInterest(userNo, movieId);

        return MovieDetailResult.builder()
                .movieId(movie.getId())
                .title(movie.getTitle())
                .ranking(ranking)
                .isAvailable(isAvailable)
                .recommendedMovieIds(recommendedMovieIds)
                .build();
    }

    // 예매 확정 푸시 알림 전송
    public void sendBookingConfirmationPush(Map<Long, Long> bookingIdByUserNo) {
        bookingIdByUserNo.forEach((userNo, bookingId) -> {
            moviePushNotificationClient.sendBookingConfirmation(userNo, bookingId);
        });
    }
}
