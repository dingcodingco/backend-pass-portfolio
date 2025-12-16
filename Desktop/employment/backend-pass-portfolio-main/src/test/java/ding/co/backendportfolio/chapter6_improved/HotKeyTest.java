package ding.co.backendportfolio.chapter6_improved;

import com.fasterxml.jackson.core.JsonProcessingException;
import ding.co.backendportfolio.chapter6._2_remote_redis_practice.AbstractBookTest;
import ding.co.backendportfolio.chapter6._3_remote_redis_real_example.HotKeyService;
import ding.co.backendportfolio.chapter6_improved._3_remote_redis_real_example.ImprovedHotKeyService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
class HotKeyTest extends AbstractBookTest {

    private static final int LOOP_COUNT = 300;
    private static final Long HOT_KEY_ID = 1L;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private HotKeyService hotKeyService;

    @Autowired
    private ImprovedHotKeyService improvedHotKeyService;

    @AfterEach
    void tearDown() {
        redisTemplate.delete(HOT_KEY_ID.toString());
    }

    @Test
    @DisplayName("Hot Key - 락 없이 DB 요청 폭주")
    void hotKeyWithoutLock() throws Exception {
        ExecutorService executor = getExecutorService();
        CountDownLatch readyLatch = new CountDownLatch(LOOP_COUNT);
        CountDownLatch fireLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(LOOP_COUNT);


        for (int i = 0; i < LOOP_COUNT; i++) {
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    fireLatch.await(); // 모든 스레드가 준비될 때까지 대기
                    hotKeyService.findBookByIdWithoutLock(HOT_KEY_ID);
                } catch (JsonProcessingException | InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 모든 스레드 준비 후 동시에 발사
        readyLatch.await();
        fireLatch.countDown();

        doneLatch.await();

        hotKeyService.logDbCall();
        hotKeyService.reset();
        sleep(3000);
    }

    @Test
    @DisplayName("Hot Key - 락 적용 후 DB 요청 제어")
    void hotKeyWithLock() throws Exception {
        ExecutorService executor = getExecutorService();
        CountDownLatch readyLatch = new CountDownLatch(LOOP_COUNT);
        CountDownLatch fireLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(LOOP_COUNT);

        for (int i = 0; i < LOOP_COUNT; i++) {
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    fireLatch.await(); // 모든 스레드가 준비될 때까지 대기
                    improvedHotKeyService.findBookByIdWithLock(HOT_KEY_ID);
                } catch (JsonProcessingException | InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 모든 스레드 준비 후 동시에 발사
        readyLatch.await();
        fireLatch.countDown();

        doneLatch.await();

        improvedHotKeyService.logDbCall();
        improvedHotKeyService.reset();
        sleep(3000);
    }

    private @NotNull ExecutorService getExecutorService() {
        // warm-up
        ExecutorService executor = Executors.newFixedThreadPool(300);
        for (int i = 0; i < 300; i++) {
            executor.submit(() -> {
                sleep(10);
                Math.sin(LocalTime.now().toNanoOfDay());
            });
        }
        return executor;
    }

    /**
     * Thread.sleep() 대신 예외 처리를 간소화하기 위한 메서드
     */
    private void sleep(long ms) {
        try {
            TimeUnit.MILLISECONDS.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
