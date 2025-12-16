package ding.co.backendportfolio.chapter3.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderStatisticsResponse {
    private String memberEmail;
    private Long totalOrders;
    private Long totalAmount;
    private Double averageAmount;
} 