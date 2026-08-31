package com.perf.lab.services;

import java.util.List;

import com.perf.lab.dtos.CreateOrderRequestDto;
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

    public void createOrder(CreateOrderRequestDto createRequestDto) {

        Order order = Order.builder()
                .status(OrderStatus.PENDING)
                .build();

        orderRespository.save(order);

        if (createRequestDto.getOrderItems() == null) return;

        for (var itemDto : createRequestDto.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId()));

            OrderProducts orderProduct = OrderProducts.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemDto.getQuantity() != null ? itemDto.getQuantity() : 1)
                    .build();

            orderproductsRepository.save(orderProduct);
        }
    }
}
