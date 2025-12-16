package ding.co.backendportfolio.chapter4.fixture;

import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter2.entity.Role;
import ding.co.backendportfolio.chapter4._1_event.entity.Event;
import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLock;
import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLockParticipant;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class Chapter4Fixture {

    // Event 관련 픽스처 (chapter4._1_event용)
    public static Event createEvent(String name, int maxParticipants) {
        return Event.builder()
                .name(name != null ? name : "테스트 이벤트")
                .description("동시성 테스트")
                .eventDate(LocalDateTime.now().plusDays(7))
                .maxParticipants(maxParticipants > 0 ? maxParticipants : 100)
                .build();
    }

    public static List<Event> createEvents(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createEvent("테스트 이벤트 " + i, 100))
                .collect(Collectors.toList());
    }

    // EventWithLock 관련 픽스처 (chapter4._3_event_with_lock 이후 버전용)
    public static EventWithLock createEventWithLock(String name, int maxParticipants) {
        return EventWithLock.builder()
                .name(name != null ? name : "테스트 이벤트")
                .description("동시성 테스트")
                .eventDate(LocalDateTime.now().plusDays(7))
                .maxParticipants(maxParticipants > 0 ? maxParticipants : 100)
                .build();
    }

    public static List<EventWithLock> createTestEventsWithLock(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createEventWithLock("테스트 이벤트 " + i, 100))
                .collect(Collectors.toList());
    }

    // Member 관련 픽스처
    public static Member createTestMember(String nickname) {
        return Member.builder()
                .email("test" + UUID.randomUUID() + "@test.com")
                .password("password")
                .nickname(nickname != null ? nickname : "테스트유저")
                .role(Role.USER)
                .build();
    }

    public static List<Member> createTestMembers(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> createTestMember("테스트유저" + i))
                .collect(Collectors.toList());
    }

    // Participant 관련 픽스처
    public static EventWithLockParticipant createTestParticipant(EventWithLock event, Member member) {
        return EventWithLockParticipant.builder()
                .event(event)
                .member(member)
                .build();
    }

    public static List<EventWithLockParticipant> createTestParticipants(EventWithLock event, List<Member> members) {
        return members.stream()
                .map(member -> createTestParticipant(event, member))
                .collect(Collectors.toList());
    }
} 