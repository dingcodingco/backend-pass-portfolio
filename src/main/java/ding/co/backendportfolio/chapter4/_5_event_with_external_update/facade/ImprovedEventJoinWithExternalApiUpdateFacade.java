package ding.co.backendportfolio.chapter4._5_event_with_external_update.facade;

import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLockParticipant;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.ExternalEventApi;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.model.ExternalEventResponse;
import ding.co.backendportfolio.chapter4._4_event_with_external.service.EventExternalUpdateService;
import ding.co.backendportfolio.chapter4._5_event_with_external_update.event.EventJoinCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImprovedEventJoinWithExternalApiUpdateFacade {
    private static final String TEST_PHONE_NUMBER = "01012341234";

    private final EventExternalUpdateService eventJoinService;
    private final ExternalEventApi externalEventApi;
    private final ApplicationEventPublisher eventPublisher;

    public void joinEvent(Long eventId, Long memberId) {
        // 1. 기존 서비스로 이벤트 참가 처리
        EventWithLockParticipant participant = eventJoinService.joinEventWithTransaction(eventId, memberId);

        // 2. 외부 API 호출
        ExternalEventResponse response = externalEventApi.registerParticipant(
                eventId, memberId, participant.getEvent().getName()
        );

        if (!response.isSuccess()) {
            throw new RuntimeException("외부 API 호출 실패: " + response.getErrorMessage());
        }

        // 3. 외부 API 응답으로 참가자 정보 업데이트
        eventJoinService.updateExternalId(participant, response.getExternalId());

        // 4. 이벤트 발행 (트랜잭션 커밋 후 실행됨)
        eventPublisher.publishEvent(new EventJoinCompletedEvent(
                eventId,
                participant.getEvent().getName(),
                TEST_PHONE_NUMBER
        ));
    }
}

//
//유저_가입
//1. 유저가 가입한다
//
//
//유저 가입 완료됨~~~ 이벤트 발송
//
//
//        관심있는 애들은 이거 듣고 너네 알아서 해.
//
//            이메일 쪽 수신
//            2. 유저가 가입이 완료되면 웰컴 이메일을 보낸다.
//
//            쿠폰 팀 수신
//            3. 유저가 가입이 완료되면 웰컴 쿠폰을 넣어준다
//
//            혜택 팀 수신
//            4. 혜텍 서비스 유저가 가입이 완료되면 혜택서비스의 알림을 송수신하도록 한다.
//
//
//
