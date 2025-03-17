package ding.co.backendportfolio.chapter4._5_event_with_external_update;

import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter2.repository.MemberRepository;
import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLock;
import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLockParticipant;
import ding.co.backendportfolio.chapter4._3_event_with_lock.repository.EventWithLockRepository;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.ExternalEventApi;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.model.ExternalEventResponse;
import ding.co.backendportfolio.chapter4._4_event_with_external.repository.EventWithLockParticipantRepository;
import ding.co.backendportfolio.chapter4._4_event_with_external.service.EventExternalUpdateService;
import ding.co.backendportfolio.chapter4._5_event_with_external_update.service.ExternalApiUpdateRecoveryService;
import ding.co.backendportfolio.chapter4.fixture.Chapter4Fixture;
import ding.co.backendportfolio.config.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@Slf4j
@IntegrationTest
class ExternalApiUpdateRecoveryServiceTest {
    @Autowired
    private ExternalApiUpdateRecoveryService recoveryService;
    @Autowired
    private EventWithLockParticipantRepository participantRepository;
    @Autowired
    private EventWithLockRepository eventRepository;
    @Autowired
    private MemberRepository memberRepository;
    @SpyBean
    private ExternalEventApi externalEventApi;
    @MockBean
    private EventExternalUpdateService eventJoinService;

    private EventWithLock testEvent;
    private Member testMember;
    private EventWithLockParticipant testParticipant;

    @BeforeEach
    void setUp() {
        testEvent = eventRepository.save(
                Chapter4Fixture.createEventWithLock("테스트 이벤트", 100)
        );
        testMember = memberRepository.save(
                Chapter4Fixture.createTestMember("테스트유저")
        );
        testParticipant = participantRepository.save(
                Chapter4Fixture.createTestParticipant(testEvent, testMember)
        );

        doReturn(ExternalEventResponse.builder()
                .success(true)
                .externalId(UUID.randomUUID().toString())
                .build())
                .when(externalEventApi)
                .getParticipantInfo(any(), any());
    }

    @Test
    @DisplayName("외부 ID 복구 - 정상 케이스")
    void recoverMissingExternalIds_Success() {
        // given
        String expectedExternalId = UUID.randomUUID().toString();
        doReturn(ExternalEventResponse.builder()
                .success(true)
                .externalId(expectedExternalId)
                .build())
                .when(externalEventApi)
                .getParticipantInfo(any(), any());

        // when
        recoveryService.syncExternalId(testParticipant);

        // then
        verify(eventJoinService, times(1))
                .updateExternalId(eq(testParticipant), eq(expectedExternalId));
    }

    @Test
    @DisplayName("외부 API 실패 시 업데이트 시도하지 않음")
    void recoverMissingExternalIds_ApiFailure() {
        // given
        doReturn(ExternalEventResponse.builder()
                .success(false)
                .errorMessage("외부 시스템 오류")
                .build())
                .when(externalEventApi)
                .getParticipantInfo(any(), any());

        // when
        recoveryService.syncExternalId(testParticipant);

        // then
        verify(eventJoinService, never()).updateExternalId(any(), any());
    }

    @Test
    @DisplayName("scheduled 메서드가 여러 참가자를 처리")
    void recoverMissingExternalIds_MultipleParticipants() {
        // given
        List<Member> members = memberRepository.saveAll(
                Chapter4Fixture.createTestMembers(4)  // testParticipant 포함해서 총 5명
        );

        List<EventWithLockParticipant> participants = new ArrayList<>(
                participantRepository.saveAll(
                        Chapter4Fixture.createTestParticipants(testEvent, members)
                )
        );
        participants.add(testParticipant);

        // when
        recoveryService.recoverMissingExternalIds();

        // then
        verify(externalEventApi, times(participants.size())).getParticipantInfo(any(), any());
        verify(eventJoinService, times(participants.size())).updateExternalId(any(), any());
    }

    @AfterEach
    void tearDown() {
        participantRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
        eventRepository.deleteAllInBatch();
    }
} 