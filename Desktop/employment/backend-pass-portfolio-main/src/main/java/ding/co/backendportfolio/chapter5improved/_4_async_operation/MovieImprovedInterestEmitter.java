package ding.co.backendportfolio.chapter5improved._4_async_operation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MovieImprovedInterestEmitter {

    @Async("asyncExecutor")
    public void emitUserInterest(Long userNo, Long movieId) {
        try {
            Thread.sleep(200L);
        } catch (InterruptedException e) {
        }
    }
}
