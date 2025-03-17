package ding.co.backendportfolio.chapter5._2_bulk_operation;

import org.springframework.stereotype.Component;

@Component
public class BatchExecutionResultEmitter {

    public void emit(BatchContext batchContext) {
        batchContext.log();
    }
}
