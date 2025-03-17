package ding.co.backendportfolio.chapter5._3_data_processing.loop;

import ding.co.backendportfolio.config.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class LoopServiceTest {

    @Autowired
    private LoopService loopService;

    @DisplayName("O(N^2) Loop 테스트")
    @RepeatedTest(20)
    void execute() {
        loopService.execute(LoopTestDataUtil.createTestData());
    }
}
