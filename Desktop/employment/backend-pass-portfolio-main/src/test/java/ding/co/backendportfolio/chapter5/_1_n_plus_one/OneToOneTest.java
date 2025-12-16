package ding.co.backendportfolio.chapter5._1_n_plus_one;

import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_one.Computer;
import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_one.ComputerRepository;
import ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_one.Monitor;
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
    private ComputerRepository computerRepository;

    @BeforeEach
    void setup() {
        TestLogUtil.setUpStart();

        saveComputerAndMonitor("computer1");
        saveComputerAndMonitor("computer2");

        TestLogUtil.setUpEnd();
    }

    @AfterEach
    void cleanUp() {
        TestLogUtil.CleanStart();

        computerRepository.deleteAll();

        TestLogUtil.CleanEnd();
    }

    @DisplayName("OneToOne 주인이 아닌 엔티티에서 lazy 조회")
    @Test
    void findAllComputer() {
        computerRepository.findAll();
    }

    private void saveComputerAndMonitor(String computerName) {
        Computer computer = new Computer(computerName);
        Monitor monitor = new Monitor();
        computer.addMonitor(monitor);

        computerRepository.save(computer);
    }
}