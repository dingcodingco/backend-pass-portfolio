package ding.co.backendportfolio.chapter3.service;

import ding.co.backendportfolio.chapter3.repository.ProductRepository;
import ding.co.backendportfolio.chapter3.entity.Product;
import ding.co.backendportfolio.chapter3.entity.ProductCategory;
import ding.co.backendportfolio.chapter3.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponse> findProductsByCategory(ProductCategory category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable)
                .map(this::toProductResponse);
    }

    private ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .build();
    }
} 