package ding.co.backendportfolio.chapter3.repository;

import ding.co.backendportfolio.chapter3.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepositoryOptimized extends JpaRepository<Order, Long> {
    
    // 1. 주문번호 검색 최적화 (인덱스 활용)
    @Query(value = "SELECT o.* FROM ch4_orders o " +
           "WHERE o.order_number LIKE :keyword% " +  // prefix 검색은 인덱스 활용 가능
           "LIMIT 100", nativeQuery = true)  // 결과 제한
    List<Order> findByOrderNumberStartsWithOptimized(@Param("keyword") String keyword);

    // 2. 주문금액 범위 검색 최적화 (인덱스 + 페이징)
    @Query(value = "SELECT o.* FROM ch4_orders o " +
           "WHERE o.total_amount BETWEEN :minAmount AND :maxAmount " +
           "ORDER BY o.total_amount DESC " +
           "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<Order> findByTotalAmountBetweenOptimized(
            @Param("minAmount") int minAmount,
            @Param("maxAmount") int maxAmount,
            @Param("offset") int offset,
            @Param("limit") int limit);

    // 3. 회원별 주문 통계 최적화 (인덱스 + 임시 테이블 활용)
    @Query(value = 
           "WITH member_stats AS (" +
           "    SELECT o.member_id, COUNT(*) as order_count, SUM(o.total_amount) as total_amount " +
           "    FROM ch4_orders o " +
           "    FORCE INDEX (idx_orders_member_amount) " +  // 인덱스 힌트
           "    GROUP BY o.member_id " +
           "    HAVING SUM(o.total_amount) > :minAmount" +
           ") " +
           "SELECT * FROM member_stats " +
           "ORDER BY total_amount DESC " +
           "LIMIT 100", nativeQuery = true)
    List<Object[]> findOrderStatsByMemberOptimized(@Param("minAmount") int minAmount);

    // 4. 복합 조건 검색 최적화 (복합 인덱스 활용)
    @Query(value = "SELECT o.* FROM ch4_orders o " +
           "FORCE INDEX (idx_orders_date_status_amount) " +  // 복합 인덱스 힌트
           "WHERE o.order_date >= :startDate " +
           "AND o.status = :status " +
           "AND o.total_amount >= :minAmount " +
           "ORDER BY o.order_date DESC " +
           "LIMIT :limit", nativeQuery = true)
    List<Order> findOrdersByComplexConditionOptimized(
            @Param("startDate") LocalDateTime startDate,
            @Param("status") String status,
            @Param("minAmount") int minAmount,
            @Param("limit") int limit);

    // 5. N+1 문제 해결을 위한 배치 조회 최적화
    @Query(value = 
           "SELECT o.*, m.*, oi.*, p.* FROM ch4_orders o " +
           "INNER JOIN members m ON o.member_id = m.id " +
           "LEFT JOIN ch4_order_items oi ON o.id = oi.order_id " +
           "LEFT JOIN ch4_products p ON oi.product_id = p.id " +
           "WHERE o.order_date BETWEEN :startDate AND :endDate " +
           "AND o.status = :status " +
           "LIMIT :limit", nativeQuery = true)
    List<Order> findOrdersWithDetailsOptimized(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") String status,
            @Param("limit") int limit);
}