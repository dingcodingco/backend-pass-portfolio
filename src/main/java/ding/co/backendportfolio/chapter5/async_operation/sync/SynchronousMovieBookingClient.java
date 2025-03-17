package ding.co.backendportfolio.chapter5.async_operation.sync;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class SynchronousMovieBookingClient {

    public boolean isAvailable(long movieId, long delaySeconds) throws InterruptedException {
        Thread.sleep(delaySeconds * 1000L);
        return ThreadLocalRandom.current().nextBoolean();
    }
}
