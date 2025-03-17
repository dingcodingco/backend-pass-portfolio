package ding.co.backendportfolio.chapter2.repository;

import ding.co.backendportfolio.chapter2.entity.Board;
import ding.co.backendportfolio.chapter2.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByCategory(Category category, Pageable pageable);
    
    @Query("SELECT b FROM Board b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    Page<Board> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Page<Board> findByTitleContainingOrContentContaining(
            String title, String content, Pageable pageable);

    List<Board> findByBoardTags_Tag_Name(String tagName);

    @Query("SELECT b FROM Board b " +
           "LEFT JOIN b.boardTags bt " +
           "LEFT JOIN bt.tag t " +
           "WHERE (:category IS NULL OR b.category = :category) " +
           "AND (:keyword IS NULL OR b.title LIKE %:keyword% OR b.content LIKE %:keyword%) " +
           "AND (:tags IS NULL OR t.name IN :tags) " +
           "GROUP BY b " +
           "HAVING COUNT(DISTINCT t.name) = :tagCount")
    Page<Board> searchBoardsComplex(
            @Param("keyword") String keyword,
            @Param("category") Category category,
            @Param("tags") List<String> tags,
            @Param("tagCount") long tagCount,
            Pageable pageable);

    @Query("SELECT new map(" +
           "DATE(b.createdAt) as date, " +
           "COUNT(b) as postCount, " +
           "COUNT(DISTINCT b.member) as uniqueAuthors, " +
           "AVG(SIZE(b.likes)) as avgLikes) " +
           "FROM Board b " +
           "WHERE b.createdAt >= :startDate " +
           "GROUP BY DATE(b.createdAt)")
    List<Map<String, Object>> getBoardAnalytics(@Param("startDate") LocalDateTime startDate);

    Page<Board> findByBoardTags_Tag_NameIn(List<String> tagNames, Pageable pageable);

    @Query("SELECT b FROM Board b " +
           "WHERE b.id != :boardId " +
           "AND b.category = :category " +
           "ORDER BY b.createdAt DESC")
    List<Board> findSimilarBoards(
            @Param("boardId") Long boardId,
            @Param("category") Category category,
            Pageable pageable);
}