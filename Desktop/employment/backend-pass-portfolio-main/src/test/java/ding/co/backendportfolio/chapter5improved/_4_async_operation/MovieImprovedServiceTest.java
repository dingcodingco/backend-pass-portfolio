package ding.co.backendportfolio.chapter5improved._4_async_operation;

import ding.co.backendportfolio.chapter5._4_async_operation.Movie;
import ding.co.backendportfolio.chapter5._4_async_operation.MovieRepository;
import ding.co.backendportfolio.config.IntegrationTest;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@IntegrationTest
class MovieImprovedServiceTest {

    private static final int REPEAT_COUNT = 10;
    private static final int WARM_UP_COUNT = 5;
    private static final Long USER_NO = 1L;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private MovieImprovedService movieService;

    @Autowired
    private MeterRegistry meterRegistry;

    @DisplayName("영화 상세 조회 - 비동기 실행")
    @RepeatedTest(REPEAT_COUNT)
    void testAsyncGetMovieImprovedDetail(RepetitionInfo repetitionInfo) {
        // given
        Timer timer = meterRegistry.timer("async-get-movie-detail");
        Movie movie = movieRepository.save(new Movie("Test"));
        final Long movieId = movie.getId();

        // when
        if (repetitionInfo.getCurrentRepetition() <= WARM_UP_COUNT) {
            movieService.getMovieDetail(USER_NO, movieId);
        } else {
            timer.record(() -> movieService.getMovieDetail(USER_NO, movieId));
        }

        // then
        if (repetitionInfo.getCurrentRepetition() == REPEAT_COUNT) {
            double mean = timer.mean(TimeUnit.MILLISECONDS);
            log.info("Async movie detail timer - mean={}ms", String.format("%.2f", mean));
        }
    }

    @DisplayName("영화 예매 완료 푸시 - 비동기 실행")
    @RepeatedTest(REPEAT_COUNT)
    void testAsyncSendBookingConfirmationPush(RepetitionInfo repetitionInfo) {
        // given
        Timer timer = meterRegistry.timer("async-send-booking-confirmation-push");
        Map<Long, Long> bookingIdByUserNo = new HashMap<>();
        for (long i = 1; i <= 10; i++) {
            bookingIdByUserNo.put(i, i);
        }

        // when
        if (repetitionInfo.getCurrentRepetition() <= WARM_UP_COUNT) {
            movieService.sendBookingConfirmationPush(bookingIdByUserNo);
        } else {
            timer.record(() -> movieService.sendBookingConfirmationPush(bookingIdByUserNo));
        }

        // then
        if (repetitionInfo.getCurrentRepetition() == REPEAT_COUNT) {
            double mean = timer.mean(TimeUnit.MILLISECONDS);
            log.info("Async send booking confirmation push timer - mean={}ms", String.format("%.2f", mean));
        }
    }
}