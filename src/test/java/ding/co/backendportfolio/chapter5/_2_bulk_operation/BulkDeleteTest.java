package ding.co.backendportfolio.chapter5._2_bulk_operation;

import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@IntegrationTest
class BulkDeleteTest {

    @Autowired
    private SubwayStatBatchApplication subwayStatBatchApplication;

    @Autowired
    private SubwayStatsRepository repository;

    /**
     * - 삭제 테스트를 위한 초기 데이터를 생성합니다.
     */
    @BeforeEach
    public void setUp() {
        TestLogUtil.setUpStart();

        List<SubwayStats> subwayStats = repository.saveAll(TestDataUtil.createTestData());
        Assertions.assertThat(subwayStats).hasSize(1440);

        TestLogUtil.setUpEnd();
    }

    @DisplayName("BulkDelete")
    //    @RepeatedTest(value = 10)
    @Test
    void testBulkDelete() {
        subwayStatBatchApplication.bulkDelete();

        Assertions.assertThat(repository.count()).isZero();
    }
}