package ding.co.backendportfolio.chapter4._3_event_with_lock.facade;

import ding.co.backendportfolio.chapter4._3_event_with_lock.service.EventWithLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OptimisticLockEventFacade {
    private final EventWithLockService eventWithLockService;

    private static final long RETRY_DELAY_MS = 50;

    public void joinEvent(Long eventId, Long memberId) throws InterruptedException {
        int retryCount = 0;

        while (true) {
            try {
                eventWithLockService.joinEventOptimistic(eventId, memberId);
                log.info("이벤트 참가 성공 - eventId: {}, memberId: {}, 총 시도횟수: {}",
                        eventId, memberId, retryCount + 1);
                return;
            } catch (Exception e) {
                retryCount++;
                log.warn("이벤트 참가 재시도 - eventId: {}, memberId: {}, 현재 시도횟수: {}, error: {}",
                        eventId, memberId, retryCount, e.getMessage());
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
    }
} 