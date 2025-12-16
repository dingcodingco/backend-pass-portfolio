package ding.co.backendportfolio.chapter4._5_event_with_external_update.event;

import ding.co.backendportfolio.chapter4._4_event_with_external.external.KakaoTalkMessageApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventJoinEventListener {
    private final KakaoTalkMessageApi kakaoTalkMessageApi;

    @Async
    @EventListener
    public void handleEventJoinCompleted(EventJoinCompletedEvent event) {
        try {
            kakaoTalkMessageApi.sendEventJoinMessage(
                    event.getPhoneNumber(),
                    event.getEventName()
            );
        } catch (Exception e) {
            log.error("알림 발송 실패. eventId={}, eventName={}",
                    event.getEventId(),
                    event.getEventName(),
                    e);
        }
    }
} 