package ding.co.backendportfolio.chapter4._3_event_with_lock.service;

import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter2.repository.MemberRepository;
import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLock;
import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLockParticipant;
import ding.co.backendportfolio.chapter4._3_event_with_lock.repository.EventWithLockRepository;
import ding.co.backendportfolio.chapter4._4_event_with_external.repository.EventWithLockParticipantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventWithLockService {
    private final EventWithLockRepository eventRepository;
    private final EventWithLockParticipantRepository participantRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void joinEventOptimistic(Long eventId, Long memberId) {
        EventWithLock event = eventRepository.findByIdWithOptimisticLock(eventId)
                .orElseThrow(() -> new EntityNotFoundException("이벤트를 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        event.increaseParticipants();
        eventRepository.saveAndFlush(event);

        EventWithLockParticipant participant = EventWithLockParticipant.builder()
                .event(event)
                .member(member)
                .build();
        participantRepository.save(participant);
    }

    @Transactional
    public void joinEventPessimistic(Long eventId, Long memberId) {
        EventWithLock event = eventRepository.findByIdWithPessimisticLock(eventId)
                .orElseThrow(() -> new EntityNotFoundException("이벤트를 찾을 수 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        event.increaseParticipants();
        EventWithLockParticipant participant = EventWithLockParticipant.builder()
                .event(event)
                .member(member)
                .build();

        participantRepository.save(participant);
    }

    // TODO: Named Lock 을 위해 아래 주석을 해제해야함
    //    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void joinEventWithNamedLock(Long eventId, Long memberId) {
        EventWithLock event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("이벤트를 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        event.increaseParticipants();
        eventRepository.saveAndFlush(event);

        EventWithLockParticipant participant = EventWithLockParticipant.builder()
                .event(event)
                .member(member)
                .build();
        participantRepository.save(participant);
    }
} 