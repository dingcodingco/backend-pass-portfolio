package ding.co.backendportfolio.chapter5._2_bulk_operation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class SubwayStatBatchApplication {

    private final BulkOperationService bulkOperationService;
    private final BatchExecutionResultEmitter batchExecutionResultEmitter;

    public void bulkInsert() {
        // HTTP 요청으로 처리되지 않는다고 가정하고 수동으로 BatchContext를 초기화
        BatchContextHolder.initContext(new BatchContext(BatchName.BULK_INSERT));

        List<SubwayStats> data = createData();
        bulkOperationService.insertBulkData(data);

        BatchContext batchContext = BatchContextHolder.getContext();
        if (batchContext != null) {
            batchExecutionResultEmitter.emit(batchContext);
        }

        // 메모리 누수 방지를 위해 반드시 호출 필요
        BatchContextHolder.clear();
    }

    public void bulkDelete() {
        // HTTP 요청으로 처리되지 않는다고 가정하고 수동으로 BatchContext를 초기화
        BatchContextHolder.initContext(new BatchContext(BatchName.BULK_DELETE));

        bulkOperationService.deleteBulkData();

        BatchContext batchContext = BatchContextHolder.getContext();
        if (batchContext != null) {
            batchExecutionResultEmitter.emit(batchContext);
        }

        // 메모리 누수 방지를 위해 반드시 호출 필요
        BatchContextHolder.clear();
    }

    /**
     * - 테스트 데이터 생성
     * - 10개 역 = 1Set
     * - 24시간(1440분)을 10 분 간격으로 144 개의 Set 데이터 생성
     * => 10 * 144 = 1440개의 데이터 생성
     */
    private static List<SubwayStats> createData() {
        List<SubwayStats> testData = new ArrayList<>(1440);

        LocalDateTime startTime = LocalDate.now().atStartOfDay()
                .minusDays(1L)
                .truncatedTo(ChronoUnit.HOURS);

        for (int station = 1; station <= 10; station++) {
            for (int min = 0; min < 1440; min += 10) {
                LocalDateTime recordTime = startTime.plusMinutes(min);
                SubwayStats subwayStats = new SubwayStats("역-" + station, recordTime);
                testData.add(subwayStats);
            }
        }

        return testData;
    }
}
