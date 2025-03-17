package ding.co.backendportfolio.chapter5.async_operation.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncMovieInterestEmitter {

    @Async("emitExecutor")
    public void emitUserInterest(Long userNo, Long movieId, long l) throws InterruptedException {
        Thread.sleep(l * 1000L);
    }
}
