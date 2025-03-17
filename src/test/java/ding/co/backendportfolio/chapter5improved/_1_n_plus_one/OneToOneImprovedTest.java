package ding.co.backendportfolio.chapter5improved._1_n_plus_one;

import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_one.ComputerImprovedRepository;
import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_one.MonitorImprovedRepository;
import ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_one.OneToOneImprovedService;
import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class OneToOneImprovedTest {

    @Autowired
    private OneToOneImprovedService oneToOneImprovedService;

    @Autowired
    private ComputerImprovedRepository computerImprovedRepository;

    @Autowired
    private MonitorImprovedRepository monitorImprovedRepository;

    @BeforeEach
    void setup() {
        TestLogUtil.setUpStart();

        oneToOneImprovedService.saveComputerAndMonitor("computer1");
        oneToOneImprovedService.saveComputerAndMonitor("computer2");

        TestLogUtil.setUpEnd();
    }

    @AfterEach
    void cleanUp() {
        TestLogUtil.CleanStart();

        monitorImprovedRepository.deleteAllInBatch();
        computerImprovedRepository.deleteAllInBatch();

        TestLogUtil.CleanEnd();
    }

    @DisplayName("OneToOne 주인이 아닌 엔티티에서 조회 - fetch join")
    @Test
    void findAllComputer() {
        computerImprovedRepository.findAllWithMonitorImproved();
    }
}