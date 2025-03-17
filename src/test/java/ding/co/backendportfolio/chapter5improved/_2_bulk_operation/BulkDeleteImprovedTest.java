package ding.co.backendportfolio.chapter5improved._2_bulk_operation;

import ding.co.backendportfolio.chapter5._2_bulk_operation.SubwayStats;
import ding.co.backendportfolio.chapter5._2_bulk_operation.SubwayStatsRepository;
import ding.co.backendportfolio.chapter5._2_bulk_operation.TestDataUtil;
import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@IntegrationTest
class BulkDeleteImprovedTest {

    @Autowired
    private SubwayStatBatchApplicationImproved subwayStatBatchApplicationImproved;

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

    @DisplayName("BulkDeleteImproved")
//    @RepeatedTest(value = 10)
    @Test
    void testBulkDeleteImproved() {
        subwayStatBatchApplicationImproved.bulkDeleteImproved();

        Assertions.assertThat(repository.count()).isZero();
    }
}