package ding.co.backendportfolio.chapter6_improved;

import com.fasterxml.jackson.core.JsonProcessingException;
import ding.co.backendportfolio.chapter6._2_remote_redis_practice.AbstractBookTest;
import ding.co.backendportfolio.chapter6._3_remote_redis_real_example.BookRedisService;
import ding.co.backendportfolio.chapter6_improved._3_remote_redis_real_example.ImprovedBookRedisService;
import ding.co.backendportfolio.util.TestLogUtil;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
class CachePenetrationTest extends AbstractBookTest {

    @Autowired
    private SimpleMeterRegistry registry;
    @Autowired
    private BookRedisService bookRedisService;
    @Autowired
    private ImprovedBookRedisService improvedBookRedisService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @DisplayName("Cache Penetration 발생")
    @RepeatedTest(REPEATED_COUNT)
    void findBookByIdWithCachePenetration(RepetitionInfo repetitionInfo) {
        measureAndRecordTime("Cache Penetration 발생", repetitionInfo.getCurrentRepetition(), () -> {
            try {
                bookRedisService.findBookById(-1L);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        if (repetitionInfo.getCurrentRepetition() == REPEATED_COUNT) {
            printCacheStats("BookRedisService");
            clean();
        }
    }

    @DisplayName("NULL 캐싱 적용")
    @RepeatedTest(REPEATED_COUNT)
    void findBookByIdWithNullCache(RepetitionInfo repetitionInfo) {
        measureAndRecordTime("NULL 캐싱 적용", repetitionInfo.getCurrentRepetition(), () -> {
            try {
                improvedBookRedisService.findBookByIdWithNullCache(-1L);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        if (repetitionInfo.getCurrentRepetition() == REPEATED_COUNT) {
            printCacheStats("ImprovedBookRedisService");
            clean();
        }
    }

    private void clean() {
        TestLogUtil.CleanStart();
        Boolean delete = redisTemplate.delete("-1");
        if (delete) {
            log.info(">>> 삭제 성공");
        } else {
            log.info(">>> 삭제 실패");
        }
        TestLogUtil.CleanEnd();
    }

    private void printCacheStats(String serviceName) {
        double hit = registry.counter("cache.hit", "service", serviceName).count();
        double miss = registry.counter("cache.miss", "service", serviceName).count();
        double total = hit + miss;
        double hitRate = calculateHitRatio(hit, total);

        double dbSelect = registry.counter("db.select", "service", serviceName).count();

        log.info(">>> 캐시 매트릭[{}] 캐시 히트: {}, 미스: {}, 전체 요청: {}, 히트율: {}%", serviceName, (int) hit, (int) miss, (int) total, String.format("%.2f", hitRate));
        log.info(">>> DB 매트릭[{}] 조회 요청: {}", serviceName, dbSelect);
    }

    private static double calculateHitRatio(double hit, double total) {
        if (total == 0) {
            return 0;
        }
        return (hit / total) * 100;
    }
}