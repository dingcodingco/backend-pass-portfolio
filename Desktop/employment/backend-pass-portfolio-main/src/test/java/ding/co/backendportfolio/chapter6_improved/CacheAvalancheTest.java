package ding.co.backendportfolio.chapter6_improved;

import com.fasterxml.jackson.core.JsonProcessingException;
import ding.co.backendportfolio.chapter6._2_remote_redis_practice.AbstractBookTest;
import ding.co.backendportfolio.chapter6._3_remote_redis_real_example.CacheAvalancheService;
import ding.co.backendportfolio.chapter6_improved._3_remote_redis_real_example.ImprovedCacheAvalancheService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;

@Slf4j
class CacheAvalancheTest extends AbstractBookTest {

    public static final int LOOP_COUNT = 300;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private CacheAvalancheService cacheAvalancheService;

    @Autowired
    private ImprovedCacheAvalancheService improvedCacheAvalancheService;

    // 테스트용 Book ID의 범위
    private final List<Long> ids = LongStream.rangeClosed(1, 1000)
            .boxed()
            .toList();

    @AfterEach
    void setUp() {
        List<String> keys = ids.stream().map(String::valueOf).toList();
        redisTemplate.delete(keys);
    }

    @Test
    @DisplayName("No Jitter")
    void noJitter() throws Exception {
        ExecutorService executor = getExecutorService();
        final CountDownLatch latch = new CountDownLatch(LOOP_COUNT * ids.size());

        for (int i = 0; i < LOOP_COUNT; i++) {
            for (long id : ids) {
                executor.submit(() -> {
                    try {
                        cacheAvalancheService.findBookByIdWithNoJitter(id);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            TimeUnit.MILLISECONDS.sleep(100);
        }

        latch.await();
        cacheAvalancheService.logDbCall();
        cacheAvalancheService.reset();
        Thread.sleep(3000);
    }

    @Test
    @DisplayName("Jitter 적용")
    void withJitter() throws Exception {
        ExecutorService executor = getExecutorService();
        final CountDownLatch latch = new CountDownLatch(LOOP_COUNT * ids.size());

        for (int i = 0; i < LOOP_COUNT; i++) {
            for (long id : ids) {
                executor.submit(() -> {
                    try {
                        improvedCacheAvalancheService.findBookByIdWithJitter(id);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    } finally {
                        latch.countDown();
                    }
                });
            }
            TimeUnit.MILLISECONDS.sleep(100);
        }
        latch.await();
        improvedCacheAvalancheService.logDbCall();
        improvedCacheAvalancheService.reset();
        Thread.sleep(3000);
    }

    private @NotNull ExecutorService getExecutorService() {
        // warm-up
        ExecutorService executor = Executors.newFixedThreadPool(200);
        for (int i = 0; i < 30; i++) {
            for (Long id : ids) {
                executor.submit(() -> Math.sin(id));
            }
        }
        return executor;
    }
}
