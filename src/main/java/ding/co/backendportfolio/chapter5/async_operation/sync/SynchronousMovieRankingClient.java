package ding.co.backendportfolio.chapter5.async_operation.sync;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class SynchronousMovieRankingClient {
    public int getRanking(Long movieId, long delaySeconds) throws InterruptedException {
        Thread.sleep(delaySeconds * 1000L);
        return ThreadLocalRandom.current().nextInt(1, 10);
    }
}
