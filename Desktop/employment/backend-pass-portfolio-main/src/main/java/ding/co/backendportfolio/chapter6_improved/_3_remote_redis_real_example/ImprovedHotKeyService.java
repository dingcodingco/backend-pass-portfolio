package ding.co.backendportfolio.chapter6_improved._3_remote_redis_real_example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ding.co.backendportfolio.chapter6._1_practice.Book;
import ding.co.backendportfolio.chapter6._1_practice.BookRepository;
import ding.co.backendportfolio.chapter6._3_remote_redis_real_example.DbCallChecker;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImprovedHotKeyService {

    private static final String SERVICE_NAME = "ImprovedHotKeyService";
    private static final String NULL_VALUE = "__NULL__";

    private static final long TTL_BASE = 500;
    private static final long JITTER_RANGE = 10;

    private static final long LOCK_TIMEOUT_MS = 400;

    private static final int MAX_RETRY_COUNT = 40;
    private static final long RETRY_DELAY_MS = 5;
    private static final long RETRY_DELAY_JITTER_MS = 15;

    private final BookRepository bookRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final DistributedLockProvider distributedLockProvider;
    private final MeterRegistry meterRegistry;
    private final DbCallChecker dbCallChecker = new DbCallChecker("Hot Key + 락 적용");

    /**
     * 특정 책 정보 조회 시도
     * 1) 캐시에서 우선 확인
     * 2) 캐시에 없으면 락 획득 시도
     * 3) 락 획득 후에도 캐시를 재확인 (Double-Checked Locking)
     * 4) 최종적으로도 캐시가 없으면 DB 조회 후 캐시에 저장
     *
     * @param id 조회할 책 ID
     * @return Book 객체 (없으면 null)
     */
    public Book findBookByIdWithLock(Long id) throws JsonProcessingException {
        // 1. 캐시에서 먼저 조회
        String key = id.toString();
        String cached = redisTemplate.opsForValue().get(key);

        // 2. 캐시에 존재하지만 NULL_VALUE가 저장되어 있다면 (데이터가 없음을 의미)
        if (cached != null && cached.equals(NULL_VALUE)) {
            meterRegistry.counter("cache.hit", "service", SERVICE_NAME).increment();
            return null;
        }

        // 3. 캐시에 존재하지만 NULL_VALUE 는 아닌 경우
        if (cached != null) {
            meterRegistry.counter("cache.hit", "service", SERVICE_NAME).increment();
            return objectMapper.readValue(cached, new TypeReference<>() {
            });
        }

        meterRegistry.counter("cache.miss", "service", SERVICE_NAME).increment();

        // 3. 캐시 미스 시 분산락 획득 시도 (최대 MAX_RETRY_COUNT 번 재시도)
        String lockKey = makeLockKey(id);
        for (int retry = 0; retry < MAX_RETRY_COUNT; retry++) {
            boolean lockAcquired = distributedLockProvider.tryLock(lockKey, LOCK_TIMEOUT_MS);

            // 3-1. 락 획득 실패 시 잠깐 대기 후 다시 시도
            if (!lockAcquired) {
                log.warn(">> 경쟁 발생 !! - key={}, retry={}", lockKey, retry);
                long jitter = ThreadLocalRandom.current().nextLong(RETRY_DELAY_JITTER_MS);
                sleep(RETRY_DELAY_MS + jitter);
                continue;
            }

            try {
                // 3-2. 락을 획득한 경우
                // 동일하게 캐시를 다시 확인(Double-Checked-Locking)
                cached = redisTemplate.opsForValue().get(key);
                if (cached != null && cached.equals(NULL_VALUE)) {
                    meterRegistry.counter("cache.hit", "service", SERVICE_NAME).increment();
                    return null;
                }

                if (cached != null) {
                    meterRegistry.counter("cache.hit", "service", SERVICE_NAME).increment();
                    return objectMapper.readValue(cached, new TypeReference<>() {
                    });
                }

                // 4. 캐시가 여전히 없다면 DB에서 조회 후 캐시에 저장
                Book bookFromDb = bookRepository.findById(id).orElse(null);
                dbCallChecker.incrementDbSelectCount(); // 실제 DB 조회 횟수 증가

                if (bookFromDb == null) {
                    setCacheWithJitter(key, NULL_VALUE);
                    return null;
                }

                String serialized = objectMapper.writeValueAsString(bookFromDb);
                setCacheWithJitter(key, serialized);
                return bookFromDb;
            } finally {
                // 락 해제
                distributedLockProvider.releaseLock(lockKey);
            }
        }

        // MAX_RETRY_COUNT 회 모두 락 획득에 실패하면 예외 처리
        throw new IllegalStateException("Failed to acquire lock after " + MAX_RETRY_COUNT + " - key=" + lockKey);
    }

    /**
     * TTL에 랜덤 지터(jitter)를 적용하여 캐시 저장
     *
     * @param key   Redis에 저장할 Key
     * @param value Redis에 저장할 값
     */
    private void setCacheWithJitter(String key, String value) {
        long ttl = TTL_BASE + ThreadLocalRandom.current().nextLong(JITTER_RANGE);
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.MILLISECONDS);
    }

    /**
     * 락용 Key 생성
     */
    private String makeLockKey(Long id) {
        return "lock-book:" + id;
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

    public void logDbCall() {
        dbCallChecker.logDbCall();
    }

    public void reset() {
        dbCallChecker.reset();
    }
}
