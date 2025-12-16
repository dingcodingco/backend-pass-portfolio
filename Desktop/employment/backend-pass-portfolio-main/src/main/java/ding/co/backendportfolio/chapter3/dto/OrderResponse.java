package ding.co.backendportfolio.chapter3.dto;

import ding.co.backendportfolio.chapter3.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private String memberEmail;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private Long totalAmount;
    private int totalItems;
} 