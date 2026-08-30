package com.perf.lab.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.perf.lab.schema.OrderProducts;

public interface OrderproductsRepository extends JpaRepository<OrderProducts, Long> {

    List<OrderProducts> findByOrderId(Long orderId);
}
