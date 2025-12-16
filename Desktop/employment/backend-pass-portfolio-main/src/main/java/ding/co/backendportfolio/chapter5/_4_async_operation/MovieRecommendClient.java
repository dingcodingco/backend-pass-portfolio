package ding.co.backendportfolio.chapter5._4_async_operation;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MovieRecommendClient {

    public List<Long> getRecommendedMovieIds(Long userNo) {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
        }

        List<Long> recommendedMovieIds = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            long randomLong = ThreadLocalRandom.current().nextLong();
            recommendedMovieIds.add(randomLong);
        }

        return recommendedMovieIds;
    }
}
