package ding.co.backendportfolio.chapter5._3_data_processing.filter;

import ding.co.backendportfolio.config.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class FilterServiceTest {

    @Autowired
    private FilterService filterService;


    @DisplayName("내림차순 필터 순서 테스트")
    @RepeatedTest(20)
    void filter() {
        filterService.filter();
    }
}