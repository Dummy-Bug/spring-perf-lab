package com.perf.lab.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.perf.lab.dtos.CreateProductRequestDto;
import com.perf.lab.dtos.GetProductResponseDto;
import com.perf.lab.dtos.GetProductWithDetailsResponseDto;
import com.perf.lab.schema.Product;
import com.perf.lab.services.ProductService;
import com.perf.lab.utils.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetProductResponseDto>>> getAllProducts() {
        List<GetProductResponseDto> products = productService.getAllProducts();
        return ResponseEntity
                .ok(ApiResponse.success(products, "Products fetched successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GetProductResponseDto>> getProductById(@PathVariable Long id) {
        GetProductResponseDto product = productService.getProductById(id);
        return ResponseEntity
                .ok(ApiResponse.success(product, "Product fetched successfully"));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<GetProductWithDetailsResponseDto>> getProductWithDetailsById(@PathVariable Long id) {
        GetProductWithDetailsResponseDto product = productService.getProductWithDetailsById(id);
        return ResponseEntity
                .ok(ApiResponse.success(product, "Product details fetched successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody CreateProductRequestDto requestDto) {
        Product product = productService.createProduct(requestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(product, "Product created successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity
                .ok(ApiResponse.success(null, "Product deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCategory(@RequestParam("categoryName") String category) {
        List<Product> products = productService.getProductsByCategory(category);
        return ResponseEntity
                .ok(ApiResponse.success(products, "Products fetched successfully"));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        List<String> categories = productService.getAllCategories();
        return ResponseEntity
                .ok(ApiResponse.success(categories, "Categories fetched successfully"));
    }
}

