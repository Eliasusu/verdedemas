package com.eliasit.verdedemas.product.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.eliasit.verdedemas.product.entity.Product;
import com.eliasit.verdedemas.product.repository.ProductRepository;
import com.eliasit.verdedemas.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final Logger log = Logger.getLogger(ProductService.class.getName());

    private final ProductRepository productRepository;

    @SuppressWarnings("null")
    public Product getProductById(Long id){
        log.info("Buscando product by ID: " + id);
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));

    }

    public List<Product> listActive() {
        return productRepository.findByIsActiveTrue();
    }
}
