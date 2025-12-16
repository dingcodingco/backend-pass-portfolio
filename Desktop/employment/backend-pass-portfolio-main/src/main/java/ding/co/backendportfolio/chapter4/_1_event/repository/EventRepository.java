package ding.co.backendportfolio.chapter4._1_event.repository;

import ding.co.backendportfolio.chapter4._1_event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
}