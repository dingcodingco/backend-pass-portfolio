package ding.co.backendportfolio.chapter5improved._2_bulk_operation;

import ding.co.backendportfolio.chapter5._2_bulk_operation.BulkOperationService;
import ding.co.backendportfolio.chapter5._2_bulk_operation.SubwayStats;
import ding.co.backendportfolio.chapter5improved._2_bulk_operation.monitoring.BatchContext;
import ding.co.backendportfolio.chapter5improved._2_bulk_operation.monitoring.BatchContextHolder;
import ding.co.backendportfolio.chapter5improved._2_bulk_operation.monitoring.BatchName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class BatchApplication {

    private final BulkOperationService bulkOperationService;

    public void bulkInsertWithMonitoring(List<SubwayStats> data) {
        // Batch 시작 시, 수동으로 BatchContext를 초기화
        BatchContextHolder.initContext(new BatchContext(BatchName.BULK_INSERT));

        bulkOperationService.bulkInsert(data);

        BatchContext batchContext = BatchContextHolder.getContext();
        if (batchContext != null) {
            batchContext.log();
        }

        // 메모리 누수 방지를 위해 반드시 호출 필요
        BatchContextHolder.clear();
    }

    public void bulkDeleteWithMonitoring() {
        // Batch 시작 시, 수동으로 BatchContext를 초기화
        BatchContextHolder.initContext(new BatchContext(BatchName.BULK_DELETE));

        bulkOperationService.bulkDelete();

        BatchContext batchContext = BatchContextHolder.getContext();
        if (batchContext != null) {
            batchContext.log();
        }

        // 메모리 누수 방지를 위해 반드시 호출 필요
        BatchContextHolder.clear();
    }
}
