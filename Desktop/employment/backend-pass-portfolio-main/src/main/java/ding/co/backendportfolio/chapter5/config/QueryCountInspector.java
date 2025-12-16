package ding.co.backendportfolio.chapter5.config;

import ding.co.backendportfolio.chapter5._1_n_plus_one.config.RequestContext;
import ding.co.backendportfolio.chapter5._1_n_plus_one.config.RequestContextHolder;
import ding.co.backendportfolio.chapter5improved._2_bulk_operation.monitoring.BatchContext;
import ding.co.backendportfolio.chapter5improved._2_bulk_operation.monitoring.BatchContextHolder;
import org.hibernate.resource.jdbc.spi.StatementInspector;

public class QueryCountInspector implements StatementInspector {
    @Override
    public String inspect(String sql) {
        // HTTP 요청 컨텍스트
        RequestContext requestContext = RequestContextHolder.getContext();
        if (requestContext != null) {
            requestContext.incrementQueryCount(sql);
        }

        // 배치 컨텍스트
        BatchContext batchContext = BatchContextHolder.getContext();
        if (batchContext != null) {
            batchContext.incrementQueryCount(sql);
        }

        return sql;
    }
}
