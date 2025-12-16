package ding.co.backendportfolio.chapter4._4_event_with_external.external.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExternalEventRequest {
    private Long eventId;
    private Long memberId;
    private String eventName;
} 