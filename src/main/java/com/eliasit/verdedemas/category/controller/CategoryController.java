package com.eliasit.verdedemas.category.controller;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eliasit.verdedemas.category.dto.request.CreateCategoryRequest;
import com.eliasit.verdedemas.category.dto.request.UpdateCategoryRequest;
import com.eliasit.verdedemas.category.dto.response.CategoryResponse;
import com.eliasit.verdedemas.category.entity.Category;
import com.eliasit.verdedemas.category.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    
    private Logger log = Logger.getLogger(CategoryController.class.getName());
    
    private final CategoryService categoryService;
    
    /**
     * Listar todas las categorías activas
     */
    @GetMapping
    public List<CategoryResponse> list() {
        log.info("Listando categorías");
        return categoryService.listActive()
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtener categoría por ID
     */
    @GetMapping("/{id}")
    public CategoryResponse getById(@PathVariable Long id) {
        log.info("Obteniendo categoría por ID: " + id);
        Category category = categoryService.getById(id);
        return convertToResponse(category);
    }
    
    /**
     * Crear nueva categoría
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse create(@Valid @RequestBody CreateCategoryRequest request) {
        log.info("Creando nueva categoría: " + request.getName());
        Category category = categoryService.create(request.getName(), request.getDescription());
        return convertToResponse(category);
    }
    
    /**
     * Actualizar categoría existente
     */
    @PutMapping("/{id}")
    public CategoryResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCategoryRequest request) {
        log.info("Actualizando categoría con ID: " + id);
        Category category = categoryService.update(id, request.getName(), request.getDescription());
        return convertToResponse(category);
    }
    
    /**
     * Eliminar (desactivar) categoría
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Eliminando categoría con ID: " + id);
        categoryService.delete(id);
    }
    
    /**
     * Convierte una entidad Category a CategoryResponse
     */
    private CategoryResponse convertToResponse(Category category) {
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getIsActive(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }
}
