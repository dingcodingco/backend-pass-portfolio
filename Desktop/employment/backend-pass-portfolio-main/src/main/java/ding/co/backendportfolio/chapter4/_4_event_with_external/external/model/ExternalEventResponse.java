package ding.co.backendportfolio.chapter4._4_event_with_external.external.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExternalEventResponse {
    private boolean success;
    private String externalId;
    private String message;
    private String errorMessage;
} 