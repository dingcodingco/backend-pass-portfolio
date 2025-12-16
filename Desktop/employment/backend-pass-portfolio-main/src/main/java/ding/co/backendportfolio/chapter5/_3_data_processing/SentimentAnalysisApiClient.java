package ding.co.backendportfolio.chapter5._3_data_processing;

import org.springframework.stereotype.Component;

@Component
public class SentimentAnalysisApiClient {

    /**
     * - 비용: 10
     * - 선택도: 0.5
     * => FilterOverhead: 10 * 0.5 = 5
     */
    public boolean isPositiveSentiment(Post post) {
        FilterUtil.simulateCost(10);
        return FilterUtil.simulateSelectivity(0.5);
    }
}
