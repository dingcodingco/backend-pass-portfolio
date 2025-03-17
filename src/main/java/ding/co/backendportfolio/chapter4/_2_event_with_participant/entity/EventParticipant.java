package ding.co.backendportfolio.chapter4._2_event_with_participant.entity;

import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter4._1_event.entity.Event;
import ding.co.backendportfolio.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ch4_event_participant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventParticipant extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public EventParticipant(Event event, Member member) {
        this.event = event;
        this.member = member;
    }
} 