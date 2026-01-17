package com.eliasit.verdedemas.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eliasit.verdedemas.order.entity.Order;
import java.util.List;


public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerPhone(String customerPhone);
    
    List<Order> findByCustomerNameContainingIgnoreCase(String customerName);
}
