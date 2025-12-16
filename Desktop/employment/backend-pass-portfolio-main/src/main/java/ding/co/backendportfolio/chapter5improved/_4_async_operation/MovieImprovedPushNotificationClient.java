package ding.co.backendportfolio.chapter5improved._4_async_operation;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class MovieImprovedPushNotificationClient {

    @Async("asyncExecutor")
    public CompletableFuture<Void> sendBookingConfirmation(Long userNo, Long bookingId) {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
        }
        return CompletableFuture.completedFuture(null);
    }
}
