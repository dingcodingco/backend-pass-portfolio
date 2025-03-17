package ding.co.backendportfolio.chapter4._3_event_with_lock.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "ch4_event_with_lock")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventWithLock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private LocalDateTime eventDate;
    private int maxParticipants;
    private int currentParticipants;

    @Version
    private Long version;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Builder
    public EventWithLock(String name, String description, LocalDateTime eventDate, int maxParticipants) {
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