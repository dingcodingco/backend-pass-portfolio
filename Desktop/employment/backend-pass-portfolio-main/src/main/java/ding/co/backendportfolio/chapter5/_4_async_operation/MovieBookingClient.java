package ding.co.backendportfolio.chapter5._4_async_operation;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class MovieBookingClient {

    public boolean isAvailable(long movieId) {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
        }
        return ThreadLocalRandom.current().nextBoolean();
    }
}
