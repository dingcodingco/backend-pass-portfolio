package ding.co.backendportfolio.chapter4._1_event.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ch4_event")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private LocalDateTime eventDate;
    private int maxParticipants;
    private int currentParticipants;

    @Builder
    public Event(String name, String description, LocalDateTime eventDate, int maxParticipants) {
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = 0;
    }

    public void increaseParticipants() {
        if (this.currentParticipants >= this.maxParticipants) {
            throw new RuntimeException("최대 참가 인원을 초과했습니다.");
        }
        this.currentParticipants++;
    }
} 