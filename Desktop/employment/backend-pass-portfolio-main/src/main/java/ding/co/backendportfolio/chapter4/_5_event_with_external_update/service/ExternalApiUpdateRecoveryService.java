package ding.co.backendportfolio.chapter4._5_event_with_external_update.service;

import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLockParticipant;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.ExternalEventApi;
import ding.co.backendportfolio.chapter4._4_event_with_external.external.model.ExternalEventResponse;
import ding.co.backendportfolio.chapter4._4_event_with_external.repository.EventWithLockParticipantRepository;
import ding.co.backendportfolio.chapter4._4_event_with_external.service.EventExternalUpdateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExternalApiUpdateRecoveryService {
    private final EventWithLockParticipantRepository participantRepository;
    private final ExternalEventApi externalEventApi;
    private final EventExternalUpdateService eventJoinService;

    /**
     * 5분마다 실행되어 외부 ID가 없는 참가자들을 동기화
     */
//    @Scheduled(fixedDelay = 300000) // 5분
    public void recoverMissingExternalIds() {
        // 1. external_id가 없는 참가자들 조회
        List<EventWithLockParticipant> participantsWithoutExternalId = participantRepository.findByExternalIdIsNullAndCreatedAtBefore(
                LocalDateTime.now()
        );

        log.info("외부 ID 미할당 참가자 발견: {}건", participantsWithoutExternalId.size());

        // 2. 각 참가자별로 외부 시스템 조회 및 업데이트
        for (EventWithLockParticipant participant : participantsWithoutExternalId) {
            syncExternalId(participant);
        }
    }

    /**
     * 개별 참가자의 외부 ID 동기화
     */
    public void syncExternalId(EventWithLockParticipant participant) {
        // 1. 외부 시스템에서 참가자 정보 조회 (트랜잭션 밖에서)
        ExternalEventResponse response = externalEventApi.getParticipantInfo(
                participant.getEvent().getId(),
                participant.getMember().getId()
        );

        if (!response.isSuccess() || response.getExternalId() == null) {
            log.warn("참가자 ID: {}의 외부 시스템 정보 없음 (응답: {})",
                    participant.getId(), response);
            return;
        }

        eventJoinService.updateExternalId(participant, response.getExternalId());
    }
} 