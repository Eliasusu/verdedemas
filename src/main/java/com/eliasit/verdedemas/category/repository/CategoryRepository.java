package com.eliasit.verdedemas.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.eliasit.verdedemas.category.entity.Category;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByIsActiveTrue();
    
    Optional<Category> findByNameIgnoreCaseAndIsActiveTrue(String name);
}

