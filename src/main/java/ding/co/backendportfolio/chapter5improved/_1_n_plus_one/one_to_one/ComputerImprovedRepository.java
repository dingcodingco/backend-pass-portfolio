package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_one;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ComputerImprovedRepository extends JpaRepository<ComputerImproved, Long> {
    @Query("select c from ComputerImproved c join fetch c.monitorImproved")
    List<ComputerImproved> findAllWithMonitorImproved();
}
