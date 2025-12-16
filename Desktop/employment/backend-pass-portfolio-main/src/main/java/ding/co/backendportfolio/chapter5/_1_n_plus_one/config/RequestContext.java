package ding.co.backendportfolio.chapter5._1_n_plus_one.config;

import ding.co.backendportfolio.chapter5.config.QueryType;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class RequestContext {
    private String httpMethod;
    private String bestMatchPath;
    private final Map<QueryType, Integer> queryCountByType = new HashMap<>();

    @Builder
    public RequestContext(String httpMethod, String bestMatchPath) {
        this.httpMethod = httpMethod;
        this.bestMatchPath = bestMatchPath;
    }

    public void incrementQueryCount(String sql) {
        QueryType queryType = QueryType.from(sql);
        queryCountByType.merge(queryType, 1, Integer::sum);
    }
}