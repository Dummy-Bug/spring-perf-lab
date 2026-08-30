package com.perf.lab.services;

import java.util.List;

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
}
