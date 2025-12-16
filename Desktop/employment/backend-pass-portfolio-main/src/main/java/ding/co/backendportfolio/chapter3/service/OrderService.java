package ding.co.backendportfolio.chapter3.service;

import ding.co.backendportfolio.chapter3.entity.Order;
import ding.co.backendportfolio.chapter3.entity.OrderStatus;
import ding.co.backendportfolio.chapter3.repository.OrderRepository;
import ding.co.backendportfolio.chapter3.dto.OrderDetailResponse;
import ding.co.backendportfolio.chapter3.dto.OrderResponse;
import ding.co.backendportfolio.chapter3.dto.OrderSearchResponse;
import ding.co.backendportfolio.chapter3.dto.OrderStatisticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Page<OrderResponse> findOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(this::toOrderResponse);
    }

    public OrderDetailResponse findOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return toOrderDetailResponse(order);
    }

    public Page<OrderResponse> findOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable)
                .map(this::toOrderResponse);
    }

    public Page<OrderResponse> findOrdersByPeriod(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return orderRepository.findByOrderDateBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59), pageable)
                .map(this::toOrderResponse);
    }

    public Page<OrderResponse> searchOrders(
            LocalDateTime startDate,
            OrderStatus status,
            int minAmount,
            Pageable pageable) {
        return orderRepository.findOrdersByComplexCondition(startDate, status, minAmount, pageable)
                .map(this::toOrderResponse);
    }

    public Page<OrderStatisticsResponse> getOrderStatistics(Long minAmount, Pageable pageable) {
        return orderRepository.getOrderStatistics(minAmount, pageable)
            .map(projection -> OrderStatisticsResponse.builder()
                .memberEmail(projection.getMemberEmail())
                .totalOrders(projection.getTotalOrders())
                .totalAmount(projection.getTotalAmount())
                .averageAmount(projection.getAverageAmount())
                .build());
    }

    public OrderDetailResponse findByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return toOrderDetailResponse(order);
    }

    private OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .memberEmail(order.getMember().getEmail())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .totalAmount((long) order.getTotalAmount())
                .totalItems(order.getOrderItems().size())
                .build();
    }

    private OrderDetailResponse toOrderDetailResponse(Order order) {
        return OrderDetailResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .memberEmail(order.getMember().getEmail())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .totalAmount((long) order.getTotalAmount())
                .items(order.getOrderItems().stream()
                        .map(item -> OrderDetailResponse.OrderItemResponse.builder()
                                .productId(item.getProduct().getId())
                                .productName(item.getProduct().getName())
                                .quantity(item.getQuantity())
                                .price((long) item.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
} 