package ding.co.backendportfolio.chapter4._2_event_with_participant;

import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter2.repository.MemberRepository;
import ding.co.backendportfolio.chapter4._1_event.entity.Event;
import ding.co.backendportfolio.chapter4._1_event.repository.EventRepository;
import ding.co.backendportfolio.chapter4._2_event_with_participant.repository.EventParticipantRepository;
import ding.co.backendportfolio.chapter4._2_event_with_participant.service.EventJoinService;
import ding.co.backendportfolio.chapter4.fixture.Chapter4Fixture;
import ding.co.backendportfolio.chapter4.fixture.ConcurrentTestUtil;
import ding.co.backendportfolio.config.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@IntegrationTest
class EventJoinServiceTest {

    @Autowired
    private EventJoinService eventJoinService;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventParticipantRepository participantRepository;

    private Event testEvent;
    private List<Member> testMembers;

    @BeforeEach
    void setUp() {
        // 테스트용 이벤트 생성 - 최대 참가자 100명
        testEvent = eventRepository.save(
                Chapter4Fixture.createEvent("테스트 이벤트", 100)
        );

        // 테스트용 회원 150명 생성
        testMembers = memberRepository.saveAll(
                Chapter4Fixture.createTestMembers(150)
        );
    }

    @Test
    @DisplayName("트랜잭션만으로는 동시성 제어가 안되는 것을 보여주는 테스트")
    void transactionDoesNotGuaranteeAtomicity() throws InterruptedException {
        // when
        ConcurrentTestUtil.executeConcurrentJoins(
                testEvent.getId(),
                testMembers,
                (eventId, memberId) -> eventJoinService.joinEvent(eventId, memberId)
        );

        // then
        Event updatedEvent = eventRepository.findById(testEvent.getId()).orElseThrow();
        long actualParticipantCount = participantRepository.countByEventId(testEvent.getId());

        log.info("=== 트랜잭션 동시성 테스트 결과 ===");
        log.info("이벤트 최대 참가 인원: {}", testEvent.getMaxParticipants());
        log.info("이벤트 현재 참가자 수: {}", updatedEvent.getCurrentParticipants());
        log.info("실제 참가자 테이블 레코드 수: {}", actualParticipantCount);

        // 검증 1: 이벤트 테이블의 참가자 수와 참가자 테이블의 실제 레코드 수가 다름
        assertThat(updatedEvent.getCurrentParticipants()).isNotEqualTo(actualParticipantCount);
        // 검증 2: 참가자 수는 절대로 최대 인원을 초과하면 안 됨
        assertThat(updatedEvent.getCurrentParticipants()).isLessThanOrEqualTo(testEvent.getMaxParticipants());
    }
} 