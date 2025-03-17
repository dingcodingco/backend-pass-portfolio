package ding.co.backendportfolio.chapter4._5_event_with_external_update.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EventJoinCompletedEvent {
    private final Long eventId;
    private final String eventName;
    private final String phoneNumber;
} 