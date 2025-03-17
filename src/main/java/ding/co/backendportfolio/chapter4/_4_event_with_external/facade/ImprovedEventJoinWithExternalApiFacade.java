package ding.co.backendportfolio.chapter4._4_event_with_external.facade;

import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLock;
import ding.co.backendportfolio.chapter4._3_event_with_lock.repository.EventWithLockRepository;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.ExternalEventApi;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.KakaoTalkMessageApi;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.model.ExternalEventResponse;
import ding.co.backendportfolio.chapter4._4_event_with_external.service.EventExternalUpdateService;
import ding.co.backendportfolio.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImprovedEventJoinWithExternalApiFacade {
    private static final String TEST_PHONE_NUMBER = "01012341234";

    private final EventExternalUpdateService eventExternalUpdateService;
    private final ExternalEventApi externalEventApi;
    private final KakaoTalkMessageApi kakaoTalkMessageApi;
    private final EventWithLockRepository eventRepository;

    public void joinEvent(Long eventId, Long memberId) {
        // 0. 이벤트 정보 조회 (읽기 전용)
        EventWithLock event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("이벤트를 찾을 수 없습니다."));

        // 1. DB 트랜잭션 먼저 처리
        eventExternalUpdateService.joinEventWithTransaction(eventId, memberId);

        // 2. DB 트랜잭션 성공 후 외부 API 호출
        ExternalEventResponse response = externalEventApi.registerParticipant(
                eventId, memberId, event.getName()
        );

        if (!response.isSuccess()) {
            // 외부 API 실패 시 보상 트랜잭션 또는 알림 처리 필요
            log.error("외부 API 호출 실패. 이벤트: {}, 회원: {}", eventId, memberId);
        }

        // 3. 카카오톡 알림 발송 (선택적)
        try {
            kakaoTalkMessageApi.sendEventJoinMessage(TEST_PHONE_NUMBER, event.getName());
        } catch (Exception e) {
            log.error("알림 발송 실패", e);
            // 알림 실패는 핵심 비즈니스 로직에 영향을 주지 않음
        }
    }
} 