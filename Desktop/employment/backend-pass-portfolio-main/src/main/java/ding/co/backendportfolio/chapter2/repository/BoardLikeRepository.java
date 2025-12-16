package ding.co.backendportfolio.chapter2.repository;

import ding.co.backendportfolio.chapter2.entity.BoardLike;
import ding.co.backendportfolio.chapter2.entity.Member;
import ding.co.backendportfolio.chapter2.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByBoardAndMember(Board board, Member member);
} 