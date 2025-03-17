package ding.co.backendportfolio.chapter5._2_bulk_operation;

import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class BulkInsertTest {

    @Autowired
    private SubwayStatBatchApplication SubwayStatBatchApplication;

    @Autowired
    private SubwayStatsRepository repository;

    @AfterEach
    public void cleanUp() {
        TestLogUtil.CleanStart();

        repository.deleteAllInBatch();

        TestLogUtil.CleanEnd();
    }

    @DisplayName("BulkInsert")
    //    @RepeatedTest(value = 10)
    @Test
    void testBulkInsert() {
        SubwayStatBatchApplication.bulkInsert();

        Assertions.assertThat(repository.count()).isEqualTo(1440);
    }
}