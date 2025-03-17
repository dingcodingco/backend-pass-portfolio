package ding.co.backendportfolio.chapter5._2_bulk_operation;

import ding.co.backendportfolio.chapter5.config.QueryType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
public class BatchContext {
    private BatchName batchName;
    private final Map<QueryType, Integer> queryCountByType = new HashMap<>();

    public BatchContext(BatchName batchName) {
        this.batchName = batchName;
    }

    public void incrementQueryCount(String sql) {
        QueryType queryType = QueryType.from(sql);
        queryCountByType.merge(queryType, 1, Integer::sum);
    }

    /*
        ========================================
        # Batch Query Count Report
        - Batch Name: BULK_INSERT
        - Query Statistics:
          - INSERT : 1440
        ========================================
    */
    public void log() {
        StringBuilder logMessage = new StringBuilder("\n");
        logMessage.append("========================================").append("\n");
        logMessage.append("# Batch Query Count Report").append("\n");
        logMessage.append("- Batch Name: ").append(batchName).append("\n");
        logMessage.append("- Query Statistics:\n");
        // 쿼리 타입별로 정렬하여 출력
        queryCountByType.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    QueryType type = entry.getKey();
                    Integer count = entry.getValue();

                    logMessage.append(String.format("  - %-7s: %d\n", type, count));
                });

        logMessage.append("========================================").append("\n");

        log.info(logMessage.toString());
    }
}
