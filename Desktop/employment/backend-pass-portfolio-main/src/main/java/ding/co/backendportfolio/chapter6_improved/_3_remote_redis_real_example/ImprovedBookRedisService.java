package ding.co.backendportfolio.chapter6_improved._3_remote_redis_real_example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ding.co.backendportfolio.chapter6._1_practice.Book;
import ding.co.backendportfolio.chapter6._1_practice.BookRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class ImprovedBookRedisService {

    private static final String NULL_VALUE = "__NULL__"; // null을 대체할 객체

    private final BookRepository bookRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    // Null 값 캐싱을 통한 Cache Penetration 방지
    public Book findBookByIdWithNullCache(Long id) throws JsonProcessingException {
        // 1. 캐시에서 먼저 조회
        String cachedResult = redisTemplate.opsForValue().get(id.toString());

        // 2. 캐시에 존재하지만 NULL_VALUE가 저장되어 있다면 (데이터가 없음을 의미)
        if (cachedResult != null && cachedResult.equals(NULL_VALUE)) {
            meterRegistry.counter("cache.hit", "service", "ImprovedBookRedisService").increment();
            return null;
        }

        // 3. 캐시에 존재하지만 NULL_VALUE 는 아닌 경우
        if (cachedResult != null) {
            meterRegistry.counter("cache.hit", "service", "ImprovedBookRedisService").increment();
            return objectMapper.readValue(cachedResult, new TypeReference<>() {
            });
        }

        meterRegistry.counter("cache.miss", "service", "ImprovedBookRedisService").increment();

        // 3. 캐시에 아예 없는 경우 DB에서 조회
        Book bookFromDb = bookRepository.findById(id).orElse(null);
        meterRegistry.counter("db.select", "service", "ImprovedBookRedisService").increment();

        // 5. DB 조회 결과가 null이더라도 캐시에 저장 (짧은 TTL 적용)
        if (bookFromDb == null) {
            redisTemplate.opsForValue().set(id.toString(), NULL_VALUE, 5, TimeUnit.MINUTES);
            return bookFromDb;
        }

        String serializedBookFromDb = objectMapper.writeValueAsString(bookFromDb);
        redisTemplate.opsForValue().set(id.toString(), serializedBookFromDb, 30, TimeUnit.MINUTES);
        return bookFromDb;
    }
}
