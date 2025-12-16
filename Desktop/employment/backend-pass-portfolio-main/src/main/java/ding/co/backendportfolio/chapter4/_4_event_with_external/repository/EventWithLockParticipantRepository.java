package ding.co.backendportfolio.chapter4._4_event_with_external.repository;

import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLockParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventWithLockParticipantRepository extends JpaRepository<EventWithLockParticipant, Long> {
    // 특정 시간 이전에 생성된 외부 ID 미할당 참가자 조회
    List<EventWithLockParticipant> findByExternalIdIsNullAndCreatedAtBefore(
            LocalDateTime dateTime
    );
} 