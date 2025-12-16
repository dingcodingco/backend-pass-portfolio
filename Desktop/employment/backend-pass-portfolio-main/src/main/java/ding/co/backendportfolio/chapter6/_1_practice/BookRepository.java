package ding.co.backendportfolio.chapter6._1_practice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIdAndName(Long id, String name);

    List<Book> findByNameContaining(String name);
}
