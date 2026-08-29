package com.perf.lab.services;

import com.perf.lab.dtos.CreateProductRequestDto;
import com.perf.lab.repositories.ProductRepository;
import com.perf.lab.schema.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found "));
    }

    public Product createProduct(CreateProductRequestDto requestDto) {
        Product product = Product.builder()
                .title(requestDto.getTitle())
                .description(requestDto.getDescription())
                .image(requestDto.getImage())
                .price(requestDto.getPrice())
                .category(requestDto.getCategory())
                .rating(requestDto.getRating()).build();

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
}
