package ding.co.backendportfolio.chapter4._5_event_with_external_update.facade;

import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLockParticipant;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.ExternalEventApi;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.KakaoTalkMessageApi;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.model.ExternalEventResponse;
import ding.co.backendportfolio.chapter4._4_event_with_external.service.EventExternalUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventJoinWithExternalApiUpdateFacade {
    private static final String TEST_PHONE_NUMBER = "01012341234";

    private final EventExternalUpdateService eventJoinService;
    private final ExternalEventApi externalEventApi;
    private final KakaoTalkMessageApi kakaoTalkMessageApi;

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

        // 4. 카카오톡 알림 발송 (테스트용 전화번호 사용)
        kakaoTalkMessageApi.sendEventJoinMessage(TEST_PHONE_NUMBER, participant.getEvent().getName());
    }
}   