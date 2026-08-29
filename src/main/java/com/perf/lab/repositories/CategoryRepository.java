package com.perf.lab.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.perf.lab.schema.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
}
