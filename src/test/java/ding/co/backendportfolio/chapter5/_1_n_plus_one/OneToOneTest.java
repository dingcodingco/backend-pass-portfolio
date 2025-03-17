package ding.co.backendportfolio.chapter5._1_n_plus_one;

import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_one.ComputerRepository;
import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_one.MonitorRepository;
import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_one.OneToOneService;
import ding.co.backendportfolio.config.IntegrationTest;
import ding.co.backendportfolio.util.TestLogUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
class OneToOneTest {

    @Autowired
    private OneToOneService oneToOneService;

    @Autowired
    private ComputerRepository computerRepository;

    @Autowired
    private MonitorRepository monitorRepository;

    @BeforeEach
    void setup() {
        TestLogUtil.setUpStart();

        oneToOneService.saveComputerAndMonitor("computer1");
        oneToOneService.saveComputerAndMonitor("computer2");

        TestLogUtil.setUpEnd();
    }

    @AfterEach
    void cleanUp() {
        TestLogUtil.CleanStart();

        monitorRepository.deleteAllInBatch();
        computerRepository.deleteAllInBatch();

        TestLogUtil.CleanEnd();
    }

    @DisplayName("OneToOne 주인이 아닌 엔티티에서 lazy 조회")
    @Test
    void findAllComputer() {
        computerRepository.findAll();
    }
}