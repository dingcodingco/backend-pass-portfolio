package ding.co.backendportfolio.chapter4.fixture;

import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter4._3_event_with_lock.entity.EventWithLock;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConcurrentTestUtil {

    @FunctionalInterface
    public interface EventJoinTask {
        void join(Long eventId, Long memberId) throws Exception;
    }

    public static void executeNonConflictingJoins(
            List<EventWithLock> events,  // 이벤트 리스트
            List<Member> members,        // 회원 리스트
            EventJoinTask joinTask       // 실행할 작업
    ) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(events.size());

        for (int i = 0; i < events.size(); i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    joinTask.join(events.get(index).getId(), members.get(index).getId());
                } catch (Exception e) {
                    log.error("이벤트 참가 실패: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();
    }

    public static void executeConcurrentJoins(
            Long eventId,
            List<Member> members,
            EventJoinTask joinTask
    ) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(members.size());

        for (int i = 0; i < members.size(); i++) {
            final int index = i;
            executorService.submit(() -> {
                try {
                    joinTask.join(eventId, members.get(index).getId());
                } catch (Exception e) {
                    log.error("이벤트 참가 실패: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();
    }
} 