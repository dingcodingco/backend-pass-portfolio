package ding.co.backendportfolio.chapter4._3_event_with_lock;

import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter2.repository.MemberRepository;
import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLock;
import ding.co.backendportfolio.chapter4._3_event_with_lock.facade.NamedLockEventFacade;
import ding.co.backendportfolio.chapter4._3_event_with_lock.facade.OptimisticLockEventFacade;
import ding.co.backendportfolio.chapter4._3_event_with_lock.repository.EventWithLockRepository;
import ding.co.backendportfolio.chapter4._3_event_with_lock.service.EventWithLockService;
import ding.co.backendportfolio.chapter4._4_event_with_external.repository.EventWithLockParticipantRepository;
import ding.co.backendportfolio.chapter4.fixture.Chapter4Fixture;
import ding.co.backendportfolio.chapter4.fixture.ConcurrentTestUtil;
import ding.co.backendportfolio.config.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@IntegrationTest
class EventWithLockTest {
    @Autowired
    private OptimisticLockEventFacade optimisticLockEventFacade;
    @Autowired
    private EventWithLockService eventWithLockService;
    @Autowired
    private EventWithLockRepository eventRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private EventWithLockParticipantRepository participantRepository;
    @Autowired
    private NamedLockEventFacade namedLockEventFacade;

    private EventWithLock testEvent;
    private List<Member> testMembers;

    private static final int THREAD_COUNT = 100;

    @BeforeEach
    void setUp() {
        // 테스트용 이벤트와 회원 생성
        testEvent = eventRepository.saveAndFlush(
                Chapter4Fixture.createEventWithLock("테스트 이벤트", 100)
        );
        testMembers = memberRepository.saveAll(
                Chapter4Fixture.createTestMembers(THREAD_COUNT)
        );
    }

    @AfterEach
    void cleanup() {
        participantRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("낙관적 락으로 100명 동시 참가 테스트")
    void optimisticLockTest() throws InterruptedException {
        // when
        long startTime = System.currentTimeMillis();
        ConcurrentTestUtil.executeConcurrentJoins(
                testEvent.getId(),    // 어떤 이벤트에
                testMembers,         // 어떤 회원들이
                (eventId, memberId) -> optimisticLockEventFacade.joinEvent(eventId, memberId)    // 어떻게 참가할지
        );
        long executionTime = System.currentTimeMillis() - startTime;

        // then
        EventWithLock event = eventRepository.findById(testEvent.getId()).orElseThrow();
        log.info("=== 낙관적 락 테스트 결과 ===");
        log.info("실행 시간: {}ms", executionTime);
        log.info("최종 참가자 수: {}", event.getCurrentParticipants());

        assertThat(event.getCurrentParticipants()).isEqualTo(THREAD_COUNT);
    }

    @Disabled
    @DisplayName("네임드 락으로 100명 동시 참가 테스트")
    void namedLockTest() throws InterruptedException {
        // when
        long startTime = System.currentTimeMillis();
        ConcurrentTestUtil.executeConcurrentJoins(
                testEvent.getId(),
                testMembers,
                (eventId, memberId) -> namedLockEventFacade.joinEvent(eventId, memberId)
        );
        long executionTime = System.currentTimeMillis() - startTime;

        // then
        EventWithLock event = eventRepository.findById(testEvent.getId()).orElseThrow();
        log.info("=== 네임드 락 테스트 결과 ===");
        log.info("실행 시간: {}ms", executionTime);
        log.info("최종 참가자 수: {}", event.getCurrentParticipants());

        assertThat(event.getCurrentParticipants()).isEqualTo(THREAD_COUNT);
    }

    @Test
    @DisplayName("비관적 락으로 100명 동시 참가 테스트")
    void pessimisticLockTest() throws InterruptedException {
        // when
        long startTime = System.currentTimeMillis();
        ConcurrentTestUtil.executeConcurrentJoins(
                testEvent.getId(),
                testMembers,
                (eventId, memberId) -> eventWithLockService.joinEventPessimistic(eventId, memberId)
        );
        long executionTime = System.currentTimeMillis() - startTime;

        // then
        EventWithLock event = eventRepository.findById(testEvent.getId()).orElseThrow();
        log.info("=== 비관적 락 테스트 결과 ===");
        log.info("실행 시간: {}ms", executionTime);
        log.info("최종 참가자 수: {}", event.getCurrentParticipants());

        assertThat(event.getCurrentParticipants()).isEqualTo(THREAD_COUNT);
    }
} 