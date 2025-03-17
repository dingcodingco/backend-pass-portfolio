package ding.co.backendportfolio.chapter4._3_event_with_lock.entity;

import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ch4_event_with_lock_participant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventWithLockParticipant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private EventWithLock event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "external_id")
    private String externalId;

    @Builder
    public EventWithLockParticipant(EventWithLock event, Member member) {
        this.event = event;
        this.member = member;
    }

    public void updateExternalId(String externalId) {
        this.externalId = externalId;
    }
} 