package ding.co.backendportfolio.chapter4._3_event_with_lock.controller;

import ding.co.backendportfolio.chapter2.service.MemberService;
import ding.co.backendportfolio.chapter4._3_event_with_lock.service.EventWithLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chapter4/events")
@RequiredArgsConstructor
public class EventController {
    private final EventWithLockService eventWithLockService;
    private final MemberService memberService;

    @PostMapping("/{eventId}/join/optimistic")
    public ResponseEntity<Void> joinEventOptimistic(
            @PathVariable Long eventId,
            @AuthenticationPrincipal User user) throws InterruptedException {
        Long memberId = memberService.getCurrentMember(user.getUsername()).getId();
        eventWithLockService.joinEventOptimistic(eventId, memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{eventId}/join/pessimistic")
    public ResponseEntity<Void> joinEventPessimistic(
            @PathVariable Long eventId,
            @AuthenticationPrincipal User user) {
        Long memberId = memberService.getCurrentMember(user.getUsername()).getId();
        eventWithLockService.joinEventPessimistic(eventId, memberId);
        return ResponseEntity.ok().build();
    }
} 