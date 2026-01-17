package com.eliasit.verdedemas.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eliasit.verdedemas.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
    List<Product> findByIsActiveTrue();
}
