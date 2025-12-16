package ding.co.backendportfolio.chapter5._1_n_plus_one.one_to_one;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MonitorRepository extends JpaRepository<Monitor, Long> {
}
