package ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_one;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OneToOneService {

    private final ComputerRepository computerRepository;
    private final MonitorRepository monitorRepository;

    @Transactional
    public void saveComputerAndMonitor(String name) {
        Computer computer = computerRepository.save(new Computer(name));

        Monitor monitor = new Monitor();
        computer.addMonitor(monitor);

        monitorRepository.save(monitor);
    }
}
