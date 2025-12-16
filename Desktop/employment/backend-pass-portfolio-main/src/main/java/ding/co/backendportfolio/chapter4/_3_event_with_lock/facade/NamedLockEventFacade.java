package ding.co.backendportfolio.chapter4._3_event_with_lock.facade;

import ding.co.backendportfolio.chapter4._3_event_with_lock.repository.EventWithLockRepository;
import ding.co.backendportfolio.chapter4._3_event_with_lock.service.EventWithLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NamedLockEventFacade {
    private final EventWithLockService eventWithLockService;
    private final EventWithLockRepository eventWithLockRepository;

    private static final long RETRY_DELAY_MS = 50;

    @Transactional
    public void joinEvent(Long eventId, Long memberId) throws InterruptedException {
        int retryCount = 0;
        String lockName = String.format("event_%d", eventId);

        while (true) {
            try {
                int lockResult = eventWithLockRepository.getLock(lockName, 3);
                if (lockResult <= 0) {
                    log.warn("락 획득 실패 - eventId: {}, memberId: {}", eventId, memberId);
                    Thread.sleep(RETRY_DELAY_MS);
                    continue;
                }

                eventWithLockService.joinEventWithNamedLock(eventId, memberId);
                log.info("이벤트 참가 성공 - eventId: {}, memberId: {}, 총 시도횟수: {}",
                        eventId, memberId, retryCount + 1);
                return;
            } catch (Exception e) {
                retryCount++;
                log.warn("이벤트 참가 재시도 - eventId: {}, memberId: {}, 현재 시도횟수: {}, error: {}",
                        eventId, memberId, retryCount, e.getMessage());
                Thread.sleep(RETRY_DELAY_MS);
            } finally {
                int releaseResult = eventWithLockRepository.releaseLock(lockName);
                if (releaseResult <= 0) {
                    log.error("락 해제 실패 - eventId: {}, memberId: {}", eventId, memberId);
                }
            }
        }
    }
}