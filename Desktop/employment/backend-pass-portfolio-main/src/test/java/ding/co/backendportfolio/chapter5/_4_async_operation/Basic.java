package ding.co.backendportfolio.chapter5._4_async_operation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class Basic {

    @Test
    public void makeBreadSynchronously() {
        System.out.println("1. 반죽하기");
        System.out.println("2. 오븐에 빵 굽기");
        try {
            Thread.sleep(1000); // 오븐에 굽는 시간
        } catch (InterruptedException e) {
        }
        System.out.println("!!! 빵 굽기 완료!!!");

        System.out.println("=== 빵 먹기 전 ===");
        System.out.println("3. 빵 먹기");
    }

    @Test
    public void makeBreadAsynchronously() {
        log.info("1. 반죽하기");
        log.info("2. 오븐에 빵 굽기");
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(1000); // 오븐에 굽는 작업
                    } catch (InterruptedException e) {
                    }
                })
                // "빵 굽기" 가 완료되면 할 일을 정의합니다.
                .thenRun(() -> log.info("!!! 빵 굽기 완료!!!"));

        log.info("=== 빵 먹기 전 ===");
        completableFuture.join();
        log.info("3. 빵 먹기");
    }

    @Test
    public void completableFutureExample() {
        // 1) supplyAsync: 별도 스레드에서 비동기로 실행 (값 반환)
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                // 오래 걸리는 작업 시뮬레이션
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            return "빵";
        });

        // 2) thenApply: 이전 단계 결과를 받아서 추가 작업
        // "빵" -> "빵 + 잼" -> "빵 + 잼 + 버터"
        CompletableFuture<String> chainedFuture = future
                .thenApply(bread -> bread + " + 잼")
                .thenApply(breadWithJam -> breadWithJam + " + 버터");

        // 3) join: 최종 결과를 얻을 때까지 기다림 (블로킹)
        String result = chainedFuture.join();
        System.out.println("결과: " + result); // 결과: 빵 + 잼 + 버터
    }
}
