package ding.co.backendportfolio.chapter5._3_data_processing;

import org.springframework.stereotype.Component;

@Component
public class SensitiveWordCheckApiClient {

    /**
     * - 비용: 10
     * - 선택도: 0.33
     * => FilterOverhead: 10 * 0.33 = 3.3
     */
    public boolean containsNegativeWords(Post post) {
        FilterUtil.simulateCost(10);
        return FilterUtil.simulateSelectivity(0.33);
    }
}
