package com.eliasit.verdedemas.order.controller;

import java.util.logging.Logger;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eliasit.verdedemas.order.dto.request.CreateOrderRequest;
import com.eliasit.verdedemas.order.dto.response.OrderResponse;
import com.eliasit.verdedemas.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private Logger log = Logger.getLogger(OrderController.class.getName());

    private final OrderService orderService;

    @PostMapping
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Creando order"); 
        return orderService.createAndSendToWhatsApp(request);
    }

    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable Long id) {
        log.info("Buscando order by ID: " + id);
        return orderService.getOrderById(id);
    }
    
    
}
