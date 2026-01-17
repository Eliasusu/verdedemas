package com.eliasit.verdedemas.order.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    
    @NotBlank(message = "El nombre es requerido")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String customerName;
    
    @NotBlank(message = "El teléfono es requerido")
    @Pattern(
        regexp = "^\\+?[0-9]{10,}$",
        message = "Teléfono inválido: debe tener al menos 10 dígitos"
    )
    private String customerPhone;
    
    @NotBlank(message = "La dirección es requerida")
    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    private String customerAddress;
    
    @NotNull(message = "Debes seleccionar una zona de entrega")
    private Long deliveryZoneId;
    
    @NotEmpty(message = "Debes seleccionar al menos un producto")
    @Valid
    private List<OrderItemRequest> items;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        
        @NotNull(message = "El ID del producto es requerido")
        private Long productId;
        
        @NotNull(message = "La cantidad es requerida")
        @Min(value = 1, message = "La cantidad debe ser mínimo 1")
        @Max(value = 999, message = "La cantidad no puede exceder 999")
        private Integer quantity;
    }
}