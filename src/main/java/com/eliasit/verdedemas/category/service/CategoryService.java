package com.eliasit.verdedemas.category.service;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eliasit.verdedemas.category.entity.Category;
import com.eliasit.verdedemas.category.repository.CategoryRepository;
import com.eliasit.verdedemas.shared.exception.BusinessException;
import com.eliasit.verdedemas.shared.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    
    private Logger log = Logger.getLogger(CategoryService.class.getName());
    
    private final CategoryRepository categoryRepository;
    
    /**
     * Lista todas las categorías activas
     */
    public List<Category> listActive() {
        log.info("Listando categorías activas");
        return categoryRepository.findByIsActiveTrue();
    }
    
    /**
     * Obtiene una categoría por ID
     */
    public Category getById(Long id) {
        log.info("Obteniendo categoría por ID: " + id);
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
    }
    
    /**
     * Crea una nueva categoría
     */
    public Category create(String name, String description) {
        log.info("Creando nueva categoría: " + name);
        
        // Validar que no exista una categoría con el mismo nombre
        categoryRepository.findByNameIgnoreCaseAndIsActiveTrue(name)
            .ifPresent(existing -> {
                throw new BusinessException("Ya existe una categoría con el nombre: " + name);
            });
        
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setIsActive(true);
        
        return categoryRepository.save(category);
    }
    
    /**
     * Actualiza una categoría existente
     */
    public Category update(Long id, String name, String description) {
        log.info("Actualizando categoría con ID: " + id);
        
        Category category = getById(id);
        
        // Validar que no exista otra categoría con el mismo nombre
        if (!category.getName().equalsIgnoreCase(name)) {
            categoryRepository.findByNameIgnoreCaseAndIsActiveTrue(name)
                .ifPresent(existing -> {
                    throw new BusinessException("Ya existe una categoría con el nombre: " + name);
                });
            category.setName(name);
        }
        
        category.setDescription(description);
        
        return categoryRepository.save(category);
    }
    
    /**
     * Desactiva una categoría (soft delete)
     */
    public void delete(Long id) {
        log.info("Eliminando categoría con ID: " + id);
        
        Category category = getById(id);
        category.setIsActive(false);
        categoryRepository.save(category);
    }
}

