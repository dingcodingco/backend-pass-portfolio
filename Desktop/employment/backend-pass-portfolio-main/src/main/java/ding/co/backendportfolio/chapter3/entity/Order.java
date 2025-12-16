package ding.co.backendportfolio.chapter3.entity;

import ding.co.backendportfolio.global.BaseTimeEntity;
import ding.co.backendportfolio.chapter2.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ch3_orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String orderNumber;  // 주문번호
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;  // 주문상태
    
    private LocalDateTime orderDate;  // 주문일시
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    private int totalAmount;  // 총 주문금액

    @Builder
    public Order(Member member, String orderNumber, OrderStatus status, LocalDateTime orderDate, int totalAmount) {
        this.member = member;
        this.orderNumber = orderNumber;
        this.status = status;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
    }
} 