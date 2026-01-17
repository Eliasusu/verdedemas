package com.eliasit.verdedemas.product.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eliasit.verdedemas.product.entity.Product;
import com.eliasit.verdedemas.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private Logger log = Logger.getLogger(ProductController.class.getName());

    private final ProductService productService;

    @GetMapping()
    public List<Product> list() {
        log.info("Listando products activos");
        return productService.listActive();
    }
    
}
