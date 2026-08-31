package com.perf.lab.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.perf.lab.dtos.CreateOrderRequestDto;
import com.perf.lab.dtos.OrderItemRequestDto;
import com.perf.lab.dtos.UpdateOrderRequestDto;
import com.perf.lab.schema.OrderProducts;
import com.perf.lab.schema.OrderStatus;
import com.perf.lab.schema.Product;
import org.springframework.stereotype.Service;

import com.perf.lab.adapters.OrderAdapter;
import com.perf.lab.dtos.GetOrderResponseDto;
import com.perf.lab.exceptions.ResourceNotFoundException;
import com.perf.lab.repositories.OrderRespository;
import com.perf.lab.repositories.OrderproductsRepository;
import com.perf.lab.repositories.ProductRepository;
import com.perf.lab.schema.Order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRespository orderRespository;
    private final OrderproductsRepository orderproductsRepository;
    private final ProductRepository productRepository;
    private final OrderAdapter orderAdapter;

    public List<GetOrderResponseDto> getAllOrders() {

        List<Order> orders = orderRespository.findAll();
        return orderAdapter.mapToGetOrderResponseDtoList(orders);

    }

    public GetOrderResponseDto getOrderById(Long id) {
        Order order = orderRespository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return orderAdapter.mapToGetOrderResponseDto(order);
    }

    public void deleteOrder(Long id) {
        Order order = orderRespository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        orderRespository.delete(order);
    }

    @Transactional
    public GetOrderResponseDto createOrder(CreateOrderRequestDto createRequestDto) {

        Order order = Order.builder()
                .status(OrderStatus.PENDING)
                .build();

        orderRespository.save(order);

        if (createRequestDto.getOrderItems() != null) {
            List<Long> productIds = createRequestDto.getOrderItems().stream()
                    .map(OrderItemRequestDto::getProductId)
                    .collect(Collectors.toList());

            List<Product> products = productRepository.findAllById(productIds);

            Map<Long, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));

            for (Long productId : productIds) {
                if (!productMap.containsKey(productId)) {
                    throw new ResourceNotFoundException("Product not found with id: " + productId);
                }
            }

            List<OrderProducts> orderProducts = new ArrayList<>();

            for (var itemDto : createRequestDto.getOrderItems()) {
                Product product = productMap.get(itemDto.getProductId());

                orderProducts.add(OrderProducts.builder()
                        .order(order)
                        .product(product)
                        .quantity(itemDto.getQuantity() != null ? itemDto.getQuantity() : 1)
                        .build());
            }

            orderproductsRepository.saveAll(orderProducts);
        }

        return orderAdapter.mapToGetOrderResponseDto(order);
    }

    public GetOrderResponseDto updateOrder(Long id, UpdateOrderRequestDto updateOrderRequestDto) {
        Order order = orderRespository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (updateOrderRequestDto.getStatus() != null) {
            order.setStatus(updateOrderRequestDto.getStatus());
            orderRespository.save(order);
        }

        if (updateOrderRequestDto.getOrderItems() != null) {
            for (var itemDto : updateOrderRequestDto.getOrderItems()) {

                // process each item ---> N+1 queries: TODO
            }
        }

        return orderAdapter.mapToGetOrderResponseDto(order);
    }
}
