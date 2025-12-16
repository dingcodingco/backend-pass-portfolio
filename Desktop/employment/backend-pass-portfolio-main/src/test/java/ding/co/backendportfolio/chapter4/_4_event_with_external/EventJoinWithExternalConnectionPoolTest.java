package ding.co.backendportfolio.chapter4._4_event_with_external;

import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter2.repository.MemberRepository;
import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLock;
import ding.co.backendportfolio.chapter4._3_event_with_lock.repository.EventWithLockRepository;
import ding.co.backendportfolio.chapter4._4_event_with_external.facade.EventJoinWithExternalApiFacade;
import ding.co.backendportfolio.chapter4._4_event_with_external.facade.ImprovedEventJoinWithExternalApiFacade;
import ding.co.backendportfolio.chapter4.fixture.Chapter4Fixture;
import ding.co.backendportfolio.chapter4.fixture.ConcurrentTestUtil;
import ding.co.backendportfolio.config.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@IntegrationTest
class EventJoinWithExternalConnectionPoolTest {
    private static final int BACKGROUND_THREAD_COUNT = 28;  // 28개 커넥션 점유
    private static final int TEST_THREAD_COUNT = 20;

    @Autowired
    private EventJoinWithExternalApiFacade originalService;
    @Autowired
    private ImprovedEventJoinWithExternalApiFacade improvedService;
    @Autowired
    private EventWithLockRepository eventRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    private List<EventWithLock> testEvents;
    private List<Member> testMembers;
    private ExecutorService backgroundExecutor;

    @BeforeEach
    void setUp() {
        testEvents = eventRepository.saveAll(Chapter4Fixture.createTestEventsWithLock(TEST_THREAD_COUNT));
        testMembers = memberRepository.saveAll(Chapter4Fixture.createTestMembers(TEST_THREAD_COUNT));
        backgroundExecutor = Executors.newFixedThreadPool(BACKGROUND_THREAD_COUNT);
    }

    @AfterEach
    void cleanup() {
        backgroundExecutor.shutdownNow();
    }

    @Test
    @DisplayName("커넥션 풀 점유 상황에서 트랜잭션 분리 효과 비교")
    void compareWaitTimeTest() throws InterruptedException {
        // given
        occupyConnections();
        Thread.sleep(1000);  // 커넥션이 점유되기를 기다림

        List<Long> originalVersionTimes = Collections.synchronizedList(new ArrayList<>());
        List<Long> improvedVersionTimes = Collections.synchronizedList(new ArrayList<>());

        // when - 원본 버전 테스트
        ConcurrentTestUtil.executeNonConflictingJoins(
                testEvents,
                testMembers,
                (eventId, memberId) -> {
                    long startTime = System.currentTimeMillis();
                    originalService.joinEvent(eventId, memberId);
                    originalVersionTimes.add(System.currentTimeMillis() - startTime);
                }
        );

        // 새로운 이벤트들 생성
        testEvents = eventRepository.saveAll(Chapter4Fixture.createTestEventsWithLock(TEST_THREAD_COUNT));

        // when - 개선된 버전 테스트
        ConcurrentTestUtil.executeNonConflictingJoins(
                testEvents,
                testMembers,
                (eventId, memberId) -> {
                    long startTime = System.currentTimeMillis();
                    improvedService.joinEvent(eventId, memberId);
                    improvedVersionTimes.add(System.currentTimeMillis() - startTime);
                }
        );

        // then
        double originalAverage = calculateAverage(originalVersionTimes);
        double improvedAverage = calculateAverage(improvedVersionTimes);

        logTestResults("원본 버전", originalVersionTimes);
        logTestResults("개선된 버전", improvedVersionTimes);

        assertThat(improvedAverage)
                .isLessThan(originalAverage * 0.5)
                .as("개선된 버전이 원본 버전보다 50% 이상 빨라야 함");
    }

    private double calculateAverage(List<Long> times) {
        return times.stream()
                .mapToLong(Long::valueOf)
                .average()
                .orElse(0.0);
    }

    private void logTestResults(String version, List<Long> waitTimes) {
        double averageWaitTime = calculateAverage(waitTimes);
        log.info("=== {} 성능 테스트 결과 ===", version);
        log.info("평균 대기 시간: {}ms", averageWaitTime);
        log.info("최소 대기 시간: {}ms", Collections.min(waitTimes));
        log.info("최대 대기 시간: {}ms", Collections.max(waitTimes));
    }

    private void occupyConnections() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        for (int i = 0; i < BACKGROUND_THREAD_COUNT; i++) {
            backgroundExecutor.submit(() -> {
                try {
                    transactionTemplate.execute(status -> {
                        try {
                            Thread.sleep(Integer.MAX_VALUE);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        return null;
                    });
                } catch (Exception e) {
                    log.error("Error in background thread: ", e);
                }
            });
        }
    }
} 