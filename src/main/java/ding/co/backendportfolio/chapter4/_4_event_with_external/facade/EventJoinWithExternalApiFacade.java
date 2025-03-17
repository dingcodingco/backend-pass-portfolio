package ding.co.backendportfolio.chapter4._4_event_with_external.facade;

import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter2.repository.MemberRepository;
import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLock;
import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLockParticipant;
import ding.co.backendportfolio.chapter4._3_event_with_lock.repository.EventWithLockRepository;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.ExternalEventApi;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.KakaoTalkMessageApi;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.model.ExternalEventResponse;
import ding.co.backendportfolio.chapter4._4_event_with_external.repository.EventWithLockParticipantRepository;
import ding.co.backendportfolio.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventJoinWithExternalApiFacade {
    private static final String TEST_PHONE_NUMBER = "01012341234";

    private final EventWithLockRepository eventRepository;
    private final EventWithLockParticipantRepository participantRepository;
    private final MemberRepository memberRepository;
    private final ExternalEventApi externalEventApi;
    private final KakaoTalkMessageApi kakaoTalkMessageApi;

    @Transactional
    public void joinEvent(Long eventId, Long memberId) {
        // 1. 이벤트와 회원 정보 조회
        EventWithLock event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("이벤트를 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        // 2. 이벤트 참가자 수 증가
        event.increaseParticipants();
        eventRepository.saveAndFlush(event);

        // 3. 외부 API 호출하여 이벤트 참가 처리
        ExternalEventResponse response = externalEventApi.registerParticipant(
                eventId, memberId, event.getName()
        );

        if (!response.isSuccess()) {
            throw new RuntimeException("외부 API 호출 실패: " + response.getErrorMessage());
        }

        // 5. 참가자 정보 저장
        EventWithLockParticipant participant = EventWithLockParticipant.builder()
                .event(event)
                .member(member)
                .build();
        participantRepository.save(participant);

        // 6. 카카오톡 알림 발송 (테스트용 전화번호 사용)
        kakaoTalkMessageApi.sendEventJoinMessage(TEST_PHONE_NUMBER, event.getName());
    }
} 