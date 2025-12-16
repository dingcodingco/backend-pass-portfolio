package ding.co.backendportfolio.chapter4._1_event.service;

import ding.co.backendportfolio.chapter4._1_event.entity.Event;
import ding.co.backendportfolio.chapter4._1_event.repository.EventRepository;
import ding.co.backendportfolio.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service

@RequiredArgsConstructor
@Slf4j
public class SingleEventService {
    private final EventRepository eventRepository;


    public void increaseParticipants(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("이벤트를 찾을 수 없습니다."));
        event.increaseParticipants();
        eventRepository.save(event);
    }
} 