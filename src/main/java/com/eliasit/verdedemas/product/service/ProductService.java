package com.eliasit.verdedemas.product.service;

import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.eliasit.verdedemas.product.entity.Product;
import com.eliasit.verdedemas.product.repository.ProductRepository;
import com.eliasit.verdedemas.shared.exception.ResourceNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    private final Logger log = Logger.getLogger(ProductService.class.getName());

    private ProductRepository productRepository;

    @SuppressWarnings("null")
    public Product getProductById(Long productId){
        log.info("Buscando producto by ID: " + productId);
        return productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Zona de entrega no encontrada: " + productId));

    }
}
