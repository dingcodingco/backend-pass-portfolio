package ding.co.backendportfolio.chapter2.repository;

import ding.co.backendportfolio.chapter2.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
} 