package ding.co.backendportfolio.chapter5improved._2_bulk_operation;

import ding.co.backendportfolio.chapter5._2_bulk_operation.SubwayDataProvider;
import ding.co.backendportfolio.chapter5._2_bulk_operation.SubwayStats;
import ding.co.backendportfolio.chapter5._2_bulk_operation.SubwayStatsRepository;
import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class BatchApplicationTest {

    @Autowired
    private BatchApplication batchApplication;

    @Autowired
    private SubwayStatsRepository repository;

    @AfterEach
    void cleanUp() {
        TestLogUtil.CleanStart();
        repository.deleteAllInBatch();
        assertThat(repository.count()).isZero();
        TestLogUtil.CleanEnd();
    }

    @Test
    void testBulkInsertWithMonitoring() {
        // when
        List<SubwayStats> data = SubwayDataProvider.createData();
        batchApplication.bulkInsertWithMonitoring(data);

        // then
        TestLogUtil.assertStart();
        assertThat(repository.count()).isEqualTo(1440);
        TestLogUtil.assertEnd();
    }

    @Test
    void testBulkDeleteWithMonitoring() {
        // given
        TestLogUtil.setUpStart();
        List<SubwayStats> data = SubwayDataProvider.createData();
        List<SubwayStats> subwayStats = repository.saveAll(data);
        assertThat(subwayStats).hasSize(1440);
        TestLogUtil.setUpEnd();

        // when
        batchApplication.bulkDeleteWithMonitoring();

        // then
        TestLogUtil.assertStart();
        assertThat(repository.count()).isZero();
        TestLogUtil.assertEnd();
    }
}