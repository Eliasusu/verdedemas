package com.eliasit.verdedemas.order.service;

import com.eliasit.verdedemas.deliveryzone.entity.DeliveryZone;
import com.eliasit.verdedemas.deliveryzone.service.DeliveryZoneService;
import com.eliasit.verdedemas.order.dto.request.CreateOrderRequest;
import com.eliasit.verdedemas.order.dto.response.OrderResponse;
import com.eliasit.verdedemas.order.entity.Order;
import com.eliasit.verdedemas.order.entity.OrderItem;
import com.eliasit.verdedemas.order.repository.OrderItemRepository;
import com.eliasit.verdedemas.order.repository.OrderRepository;
import com.eliasit.verdedemas.product.entity.Product;
import com.eliasit.verdedemas.product.service.ProductService;
import com.eliasit.verdedemas.shared.exception.ResourceNotFoundException;
import com.eliasit.verdedemas.shared.util.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final Logger log = Logger.getLogger(OrderService.class.getName());
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;
    private final DeliveryZoneService deliveryZoneService;
    
    /**
     * Crear una orden y generar link a WhatsApp
     * MVP Version 1: Sin seguimiento activo, solo "fire and forget"
     */
    public OrderResponse createAndSendToWhatsApp(CreateOrderRequest request) {
        log.info("Entrando a: createAndSendToWhatsApp");
        // 1. Validar período de pedidos
        validateOrderPeriod();

        // 2. Validar y obtener zona de entrega
        DeliveryZone deliveryZone = deliveryZoneService.getZoneById(request.getDeliveryZoneId());
        
        // 3. Validar y obtener productos
        List<Product> products = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        
        for (var itemRequest : request.getItems()) {
            Product product = productService.getProductById(itemRequest.getProductId());
            products.add(product);
            
            BigDecimal itemSubtotal = product.getPrice()
                .multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            subtotal = subtotal.add(itemSubtotal);
        }
        
        // 4. Calcular total
        BigDecimal totalPrice = subtotal.add(deliveryZone.getShippingCost());
        
        // 5. Crear y guardar Order
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setCustomerPhone(request.getCustomerPhone());
        order.setCustomerAddress(request.getCustomerAddress());
        order.setDeliveryZone(deliveryZone);
        order.setSubtotal(subtotal);
        order.setShippingCost(deliveryZone.getShippingCost());
        order.setTotalPrice(totalPrice);
        order.setStatus(OrderStatus.PENDING);
        
        Order savedOrder = orderRepository.save(order);
        
        // 6. Crear y guardar OrderItems
        for (int i = 0; i < request.getItems().size(); i++) {
            var itemRequest = request.getItems().get(i);
            Product product = products.get(i);
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtTime(product.getPrice());
            
            orderItemRepository.save(orderItem);
        }
        
        // 7. Generar mensaje y link de WhatsApp
        String whatsappMessage = generateWhatsAppMessage(savedOrder, deliveryZone);
        String whatsappLink = generateWhatsAppLink(request.getCustomerPhone(), whatsappMessage);
        
        // 8. Actualizar estado de orden
        savedOrder.setStatus(OrderStatus.SENT_TO_WHATSAPP);
        savedOrder.setSentToWhatsappAt(LocalDateTime.now());
        orderRepository.save(savedOrder);
        
        // 9. Mapear y retornar respuesta
        return mapToResponse(savedOrder, whatsappLink);
    }
    
    /**
     * Obtener orden por ID (MVP: solo lectura)
     */
    @SuppressWarnings("null")
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + orderId));
        
        // Generar link nuevamente (en caso que usuario quiera reintentar)
        String whatsappMessage = generateWhatsAppMessage(order, order.getDeliveryZone());
        String whatsappLink = generateWhatsAppLink(order.getCustomerPhone(), whatsappMessage);
        
        return mapToResponse(order, whatsappLink);
    }
    
    /**
     * Generar mensaje formateado para WhatsApp
     */
    private String generateWhatsAppMessage(Order order, DeliveryZone zone) {
        StringBuilder message = new StringBuilder();

        // Cálculo de fechas (próximo viernes/sábado desde hoy)
        LocalDate today = LocalDate.now();
        LocalDate nextFriday = today.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
        LocalDate nextSaturday = today.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        long daysMin = ChronoUnit.DAYS.between(today, nextFriday);
        long daysMax = ChronoUnit.DAYS.between(today, nextSaturday);

        // Fecha/hora estimada según zona.deliveryDay
        LocalDate deliveryDate = "FRIDAY_PM".equals(zone.getDeliveryDay()) ? nextFriday : nextSaturday;
        String dayLabel = "FRIDAY_PM".equals(zone.getDeliveryDay()) ? "Viernes" : "Sábado";
        String timeRange = getDeliveryTimeRange(zone.getDeliveryDay());

        message.append("🌱 *NUEVO PEDIDO VERDEDEMAS*\n\n");
        message.append("👤 *Cliente:* ").append(order.getCustomerName()).append("\n");
        message.append("📍 *Dirección:* ").append(order.getCustomerAddress()).append("\n");
        message.append("🚚 *Zona:* ").append(zone.getName()).append("\n");
        message.append("📞 *Teléfono:* ").append(order.getCustomerPhone()).append("\n");
        message.append("─────────────────────\n");
        message.append("*PRODUCTOS:*\n");

        // Listar items
        for (OrderItem item : order.getItems()) {
            message.append("• ").append(item.getProduct().getName())
                .append(" x").append(item.getQuantity()).append("\n");
        }

        message.append("─────────────────────\n");
        message.append("💰 *Subtotal:* $").append(String.format("%.2f", order.getSubtotal())).append("\n");
        message.append("🚚 *Envío (").append(zone.getName()).append("):* $")
            .append(String.format("%.2f", order.getShippingCost())).append("\n");
        message.append("*TOTAL:* $").append(String.format("%.2f", order.getTotalPrice())).append("\n\n");

        // Entrega fija por zona + ventana min-max
        message.append("📦 *Entrega estimada:* ").append(dayLabel)
               .append(" ").append(deliveryDate).append("\n");
        message.append("🕐 *Horario:* ").append(timeRange).append("\n");
        message.append("⏱ *Ventana:* ").append(daysMin).append("-").append(daysMax).append(" días\n\n");

        message.append("¿Confirmás disponibilidad y entrega? 📲");
        return message.toString();
    }
    
    /**
     * Generar URL para abrir WhatsApp con mensaje prearmado
     */
    private String generateWhatsAppLink(String phoneNumber, String message) {
        try {
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
            return "https://wa.me/" + phoneNumber + "?text=" + encodedMessage;
        } catch (Exception e) {
            throw new RuntimeException("Error al generar link de WhatsApp", e);
        }
    }
    
    /**
     * Mapear Order a OrderResponse
     */
    private OrderResponse mapToResponse(Order order, String whatsappLink) {
        OrderResponse response = new OrderResponse();
        
        response.setId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setCustomerPhone(order.getCustomerPhone());
        response.setCustomerAddress(order.getCustomerAddress());
        response.setSubtotal(order.getSubtotal());
        response.setShippingCost(order.getShippingCost());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus());
        response.setWhatsappLink(whatsappLink);
        
        // Mapear DeliveryZone
        DeliveryZone zone = order.getDeliveryZone();
        response.setDeliveryZone(new OrderResponse.DeliveryZoneResponse(
            zone.getId(),
            zone.getName(),
            zone.getShippingCost(),
            zone.getDeliveryDay()
        ));
        
        // Calcular información de entrega
        LocalDate today = LocalDate.now();
        LocalDate nextFriday = today.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
        LocalDate nextSaturday = today.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
        
        LocalDate deliveryDate = "FRIDAY_PM".equals(zone.getDeliveryDay()) ? nextFriday : nextSaturday;
        String dayLabel = "FRIDAY_PM".equals(zone.getDeliveryDay()) ? "Viernes" : "Sábado";
        
        long daysMin = ChronoUnit.DAYS.between(today, nextFriday);
        long daysMax = ChronoUnit.DAYS.between(today, nextSaturday);
        
        response.setDeliveryDate(dayLabel + " " + deliveryDate);
        response.setDeliveryWindowMin(daysMin);
        response.setDeliveryWindowMax(daysMax);
        response.setDeliveryTimeRange(getDeliveryTimeRange(zone.getDeliveryDay()));
        
        // Mapear OrderItems
        response.setItems(order.getItems().stream()
            .map(item -> new OrderResponse.OrderItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPriceAtTime(),
                item.getPriceAtTime().multiply(BigDecimal.valueOf(item.getQuantity()))
            ))
            .collect(Collectors.toList()));
        
        return response;
    }

    private String getDeliveryTimeRange(String deliveryDay) {
        switch (deliveryDay) {
            case "FRIDAY_PM": return "17:00-20:00";
            case "SATURDAY_AM": return "09:00-13:00";
            case "SATURDAY_PM": return "15:00-19:00";
            default: return "A confirmar";
        }
    }

    /**
     * Validación de periodo: ahora se permite pedir cualquier día.
     * Informamos ventana de entrega en el mensaje (hasta viernes/sábado).
     */
    private void validateOrderPeriod() {
        // Reglas actualizadas: no se bloquea el pedido por día.
        // Se calcula la ventana de entrega en el mensaje de WhatsApp.
    }
}