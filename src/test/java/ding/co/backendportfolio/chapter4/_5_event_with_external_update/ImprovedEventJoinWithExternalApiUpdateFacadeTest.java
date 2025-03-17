package ding.co.backendportfolio.chapter4._5_event_with_external_update;

import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLock;
import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLockParticipant;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.ExternalEventApi;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.KakaoTalkMessageApi;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.model.ExternalEventResponse;
import ding.co.backendportfolio.chapter4._4_event_with_external.service.EventExternalUpdateService;
import ding.co.backendportfolio.chapter4._5_event_with_external_update.event.EventJoinCompletedEvent;
import ding.co.backendportfolio.chapter4._5_event_with_external_update.facade.ImprovedEventJoinWithExternalApiUpdateFacade;
import ding.co.backendportfolio.chapter4.fixture.Chapter4Fixture;
import ding.co.backendportfolio.config.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@Slf4j
@IntegrationTest
@RecordApplicationEvents
class ImprovedEventJoinWithExternalApiUpdateFacadeTest {

    @Autowired
    private ImprovedEventJoinWithExternalApiUpdateFacade eventJoinFacade;

    @MockBean
    private EventExternalUpdateService eventJoinService;

    @MockBean
    private ExternalEventApi externalEventApi;

    @MockBean
    private KakaoTalkMessageApi kakaoTalkMessageApi;

    @Autowired
    private ApplicationEvents applicationEvents;

    private EventWithLock testEvent;
    private Member testMember;
    private EventWithLockParticipant testParticipant;
    private static final String TEST_PHONE_NUMBER = "01012341234";

    @BeforeEach
    void setUp() {
        testEvent = Chapter4Fixture.createEventWithLock("테스트 이벤트", 100);
        testMember = Chapter4Fixture.createTestMember("테스트유저");
        testParticipant = Chapter4Fixture.createTestParticipant(testEvent, testMember);

        when(eventJoinService.joinEventWithTransaction(any(), any()))
                .thenReturn(testParticipant);

        String externalId = UUID.randomUUID().toString();
        when(externalEventApi.registerParticipant(any(), any(), any()))
                .thenReturn(ExternalEventResponse.builder()
                        .success(true)
                        .externalId(externalId)
                        .build());
    }

    @Test
    @DisplayName("이벤트 참가 성공 시 이벤트 발행")
    void joinEvent_Success() {
        // when
        eventJoinFacade.joinEvent(testEvent.getId(), testMember.getId());

        // then
        verifyEventPublished();
        verifyBasicMethodCalls();
    }

    @Test
    @DisplayName("이벤트 참가 성공 후 메시지 발송 실패해도 트랜잭션은 커밋됨")
    void joinEvent_SuccessWithMessageFailure() {
        // given
        doThrow(new RuntimeException("메시지 발송 실패"))
                .when(kakaoTalkMessageApi).sendEventJoinMessage(any(), any());

        // when
        eventJoinFacade.joinEvent(testEvent.getId(), testMember.getId());

        // then
        verifyEventPublished();
        verifyBasicMethodCalls();
    }

    private void verifyEventPublished() {
        assertThat(applicationEvents.stream(EventJoinCompletedEvent.class).count())
                .isEqualTo(1);

        EventJoinCompletedEvent event = applicationEvents.stream(EventJoinCompletedEvent.class)
                .findFirst()
                .orElseThrow();

        assertThat(event)
                .satisfies(e -> {
                    assertThat(e.getEventId()).isEqualTo(testEvent.getId());
                    assertThat(e.getEventName()).isEqualTo(testEvent.getName());
                    assertThat(e.getPhoneNumber()).isEqualTo("01012341234");
                });
    }

    private void verifyBasicMethodCalls() {
        verify(eventJoinService, times(1))
                .joinEventWithTransaction(eq(testEvent.getId()), eq(testMember.getId()));
        verify(externalEventApi, times(1))
                .registerParticipant(eq(testEvent.getId()), eq(testMember.getId()), eq(testEvent.getName()));
        verify(eventJoinService, times(1))
                .updateExternalId(eq(testParticipant), any());
    }
} 