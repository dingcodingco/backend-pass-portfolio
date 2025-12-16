package ding.co.backendportfolio.chapter6._3_remote_redis_real_example;


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
public class BookRedisService {

    private final BookRepository bookRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private final MeterRegistry meterRegistry;

    // Cache Penetration 발생
    public Book findBookById(Long id) throws JsonProcessingException {
        // 1. 캐시에서 먼저 조회
        String cachedResult = redisTemplate.opsForValue().get(id.toString());

        // 2. 캐시에 존재하면 반환.
        if (cachedResult != null) {
            meterRegistry.counter("cache.hit", "service", "BookRedisService").increment();
            return objectMapper.readValue(cachedResult, new TypeReference<>() {
            });
        }

        meterRegistry.counter("cache.miss", "service", "BookRedisService").increment();

        // 3. DB 조회
        Book bookFromDb = bookRepository.findById(id).orElse(null);
        meterRegistry.counter("db.select", "service", "BookRedisService").increment();

        // 4. DB 에서 찾은 경우에만 캐시에 저장
        if (bookFromDb != null) {
            String serializedBookFromDb = objectMapper.writeValueAsString(bookFromDb);
            redisTemplate.opsForValue().set(id.toString(), serializedBookFromDb, 1, TimeUnit.HOURS);
        }

        return bookFromDb;
    }
}