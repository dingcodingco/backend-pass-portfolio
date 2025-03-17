package ding.co.backendportfolio.chapter3improved.dto;

import ding.co.backendportfolio.chapter3.dto.OrderDetailResponse;
import ding.co.backendportfolio.chapter3.entity.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ImprovedOrderDetailResponse {
    private Long id;
    private String orderNumber;
    private String memberEmail;
    private OrderStatus status;
    private LocalDateTime orderDate;
    private long totalAmount;
    private int totalItems;
    private List<OrderDetailResponse.OrderItemResponse> orderItems;
} 