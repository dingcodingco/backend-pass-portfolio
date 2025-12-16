package ding.co.backendportfolio.chapter3.dto;

import ding.co.backendportfolio.chapter3.entity.ProductCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private ProductCategory category;
} 