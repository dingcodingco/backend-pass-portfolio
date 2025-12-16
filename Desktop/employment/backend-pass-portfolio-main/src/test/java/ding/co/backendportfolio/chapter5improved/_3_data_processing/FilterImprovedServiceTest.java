package ding.co.backendportfolio.chapter5improved._3_data_processing;

import ding.co.backendportfolio.chapter5._3_data_processing.FilterServiceDataProvider;
import ding.co.backendportfolio.chapter5._3_data_processing.Post;
import ding.co.backendportfolio.config.IntegrationTest;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@IntegrationTest
class FilterImprovedServiceTest {

    public static final int REPEAT_COUNT = 10;
    public static final int WARM_UP_COUNT = 5;

    @Autowired
    private FilterImprovedService filterImprovedService;

    @Autowired
    private MeterRegistry meterRegistry;

    @DisplayName("효율적 필터 순서 테스트")
    @RepeatedTest(REPEAT_COUNT)
    void testGoodFilter(RepetitionInfo repetitionInfo) {
        // given
        Timer goodFilterTimer = meterRegistry.timer("goodFilterTimer");
        List<Post> testData = FilterServiceDataProvider.createTestData();

        // when
        if (repetitionInfo.getCurrentRepetition() <= WARM_UP_COUNT) {
            filterImprovedService.goodFilter(testData);
        } else {
            goodFilterTimer.record(() -> filterImprovedService.goodFilter(testData));
        }

        // then
        if (repetitionInfo.getCurrentRepetition() == REPEAT_COUNT) {
            double mean = goodFilterTimer.mean(TimeUnit.MILLISECONDS);
            log.info("Good filter timer - mean={}ms", String.format("%.2f", mean));
        }
    }
}