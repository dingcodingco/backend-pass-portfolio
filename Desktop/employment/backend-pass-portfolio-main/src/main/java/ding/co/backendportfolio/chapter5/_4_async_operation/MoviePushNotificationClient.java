package ding.co.backendportfolio.chapter5._4_async_operation;

import org.springframework.stereotype.Component;

@Component
public class MoviePushNotificationClient {
    public void sendBookingConfirmation(Long userNo, Long bookingId) {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
        }
    }
}
