package ding.co.backendportfolio.chapter5improved._1_n_plus_one.one_to_many.entity_graph;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelEntityGraphRepository extends JpaRepository<HotelEntityGraph, Long> {
    @EntityGraph(attributePaths = "roomEntityGraphs")
    List<HotelEntityGraph> findAll();
}
