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
public class ImprovedCacheAvalancheService {

    private static final String SERVICE_NAME = "ImprovedCacheAvalancheService";
    private static final String NULL_VALUE = "__NULL__"; // null을 대체할 객체
    private static final long TTL_BASE = 1_000; // 1초
    public static final int JITTER_RANGE = 500;

    private final BookRepository bookRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;
    private final DbCallChecker dbCallChecker = new DbCallChecker("JITTER 적용");

    // 캐시에 저장할 때 TTL에 Jitter 적용
    public Book findBookByIdWithJitter(Long id) throws JsonProcessingException {
        // 1. 캐시에서 먼저 조회
        String cachedResult = redisTemplate.opsForValue().get(id.toString());

        // 2. 캐시에 존재하지만 NULL_VALUE가 저장되어 있다면 (데이터가 없음을 의미)
        if (cachedResult != null && cachedResult.equals(NULL_VALUE)) {
            meterRegistry.counter("cache.hit", "service", SERVICE_NAME).increment();
            return null;
        }

        // 3. 캐시에 존재하지만 NULL_VALUE 는 아닌 경우
        if (cachedResult != null) {
            meterRegistry.counter("cache.hit", "service", SERVICE_NAME).increment();
            return objectMapper.readValue(cachedResult, new TypeReference<>() {
            });
        }

        meterRegistry.counter("cache.miss", "service", SERVICE_NAME).increment();

        // 3. 캐시에 아예 없는 경우 DB에서 조회
        Book bookFromDb = bookRepository.findById(id).orElse(null);
        dbCallChecker.incrementDbSelectCount();

        // 4. DB 조회 결과가 null이더라도 캐시에 저장 (짧은 TTL 적용)
        if (bookFromDb == null) {
            // jitter 적용된 TTL로 저장
            // ms 단위로 저장
            long ttl = TTL_BASE + ThreadLocalRandom.current().nextLong(JITTER_RANGE);
            redisTemplate.opsForValue().set(id.toString(), NULL_VALUE, ttl, TimeUnit.MILLISECONDS);
            return bookFromDb;
        }

        String serializedBookFromDb = objectMapper.writeValueAsString(bookFromDb);
        // jitter 적용된 TTL로 저장
        // ms 단위로 저장
        long ttl = TTL_BASE + ThreadLocalRandom.current().nextLong(JITTER_RANGE);
        redisTemplate.opsForValue().set(id.toString(), serializedBookFromDb, ttl, TimeUnit.MILLISECONDS);
        return bookFromDb;
    }

    public void logDbCall() {
        dbCallChecker.logDbCall();
    }

    public void reset() {
        dbCallChecker.reset();
    }
}

