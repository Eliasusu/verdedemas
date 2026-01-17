package com.eliasit.verdedemas.category.entity;

import com.eliasit.verdedemas.shared.entity.BaseEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categorias")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity{
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la categoría es requerido")
    @Column(nullable = false, unique = true)
    private String name;

	private String description;

    @Column(columnDefinition = "boolean default true")
    private Boolean isActive = true;
    
}
