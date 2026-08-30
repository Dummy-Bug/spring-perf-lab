package com.perf.lab.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.perf.lab.schema.Order;

public interface OrderRespository extends JpaRepository<Order, Long> {

}
