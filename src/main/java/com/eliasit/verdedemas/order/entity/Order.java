package com.eliasit.verdedemas.order.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.eliasit.verdedemas.deliveryzone.entity.DeliveryZone;
import com.eliasit.verdedemas.shared.entity.BaseEntity;
import com.eliasit.verdedemas.shared.util.OrderStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String customerName;
    
    @Column(nullable = false)
    private String customerPhone;
    
    @Column(nullable = false)
    private String customerAddress;
    
    @ManyToOne
    @JoinColumn(name = "delivery_zone_id", nullable = false)
    private DeliveryZone deliveryZone;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingCost;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;
    
    private LocalDateTime sentToWhatsappAt;
}
