package ding.co.backendportfolio.chapter6._2_remote_redis_practice;

import com.fasterxml.jackson.core.JsonProcessingException;
import ding.co.backendportfolio.chapter6._1_practice.Book;
import ding.co.backendportfolio.chapter6._1_practice.BookRepository;
import ding.co.backendportfolio.config.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@IntegrationTest
class BookPracticeServiceTest {

    @Autowired
    private BookPracticeService bookPracticeService;

    @Autowired
    private BookRepository bookRepository;

    private Book book;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAllInBatch();

        Book newBook = Book.builder()
                .name("test")
                .isSoldOut(false)
                .build();

        book = bookRepository.save(newBook);
    }

    @Test
    void findBookById() throws JsonProcessingException {
        // 첫 번째에는 캐시가 없어 DB 의 데이터를 가져와서 사용한다.
        Book firstBook = bookPracticeService.findBookById(this.book.getId());

        log.info("--------------------");

        // 두 번째에는 캐시의 데이터를 이용한다.
        Book secondBook = bookPracticeService.findBookById(this.book.getId());

        assertThat(firstBook.getId()).isEqualTo(secondBook.getId());
        assertThat(firstBook.getName()).isEqualTo(secondBook.getName());
    }
}