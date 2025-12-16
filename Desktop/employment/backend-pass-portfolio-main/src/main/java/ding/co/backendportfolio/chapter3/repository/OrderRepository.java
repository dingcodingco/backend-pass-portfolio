package ding.co.backendportfolio.chapter3.repository;

import ding.co.backendportfolio.chapter3.entity.OrderStatus;
import ding.co.backendportfolio.chapter3.entity.Order;
import ding.co.backendportfolio.chapter3.dto.OrderStatisticsResponse;
import ding.co.backendportfolio.chapter3.repository.dto.OrderStatisticsProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // 복합 조건 검색 (여러 인덱스 필요)
    @Query("SELECT o FROM Order o " +
           "WHERE o.orderDate >= :startDate " +
           "AND o.status = :status " +
           "AND o.totalAmount >= :minAmount " +
           "ORDER BY o.orderDate DESC")
    Page<Order> findOrdersByComplexCondition(
            @Param("startDate") LocalDateTime startDate,
            @Param("status") OrderStatus status,
            @Param("minAmount") int minAmount,
            Pageable pageable);

    // OrderService에서 필요한 추가 메서드들
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    Page<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // 복합 인덱스 (member_id, order_date)를 자동으로 활용
    @Query(value = """
        SELECT o.* FROM ch3_orders o 
        WHERE o.member_id = :memberId 
        ORDER BY o.order_date DESC 
        LIMIT :limit
        """, nativeQuery = true)
    List<Order> findRecentOrdersByMemberId(
        @Param("memberId") Long memberId, 
        @Param("limit") int limit
    );
    

    @Query(value = """
        SELECT 
            m.email as memberEmail,
            COUNT(*) as totalOrders,
            SUM(o.total_amount) as totalAmount,
            AVG(o.total_amount) as averageAmount
        FROM ch3_orders o
        JOIN ch2_members m ON o.member_id = m.id
        GROUP BY m.email
        HAVING SUM(o.total_amount) >= :minAmount
        """, 
        countQuery = """
        SELECT COUNT(*) FROM (
            SELECT 1 FROM ch3_orders o
            JOIN ch2_members m ON o.member_id = m.id
            GROUP BY m.email
            HAVING SUM(o.total_amount) >= :minAmount
        ) as stats
        """,
        nativeQuery = true)
    Page<OrderStatisticsProjection> getOrderStatistics(@Param("minAmount") Long minAmount, Pageable pageable);
}
