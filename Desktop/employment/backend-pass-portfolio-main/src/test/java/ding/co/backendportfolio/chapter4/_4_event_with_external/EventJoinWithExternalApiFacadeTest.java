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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@IntegrationTest
class EventJoinWithExternalApiFacadeTest {
    private static final int TEST_THREAD_COUNT = 20;

    @Autowired
    private EventJoinWithExternalApiFacade originalService;
    @Autowired
    private ImprovedEventJoinWithExternalApiFacade improvedService;
    @Autowired
    private EventWithLockRepository eventRepository;
    @Autowired
    private MemberRepository memberRepository;

    private List<EventWithLock> testEvents;
    private List<Member> testMembers;

    @BeforeEach
    void setUp() {
        testEvents = eventRepository.saveAll(Chapter4Fixture.createTestEventsWithLock(TEST_THREAD_COUNT));
        testMembers = memberRepository.saveAll(Chapter4Fixture.createTestMembers(TEST_THREAD_COUNT));
    }

    @Test
    @DisplayName("동시 요청이 커넥션 풀 사이즈보다 적은 경우, 트랜잭션 범위가 길어도 성능 차이가 없음")
    void comparePerformanceTest() throws InterruptedException {
        // given
        List<Long> originalVersionTimes = Collections.synchronizedList(new ArrayList<>());
        List<Long> improvedVersionTimes = Collections.synchronizedList(new ArrayList<>());

        // when - 원본 버전 테스트 (긴 트랜잭션)
        long originalStartTime = System.currentTimeMillis();
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

        // when - 개선된 버전 테스트 (짧은 트랜잭션)
        long improvedStartTime = System.currentTimeMillis();
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
        logTestResults("원본 버전 (긴 트랜잭션)", originalStartTime, originalVersionTimes);
        logTestResults("개선된 버전 (짧은 트랜잭션)", improvedStartTime, improvedVersionTimes);
    }

    private double calculateAverage(List<Long> times) {
        return times.stream()
                .mapToLong(Long::valueOf)
                .average()
                .orElse(0.0);
    }

    private void logTestResults(String version, long startTime, List<Long> executionTimes) {
        long totalExecutionTime = System.currentTimeMillis() - startTime;
        double averageExecutionTime = calculateAverage(executionTimes);

        log.info("=== {} 성능 테스트 결과 ===", version);
        log.info("동시 요청 수: {}", testMembers.size());
        log.info("커넥션 풀 사이즈: 32");
        log.info("총 실행 시간: {}ms", totalExecutionTime);
        log.info("평균 실행 시간: {}ms", averageExecutionTime);
        log.info("최소 실행 시간: {}ms", Collections.min(executionTimes));
        log.info("최대 실행 시간: {}ms", Collections.max(executionTimes));
    }
}