package com.perf.lab.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.perf.lab.dtos.*;
import com.perf.lab.schema.OrderProducts;
import com.perf.lab.schema.OrderStatus;
import com.perf.lab.schema.Product;
import org.springframework.stereotype.Service;

import com.perf.lab.adapters.OrderAdapter;
import com.perf.lab.exceptions.ResourceNotFoundException;
import com.perf.lab.repositories.OrderRepository;
import com.perf.lab.repositories.OrderProductsRepository;
import com.perf.lab.repositories.ProductRepository;
import com.perf.lab.schema.Order;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderProductsRepository orderproductsRepository;
    private final ProductRepository productRepository;
    private final OrderAdapter orderAdapter;

    public List<GetOrderResponseDto> getAllOrders() {

        List<Order> orders = orderRepository.findAll();
        return orderAdapter.mapToGetOrderResponseDtoList(orders);

    }

    public GetOrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return orderAdapter.mapToGetOrderResponseDto(order);
    }

    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        orderRepository.delete(order);
    }

    @Transactional
    public GetOrderResponseDto createOrder(CreateOrderRequestDto createRequestDto) {

        Order order = Order.builder()
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);

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
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (updateOrderRequestDto.getStatus() != null) {
            order.setStatus(updateOrderRequestDto.getStatus());
            orderRepository.save(order);
        }

        if (updateOrderRequestDto.getOrderItems() != null) {
            List<Long> productIds = updateOrderRequestDto.getOrderItems().stream()
                    .map(OrderItemActionDto::getProductId)
                    .toList();

            List<Product> products = productRepository.findAllById(productIds);

            Map<Long, Product> productMap = products.stream()
                    .collect(Collectors.toMap(Product::getId, Function.identity()));

            for (Long productId : productIds) {
                if (!productMap.containsKey(productId)) {
                    throw new ResourceNotFoundException("Product not found with id: " + productId);
                }
            }

            List<OrderProducts> toSave = new ArrayList<>();
            List<OrderProducts> toDelete = new ArrayList<>();

            Map<Long, OrderProducts> existingItems = orderproductsRepository.findByOrderWithProduct(order).stream()
                    .collect(Collectors.toMap(op -> op.getProduct().getId(), Function.identity()));


            for (OrderItemActionDto itemAction : updateOrderRequestDto.getOrderItems()) {

                Product product = productMap.get(itemAction.getProductId());
                OrderProducts existing = existingItems.get(product.getId());

                switch (itemAction.getAction()) {
                    case ADD -> {
                        if (existing != null) {
                            int addQty = (itemAction.getQuantity() != null ? itemAction.getQuantity() : 1);
                            existing.setQuantity(existing.getQuantity() + addQty);
                            toSave.add(existing);
                        } else {
                            OrderProducts newItem = OrderProducts.builder()
                                    .order(order)
                                    .product(product)
                                    .quantity(itemAction.getQuantity() != null ? itemAction.getQuantity() : 1)
                                    .build();
                            existingItems.put(product.getId(), newItem);
                            toSave.add(newItem);
                        }
                    }
                    case REMOVE -> {
                        if (existing == null) {
                            throw new ResourceNotFoundException("Product not found with id: " + product.getId());
                        }
                        toDelete.add(existing);
                        existingItems.remove(product.getId());
                    }
                    case INCREMENT -> {
                        if (existing == null) {
                            throw new ResourceNotFoundException("Product not found with id: " + product.getId());
                        }
                        existing.setQuantity(existing.getQuantity() + 1);
                        toSave.add(existing);
                    }
                    case DECREMENT -> {
                        if (existing == null) {
                            throw new ResourceNotFoundException("Product not found with id: " + product.getId());
                        }
                        if (existing.getQuantity() <= 1) {
                            toDelete.add(existing);
                            existingItems.remove(product.getId());
                        } else {
                            existing.setQuantity(existing.getQuantity() - 1);
                            toSave.add(existing);
                        }
                    }
                }
            }

            if (!toSave.isEmpty()) {
                orderproductsRepository.saveAll(toSave);
            }
            if (!toDelete.isEmpty()) {
                orderproductsRepository.deleteAll(toDelete);
            }
        }

        return orderAdapter.mapToGetOrderResponseDto(order);
    }

    public GetOrderSummaryResponseDto getOrderSummary(Long id) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        List<OrderProducts> orderProducts = orderproductsRepository.findByOrderWithProduct(order);
        List<OrderItemResponseDto> items = orderAdapter.mapToOrderItemResponseDto(orderProducts);

        int totalItems = orderProducts.stream()
                .mapToInt(OrderProducts::getQuantity)
                .sum();

        BigDecimal totalPrice = orderProducts.stream()
                .map(op -> op.getProduct().getPrice().multiply(BigDecimal.valueOf(op.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return GetOrderSummaryResponseDto.builder()
                .id(order.getId())
                .status(order.getStatus())
                .items(items)
                .totalItems(totalItems)
                .totalPrice(totalPrice)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
