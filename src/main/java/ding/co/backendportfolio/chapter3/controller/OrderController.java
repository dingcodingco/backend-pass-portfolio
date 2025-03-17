package ding.co.backendportfolio.chapter3.controller;

import ding.co.backendportfolio.chapter3.dto.OrderDetailResponse;
import ding.co.backendportfolio.chapter3.dto.OrderResponse;
import ding.co.backendportfolio.chapter3.dto.OrderStatisticsResponse;
import ding.co.backendportfolio.chapter3.entity.OrderStatus;
import ding.co.backendportfolio.chapter3.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/chapter3/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrders(Pageable pageable) {
        return ResponseEntity.ok(orderService.findOrders(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getOrderDetail(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.findOrderById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<OrderResponse>> getOrdersByStatus(
            @PathVariable OrderStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.findOrdersByStatus(status, pageable));
    }

    @GetMapping("/period")
    public ResponseEntity<Page<OrderResponse>> getOrdersByPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.findOrdersByPeriod(startDate, endDate, pageable));
    }

    @GetMapping("/complex-search")
    public ResponseEntity<Page<OrderResponse>> complexSearch(
            @RequestParam LocalDateTime startDate,
            @RequestParam OrderStatus status,
            @RequestParam int minAmount,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.searchOrders(startDate, status, minAmount, pageable));
    }

    @GetMapping("/stats")
    public ResponseEntity<Page<OrderStatisticsResponse>> getOrderStatistics(
            @RequestParam(required = false) Long minAmount,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrderStatistics(minAmount, pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<OrderDetailResponse> searchByOrderNumber(
            @RequestParam String orderNumber) {
        return ResponseEntity.ok(orderService.findByOrderNumber(orderNumber));
    }
} 