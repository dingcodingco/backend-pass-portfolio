package ding.co.backendportfolio.chapter6._2_remote_redis_practice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ding.co.backendportfolio.chapter6._1_practice.Book;
import ding.co.backendportfolio.chapter6._1_practice.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookPerformanceService {

    private final BookRepository bookRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_KEY = "remote-cache-practice";

    public List<Book> findBookByIdWithoutCache() {
        List<Book> books = bookRepository.findByNameContaining("a");
        return books;
    }

    public List<Book> findBookByIdWithCache() throws JsonProcessingException {
        String serializedCacheResult = redisTemplate.opsForValue().get(CACHE_KEY);

        if (serializedCacheResult != null) {
            return objectMapper.readValue(serializedCacheResult, new TypeReference<>() {
            });
        }

        List<Book> books = bookRepository.findByNameContaining("a");
        String serializedNew = objectMapper.writeValueAsString(books);

        redisTemplate.opsForValue().set(CACHE_KEY, serializedNew);

        return books;
    }
}