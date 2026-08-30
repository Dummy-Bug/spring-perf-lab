package com.perf.lab.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.perf.lab.schema.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByCategory(String category);

    @Query(nativeQuery = true, value = "SELECT DISTINCT category FROM products")
    List<String> findAllCategories();
//
//    @Query(nativeQuery = true, value =
//            "SELECT p.*, c.* FROM products p JOIN categories c ON p.category_id = c.id WHERE p.id = :id")
//    List<Product> findProductWithDetailsById(Long id);
//    Encountered a duplicated sql alias [id] during auto-discovery of a native-sql query

//    @Query(nativeQuery = true, value =
//" SELECT p.*, c.name AS category FROM products p JOIN categories c ON p.category_id  = c.id WHERE p.id = :id")
//    List<Product> findProductWithDetailsById(Long id);


    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id")
    List<Product> findProductWithDetailsById(Long id);
}

