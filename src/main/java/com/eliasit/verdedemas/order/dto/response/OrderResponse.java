package com.eliasit.verdedemas.order.dto.response;

import com.eliasit.verdedemas.shared.util.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private Long id;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    
    private DeliveryZoneResponse deliveryZone;
    private List<OrderItemResponse> items;
    
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal totalPrice;
    
    private OrderStatus status;
    private String whatsappLink;
    
    // Información de entrega calculada
    private String deliveryDate;      // Formato: "Viernes 2026-01-24"
    private Long deliveryWindowMin;   // Días mínimos hasta entrega
    private Long deliveryWindowMax;   // Días máximos hasta entrega
    private String deliveryTimeRange; // Rango horario: "17:00-20:00"
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryZoneResponse {
        private Long id;
        private String name;
        private BigDecimal shippingCost;
        private String deliveryDay;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal priceAtTime;
        private BigDecimal subtotal;
    }
}