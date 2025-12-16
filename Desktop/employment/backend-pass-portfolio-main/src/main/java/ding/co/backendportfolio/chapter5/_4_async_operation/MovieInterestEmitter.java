package ding.co.backendportfolio.chapter5._4_async_operation;

import org.springframework.stereotype.Component;

@Component
public class MovieInterestEmitter {

    public void emitUserInterest(Long userNo, Long movieId) {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
        }
    }
}
