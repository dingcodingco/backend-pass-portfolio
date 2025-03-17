package ding.co.backendportfolio.chapter3improved.service;

import ding.co.backendportfolio.chapter3.dto.OrderStatisticsResponse;
import ding.co.backendportfolio.chapter3.entity.OrderStatus;
import ding.co.backendportfolio.chapter3improved.dto.ImprovedOrderDetailResponse;
import ding.co.backendportfolio.chapter3improved.dto.ImprovedOrderResponse;
import ding.co.backendportfolio.chapter3improved.entity.ImprovedOrder;
import ding.co.backendportfolio.chapter3improved.entity.OrderStats;
import ding.co.backendportfolio.chapter3improved.repository.ImprovedOrderRepository;
import ding.co.backendportfolio.chapter3improved.repository.OrderStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImprovedOrderService {

    private final ImprovedOrderRepository improvedOrderRepository;
    private final OrderStatsRepository orderStatsRepository;

    @Transactional(readOnly = true)
    public Page<OrderStatisticsResponse> getOrderStatistics(Long minAmount, Pageable pageable) {
        Page<OrderStats> statsPage = orderStatsRepository.findByTotalAmountGreaterThanEqual(
                minAmount != null ? minAmount : 0L,
                pageable
        );

        return statsPage.map(this::toOrderStatisticsResponse);
    }

    //    @Scheduled(cron = "0 */3 * * * *")
    @Transactional
    public void refreshOrderStatistics() {
        log.info("Starting order statistics refresh at {}", LocalDateTime.now());
        try {
            // DB에서 직접 통계 데이터 집계 및 저장
            orderStatsRepository.refreshOrderStats();
            log.info("Order statistics refreshed successfully");
        } catch (Exception e) {
            log.error("Failed to refresh order statistics", e);
            throw e;
        }
    }

    private OrderStatisticsResponse toOrderStatisticsResponse(OrderStats stats) {
        return OrderStatisticsResponse.builder()
                .memberEmail(stats.getEmail())
                .totalOrders((long) stats.getOrderCount())
                .totalAmount(stats.getTotalAmount())
                .averageAmount(stats.getAvgAmount())
                .build();
    }

    public Page<ImprovedOrderResponse> searchOrders(
            LocalDateTime startDate,
            OrderStatus status,
            int minAmount,
            Pageable pageable) {
        return improvedOrderRepository.findOrdersByComplexCondition(startDate, status, minAmount, pageable)
                .map(this::toOrderResponse);
    }

    private ImprovedOrderResponse toOrderResponse(ImprovedOrder order) {
        return ImprovedOrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .memberEmail(order.getMember().getEmail())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .totalItems(order.getTotalItems())
                .build();
    }

    public ImprovedOrderDetailResponse findByOrderNumber(String orderNumber) {
        ImprovedOrder order = improvedOrderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return toOrderDetailResponse(order);
    }

    private ImprovedOrderDetailResponse toOrderDetailResponse(ImprovedOrder order) {
        return ImprovedOrderDetailResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .memberEmail(order.getMember().getEmail())
                .status(order.getStatus())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .totalItems(order.getTotalItems())
                .build();
    }
} 