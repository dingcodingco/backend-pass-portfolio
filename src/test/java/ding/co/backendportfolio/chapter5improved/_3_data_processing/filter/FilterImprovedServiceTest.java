package ding.co.backendportfolio.chapter5improved._3_data_processing.filter;

import ding.co.backendportfolio.config.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class FilterImprovedServiceTest {

    @Autowired
    private FilterImprovedService filterImprovedService;

    @DisplayName("오름차순 필터 테스트")
    @RepeatedTest(20)
    void filterImproved() {
        filterImprovedService.filter();
    }
}