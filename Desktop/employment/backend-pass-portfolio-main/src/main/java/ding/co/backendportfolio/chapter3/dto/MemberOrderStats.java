package ding.co.backendportfolio.chapter3.dto;

import ding.co.backendportfolio.chapter3.entity.Order;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MemberOrderStats {
    private String memberEmail;
    private int orderCount;
    private long totalAmount;
    private double averageAmount;
    private LocalDateTime lastOrderDate;
    
    public static MemberOrderStats from(List<Order> orders, String memberEmail) {
        return MemberOrderStats.builder()
                .memberEmail(memberEmail)
                .orderCount(orders.size())
                .totalAmount(calculateTotalAmount(orders))
                .averageAmount(calculateAverageAmount(orders))
                .lastOrderDate(findLastOrderDate(orders))
                .build();
    }
    
    private static long calculateTotalAmount(List<Order> orders) {
        return orders.stream()
                .mapToLong(Order::getTotalAmount)
                .sum();
    }
    
    private static double calculateAverageAmount(List<Order> orders) {
        return orders.stream()
                .mapToLong(Order::getTotalAmount)
                .average()
                .orElse(0.0);
    }
    
    private static LocalDateTime findLastOrderDate(List<Order> orders) {
        return orders.stream()
                .map(Order::getOrderDate)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }
}