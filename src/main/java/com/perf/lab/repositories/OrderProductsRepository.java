package com.perf.lab.repositories;

import java.util.List;

import com.perf.lab.schema.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import com.perf.lab.schema.OrderProducts;
import org.springframework.data.jpa.repository.Query;

public interface OrderProductsRepository extends JpaRepository<OrderProducts, Long> {

    List<OrderProducts> findByOrderId(Long orderId);

    @Query("SELECT op FROM OrderProducts op JOIN FETCH op.product WHERE op.order = :order")
    List<OrderProducts> findByOrderWithProduct(Order order);
}
