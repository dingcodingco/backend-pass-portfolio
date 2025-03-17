package ding.co.backendportfolio.chapter5.async_operation.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class AsyncMovieRankingClient {
    @Async("asyncExecutor")
    public CompletableFuture<Integer> getRanking(Long movieId, long delaySeconds) throws InterruptedException {
        Thread.sleep(delaySeconds * 1000L);
        return CompletableFuture.completedFuture(ThreadLocalRandom.current().nextInt(1, 10));
    }
}
