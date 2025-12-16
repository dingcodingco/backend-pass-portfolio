package ding.co.backendportfolio.chapter5improved._2_bulk_operation;

import ding.co.backendportfolio.chapter5._2_bulk_operation.SubwayDataProvider;
import ding.co.backendportfolio.chapter5._2_bulk_operation.SubwayStats;
import ding.co.backendportfolio.chapter5._2_bulk_operation.SubwayStatsRepository;
import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class BulkOperationImprovedTest {

    @Autowired
    private BulkOperationImprovedService bulkOperationImprovedService;

    @Autowired
    private SubwayStatsRepository repository;

    @AfterEach
    void cleanUp() {
        TestLogUtil.CleanStart();
        repository.deleteAllInBatch();
        assertThat(repository.count()).isZero();
        TestLogUtil.CleanEnd();
    }

    @DisplayName("BulkInsertImproved")
    @Test
    void testBulkInsertImproved() {
        // when
        List<SubwayStats> data = SubwayDataProvider.createData();
        bulkOperationImprovedService.bulkInsertImproved(data);

        // then
        TestLogUtil.assertStart();
        assertThat(repository.count()).isEqualTo(1440);
        TestLogUtil.assertEnd();
    }

    @DisplayName("BulkDeleteImproved")
    @Test
    void testBulkDeleteImproved() {
        // given
        TestLogUtil.setUpStart();
        List<SubwayStats> data = SubwayDataProvider.createData();
        List<SubwayStats> subwayStats = repository.saveAll(data);
        assertThat(subwayStats).hasSize(1440);
        TestLogUtil.setUpEnd();

        // when
        bulkOperationImprovedService.bulkDeleteImproved();

        // then
        TestLogUtil.assertStart();
        assertThat(repository.count()).isZero();
        TestLogUtil.assertEnd();
    }
}