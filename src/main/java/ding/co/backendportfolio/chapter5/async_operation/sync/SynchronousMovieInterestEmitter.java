package ding.co.backendportfolio.chapter5.async_operation.sync;

import org.springframework.stereotype.Component;

@Component
public class SynchronousMovieInterestEmitter {

    public void emitUserInterest(Long userNo, Long movieId, long delaySeconds) throws InterruptedException {
        Thread.sleep(delaySeconds * 1000L);
    }
}
