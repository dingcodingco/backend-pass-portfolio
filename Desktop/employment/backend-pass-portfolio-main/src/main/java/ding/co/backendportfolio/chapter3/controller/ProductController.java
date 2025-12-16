package ding.co.backendportfolio.chapter3.controller;

import ding.co.backendportfolio.chapter3.entity.ProductCategory;
import ding.co.backendportfolio.chapter3.dto.ProductResponse;
import ding.co.backendportfolio.chapter3.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chapter3/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProductsByCategory(
            @RequestParam ProductCategory category,
            Pageable pageable) {
        return ResponseEntity.ok(productService.findProductsByCategory(category, pageable));
    }
} 