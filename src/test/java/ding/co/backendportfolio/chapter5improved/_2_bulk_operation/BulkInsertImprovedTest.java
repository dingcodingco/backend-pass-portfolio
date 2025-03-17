package ding.co.backendportfolio.chapter5improved._2_bulk_operation;

import ding.co.backendportfolio.chapter5._2_bulk_operation.SubwayStatsRepository;
import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class BulkInsertImprovedTest {

    @Autowired
    private SubwayStatBatchApplicationImproved subwayStatBatchApplicationImproved;

    @Autowired
    private SubwayStatsRepository repository;

    @BeforeEach
    public void cleanUp() {
        TestLogUtil.setUpStart();

        repository.deleteAllInBatch();

        TestLogUtil.setUpEnd();
    }

    @DisplayName("BulkInsertImproved")
//    @RepeatedTest(value = 10)
    @Test
    void testBulkInsertImproved() {
        subwayStatBatchApplicationImproved.bulkInsertImproved();

        Assertions.assertThat(repository.count()).isEqualTo(1440);
    }
}