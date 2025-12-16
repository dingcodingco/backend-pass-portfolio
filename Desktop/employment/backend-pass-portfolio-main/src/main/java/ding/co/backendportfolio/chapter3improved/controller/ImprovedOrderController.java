package ding.co.backendportfolio.chapter3improved.controller;

import ding.co.backendportfolio.chapter3.dto.OrderStatisticsResponse;
import ding.co.backendportfolio.chapter3.entity.OrderStatus;
import ding.co.backendportfolio.chapter3improved.dto.ImprovedOrderResponse;
import ding.co.backendportfolio.chapter3improved.dto.ImprovedOrderDetailResponse;
import ding.co.backendportfolio.chapter3improved.service.ImprovedOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/chapter3improved/orders")
@RequiredArgsConstructor
public class ImprovedOrderController {

    private final ImprovedOrderService improvedOrderService;

    @GetMapping("/stats")
    public ResponseEntity<Page<OrderStatisticsResponse>> getOrderStatistics(
            @RequestParam(required = false) Long minAmount,
            Pageable pageable) {
        return ResponseEntity.ok(improvedOrderService.getOrderStatistics(minAmount, pageable));
    }

    @GetMapping("/complex-search")
    public ResponseEntity<Page<ImprovedOrderResponse>> searchOrders(
            @RequestParam LocalDateTime startDate,
            @RequestParam OrderStatus status,
            @RequestParam int minAmount,
            Pageable pageable) {
        return ResponseEntity.ok(improvedOrderService.searchOrders(startDate, status, minAmount, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<ImprovedOrderDetailResponse> searchByOrderNumber(
            @RequestParam String orderNumber) {
        return ResponseEntity.ok(improvedOrderService.findByOrderNumber(orderNumber));
    }
} 