package com.eliasit.verdedemas.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eliasit.verdedemas.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{
    
}
