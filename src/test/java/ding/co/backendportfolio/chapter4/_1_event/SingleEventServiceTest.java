package ding.co.backendportfolio.chapter4._1_event;

import ding.co.backendportfolio.chapter4._1_event.entity.Event;
import ding.co.backendportfolio.chapter4._1_event.repository.EventRepository;
import ding.co.backendportfolio.chapter4._1_event.service.SingleEventService;
import ding.co.backendportfolio.chapter4.fixture.Chapter4Fixture;
import ding.co.backendportfolio.config.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@IntegrationTest
class SingleEventServiceTest {
    @Autowired
    private SingleEventService singleEventService;

    @Autowired
    private EventRepository eventRepository;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        testEvent = eventRepository.save(
                Chapter4Fixture.createEvent("단일 레코드 테스트 이벤트", 100)
        );
    }

    @Test
    @DisplayName("단일 스레드에서는 트랜잭션으로 정상 처리된다")
    void transactionWorksInSingleThread() {
        // given
        // 이벤트 참가 신청을 시도할 총 횟수 (최대 인원은 100명이므로 일부는 실패할 것임)
        int numberOfCalls = 150;
        // 실제로 성공한 참가 신청 횟수를 카운트할 변수
        int successCount = 0;

        // when
        // 단일 스레드에서 순차적으로 참가 신청을 처리
        for (int i = 0; i < numberOfCalls; i++) {
            try {
                // 이벤트 참가 신청 시도
                // 내부적으로 다음과 같이 동작:
                // 1. DB에서 현재 이벤트 정보를 조회
                // 2. 현재 참가자 수 확인
                // 3. 참가자 수 증가
                // 4. DB에 업데이트된 정보 저장
                singleEventService.increaseParticipants(testEvent.getId());

                // 참가 신청이 성공하면 카운트 증가
                successCount++;
            } catch (Exception e) {
                // 참가 신청 실패 시 (예: 최대 인원 초과) 로그 기록
                // 이는 예상된 정상적인 실패 케이스임
                log.info("Expected error: {}", e.getMessage());
            }
        }

        // then
        // 테스트 결과 검증을 위해 DB에서 최신 이벤트 정보 조회
        Event updatedEvent = eventRepository.findById(testEvent.getId()).orElseThrow();

        // 테스트 결과를 콘솔에 출력하여 육안으로 확인 가능하게 함
        System.out.println("이벤트 최대 참가 가능 인원: " + updatedEvent.getMaxParticipants());
        System.out.println("호출 성공 횟수: " + successCount);
        System.out.println("DB에 저장된 참가자 수: " + updatedEvent.getCurrentParticipants());

        // 검증 1: 단일 스레드 환경에서는 모든 성공한 업데이트가 정확히 DB에 반영되어야 함
        // successCount와 DB의 currentParticipants가 일치해야 함
        assertThat(updatedEvent.getCurrentParticipants()).isEqualTo(successCount);

        // 검증 2: 참가자 수는 절대로 최대 인원을 초과하면 안 됨
        // 이는 비즈니스 규칙이 정상적으로 작동했음을 의미
        assertThat(updatedEvent.getCurrentParticipants()).isLessThanOrEqualTo(updatedEvent.getMaxParticipants());
    }

    @Test
    @DisplayName("트랜잭션만으로는 동시성이 제어되지 않는다는 것을 보여주는 테스트")
    void transactionDoesNotControlConcurrency() throws InterruptedException {
        // given
        int numberOfThreads = 100;
        AtomicInteger successCount = new AtomicInteger(0);
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        // when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    singleEventService.increaseParticipants(testEvent.getId());
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("이벤트 참가 실패: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        // then
        Event updatedEvent = eventRepository.findById(testEvent.getId()).orElseThrow();

        // 결과 출력
        System.out.println("이벤트 최대 참가 가능 인원: " + updatedEvent.getMaxParticipants());
        System.out.println("성공적으로 등록된 참가자 수: " + successCount.get());
        System.out.println("DB에 저장된 참가자 수: " + updatedEvent.getCurrentParticipants());

        assertThat(updatedEvent.getCurrentParticipants()).isNotEqualTo(successCount.get());
        assertThat(updatedEvent.getCurrentParticipants()).isLessThan(successCount.get());
    }
} 