package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_one;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OneToOneImprovedService {

    private final ComputerImprovedRepository computerImprovedRepository;
    private final MonitorImprovedRepository monitorImprovedRepository;

    @Transactional
    public void saveComputerAndMonitor(String name) {
        ComputerImproved computerImproved = computerImprovedRepository.save(new ComputerImproved(name));

        MonitorImproved monitorImproved = new MonitorImproved();
        computerImproved.addMonitorImproved(monitorImproved);

        monitorImprovedRepository.save(monitorImproved);
    }
}
