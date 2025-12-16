package ding.co.backendportfolio.chapter6._1_practice;

import ding.co.backendportfolio.config.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

import java.util.Collection;

@Slf4j
@IntegrationTest
class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CacheManager cacheManager;

    private Book book = null;

    @BeforeEach
    void setUp() {
        Book book = Book.builder()
                .name("테스트")
                .isSoldOut(false)
                .build();

        bookRepository.save(book);

        this.book = book;
    }

    @AfterEach
    void clean() {
        bookRepository.deleteAllInBatch();
        Collection<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            cacheManager.getCache(cacheName).invalidate();
        }
    }


    @DisplayName("캐시 적용 테스트")
    @Test
    void cacheTest() {
        // 캐시가 적용되어, findBookById 가 한 번만 실행됩니다.
        Long bookId = book.getId();

        bookService.findBookById(bookId);
        bookService.findBookById(book.getId());
    }

    @DisplayName("캐시 name 테스트")
    @Test
    void cacheNameTest() {
        Long bookId = book.getId();

        // 다른 이름으로 캐시를 적용해서 각각 실행되고 캐싱됨.
        bookService.findBookById(bookId);
        bookService.findBookNameById(bookId);
    }

    @DisplayName("캐시 key 테스트")
    @Test
    void cacheKeyTest() {
        Long bookId = book.getId();
        String name = book.getName();

        // id 만을 캐시 키로 설정했기 때문에 name 이 달라도 같은 결과가 나온다.
        Book book1 = bookService.findBookByIdAndName(bookId, name);
        System.out.println("book1 = " + book1);
        Book book2 = bookService.findBookByIdAndName(bookId, name + "가 아님");
        System.out.println("book2 = " + book2);
    }

}