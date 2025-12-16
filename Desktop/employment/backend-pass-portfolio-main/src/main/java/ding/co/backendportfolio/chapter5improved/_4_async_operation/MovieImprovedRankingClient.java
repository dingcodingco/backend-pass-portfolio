package ding.co.backendportfolio.chapter5improved._4_async_operation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class MovieImprovedRankingClient {
    @Async("asyncExecutor")
    public CompletableFuture<Integer> getRanking(Long movieId) {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
        }
        return CompletableFuture.completedFuture(ThreadLocalRandom.current().nextInt(1, 10));
    }
}
