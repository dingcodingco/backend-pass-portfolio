package ding.co.backendportfolio.chapter6._2_remote_redis_practice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ding.co.backendportfolio.chapter6._1_practice.Book;
import ding.co.backendportfolio.chapter6._1_practice.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookPracticeService {

    private static final String CACHE_KEY_PREFIX = "remote-cache-practice";

    private final BookRepository bookRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public Book findBookById(Long id) throws JsonProcessingException {
        String cacheKey = CACHE_KEY_PREFIX + ":" + id;

        String serialized = redisTemplate.opsForValue().get(cacheKey);
        if (serialized != null) {
            log.info(">> 1. 캐시 HIT - 캐시 값 반환");
            return objectMapper.readValue(serialized, Book.class);
        }

        log.info(">> 1. 캐시 MISS");

        Book book = bookRepository.findById(id).orElseThrow();
        log.info(">> 2. 캐시에 없어서 DB에서 조회");

        redisTemplate.opsForValue().set(cacheKey, objectMapper.writeValueAsString(book));
        log.info(">> 3. 캐시에 저장");

        log.info(">> 4. DB에서 조회 후 캐시에 저장하고나서 반환");
        return book;
    }
}
