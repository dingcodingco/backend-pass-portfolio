package ding.co.backendportfolio.chapter5improved._4_async_operation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
public class MovieImprovedRecommendClient {

    @Async("asyncExecutor")
    public CompletableFuture<List<Long>> getRecommendedMovieIds(Long userNo) {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
        }

        List<Long> recommendedMovieIds = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            long randomLong = ThreadLocalRandom.current().nextLong();
            recommendedMovieIds.add(randomLong);
        }

        return CompletableFuture.completedFuture(recommendedMovieIds);
    }

}
