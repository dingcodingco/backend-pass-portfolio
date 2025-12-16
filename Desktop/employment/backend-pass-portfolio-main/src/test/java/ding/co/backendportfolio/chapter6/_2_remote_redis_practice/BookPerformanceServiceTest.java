package ding.co.backendportfolio.chapter6._2_remote_redis_practice;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.springframework.beans.factory.annotation.Autowired;

class BookPerformanceServiceTest extends AbstractBookTest {

    @Autowired
    private BookPerformanceService bookPerformanceService;

    /**
     * Redis 캐시 없이 DB만 조회하는 테스트.
     * 성능 측정 목적이며 20회 반복됨.
     */
    @DisplayName("DB 조회")
    @RepeatedTest(REPEATED_COUNT)
    void findBookByIdWithoutCache(RepetitionInfo repetitionInfo) {
        measureAndRecordTime("DB 조회", repetitionInfo.getCurrentRepetition(), () -> {
            bookPerformanceService.findBookByIdWithoutCache();
        });
    }

    /**
     * Redis 캐시 없이 DB만 조회하는 테스트.
     * 성능 측정 목적이며 20회 반복됨.
     */
    @DisplayName("Redis 캐시 조회 성능 테스트")
    @RepeatedTest(REPEATED_COUNT)
    void findBookByIdWithCache(RepetitionInfo repetitionInfo) {
        measureAndRecordTime("캐시 조회", repetitionInfo.getCurrentRepetition(), () -> {
            try {
                bookPerformanceService.findBookByIdWithCache();
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
