package ding.co.backendportfolio.chapter6._2_remote_redis_practice;

import ding.co.backendportfolio.chapter6._1_practice.Book;
import ding.co.backendportfolio.chapter6._1_practice.BookRepository;
import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@IntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractBookTest {

    public static final int REPEATED_COUNT = 20;
    public static final int WARM_UP_COUNT = 10;

    public static final String INSERT_INTO_CH_6_BOOK_NAME_IS_SOLD_OUT_VALUES = """
            INSERT INTO ch6_book (name, is_sold_out)
            VALUES (?, ?)
            """;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MeterRegistry meterRegistry;

    /**
     * 테스트 실행 전 한 번만 호출되는 초기 설정 메서드.
     * Book 데이터를 대량으로 생성하고 DB에 미리 insert해둠.
     */
    @BeforeAll
    void setUp() {
        TestLogUtil.setUpStart();
        bookRepository.deleteAllInBatch();

        List<Book> randomBooks = BookDataProvider.createRandomBooks(100_000);

        jdbcTemplate.batchUpdate(INSERT_INTO_CH_6_BOOK_NAME_IS_SOLD_OUT_VALUES, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Book book = randomBooks.get(i);
                ps.setString(1, book.getName());
                ps.setBoolean(2, book.isSoldOut());
            }

            @Override
            public int getBatchSize() {
                return randomBooks.size();
            }
        });

        TestLogUtil.setUpEnd();
    }

    /**
     * 성능 측정 및 기록을 위한 메서드.
     */
    protected void measureAndRecordTime(String operationType, int currentCount, Runnable operation) {
        // given
        Timer timer = meterRegistry.timer(operationType);

        // when
        if (currentCount <= WARM_UP_COUNT) {
            operation.run();
        } else {
            timer.record(operation);
        }

        // then
        if (currentCount == REPEATED_COUNT) {
            double mean = timer.mean(TimeUnit.MILLISECONDS);
            log.info(">>> {} - 평균 소요 시간 (warm-up 제외)={}ms", operationType, String.format("%.2f", mean));
        }
    }
}
