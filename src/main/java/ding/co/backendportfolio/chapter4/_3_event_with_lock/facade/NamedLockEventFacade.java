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

    @Transactional
    public void joinEvent(Long eventId, Long memberId) {
        eventWithLockRepository.getLock(String.format("event_%d", eventId), 3);
        eventWithLockService.joinEventWithNamedLock(eventId, memberId);
    }
} 