package com.eliasit.verdedemas.deliveryzone.entity;

import com.eliasit.verdedemas.shared.entity.BaseEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "delivery_zones")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryZone extends BaseEntity{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El nombre de la zona es requerido")
    @Column(nullable = false, unique = true)
    private String name; // "Norte", "Sur", "Este", "Oeste"
    
    private String description; // "Barrios: Saladillo, Alberdi..."
    
    @NotNull(message = "El costo de envío es requerido")
    @Positive(message = "El costo de envío debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingCost;
    
    @NotBlank(message = "El día de entrega es requerido")
    @Column(nullable = false)
    private String deliveryDay; // "FRIDAY_PM", "SATURDAY_AM", "SATURDAY_PM" (ENUMS)
    
    @Column(columnDefinition = "boolean default true")
    private Boolean isActive = true;
}
