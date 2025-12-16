package ding.co.backendportfolio.chapter5._2_bulk_operation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubwayStatsRepository extends JpaRepository<SubwayStats, Long> {
}