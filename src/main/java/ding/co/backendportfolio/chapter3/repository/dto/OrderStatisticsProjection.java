package ding.co.backendportfolio.chapter3.repository.dto;

public interface OrderStatisticsProjection {
    String getMemberEmail();
    Long getTotalOrders();
    Long getTotalAmount();
    Double getAverageAmount();
}
