# Project Structure - VerdeDeMas E-Commerce

## Overview
Estructura modular por entidad para un e-commerce de verdulería con enfoque MVP inicial (80% backend sin autenticación) y expansión futura a sistema robusto.

## Current Phase: MVP (Fase 1)
**Objetivo:** Backend funcional con productos, categorías, zonas de entrega y pedidos por WhatsApp.

```yaml
verdedemas/
├── src/
│   ├── main/
│   │   ├── java/com/verdedemas/
│   │   │   ├── VerdeDeMasApplication.java
│   │   │   │
│   │   │   ├── config/
│   │   │   │   ├── WebConfig.java                    # CORS, filtros HTTP
│   │   │   │   └── JpaConfig.java                    # Auditoría de entidades
│   │   │   │
│   │   │   ├── shared/
│   │   │   │   ├── entity/
│   │   │   │   │   └── BaseEntity.java               # Padre: createdAt, updatedAt
│   │   │   │   │
│   │   │   │   ├── exception/
│   │   │   │   │   ├── GlobalExceptionHandler.java   # Manejo centralizado
│   │   │   │   │   ├── ResourceNotFoundException.java # 404
│   │   │   │   │   └── BusinessException.java        # Errores negocio
│   │   │   │   │
│   │   │   │   └── util/
│   │   │   │       ├── Constants.java                # Valores constantes
│   │   │   │       └── OrderStatus.java              # Enum estados
│   │   │   │
│   │   │   ├── product/                              # 🛍️ MÓDULO PRODUCTOS
│   │   │   │   ├── entity/
│   │   │   │   │   └── Product.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   │   └── CreateProductRequest.java
│   │   │   │   │   └── response/
│   │   │   │   │       └── ProductResponse.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── ProductRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   └── ProductService.java
│   │   │   │   └── controller/
│   │   │   │       └── ProductController.java
│   │   │   │
│   │   │   ├── category/                             # 📂 MÓDULO CATEGORÍAS
│   │   │   │   ├── entity/
│   │   │   │   │   └── Category.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   │   └── CreateCategoryRequest.java
│   │   │   │   │   └── response/
│   │   │   │   │       └── CategoryResponse.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── CategoryRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   └── CategoryService.java
│   │   │   │   └── controller/
│   │   │   │       └── CategoryController.java
│   │   │   │
│   │   │   ├── deliveryzone/                         # 🚚 MÓDULO ZONAS ENTREGA
│   │   │   │   ├── entity/
│   │   │   │   │   └── DeliveryZone.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   │   └── CreateDeliveryZoneRequest.java
│   │   │   │   │   └── response/
│   │   │   │   │       └── DeliveryZoneResponse.java
│   │   │   │   ├── repository/
│   │   │   │   │   └── DeliveryZoneRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   └── DeliveryZoneService.java
│   │   │   │   └── controller/
│   │   │   │       └── DeliveryZoneController.java
│   │   │   │
│   │   │   ├── order/                                # 📦 MÓDULO PEDIDOS
│   │   │   │   ├── entity/
│   │   │   │   │   ├── Order.java
│   │   │   │   │   └── OrderItem.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   │   └── CreateOrderRequest.java
│   │   │   │   │   └── response/
│   │   │   │   │       └── OrderResponse.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── OrderRepository.java
│   │   │   │   │   └── OrderItemRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   └── OrderService.java             # Genera mensaje WhatsApp
│   │   │   │   └── controller/
│   │   │   │       └── OrderController.java
│   │   │   │
│   │   │   └── (futuro: user/, auth/, address/, review/, etc)
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       └── db/migration/
│   │           ├── V1__init_schema.sql
│   │           ├── V2__create_products_categories.sql
│   │           └── V3__create_delivery_zones.sql
│   │
│   └── test/
│       └── java/com/verdedemas/
│           ├── service/
│           │   ├── ProductServiceTest.java
│           │   ├── OrderServiceTest.java
│           │   └── DeliveryZoneServiceTest.java
│           └── controller/
│               ├── ProductControllerTest.java
│               ├── OrderControllerTest.java
│               └── DeliveryZoneControllerTest.java
│
├── .github/
│   ├── notes/
│   │   └── project-structure.md        # ← Este archivo
│   ├── requirements/
│   │   └── requirements.md
│   ├── arquitecture/
│   │   └── arquitecture.md
│   └── bussines-rules/
│       └── bussines-rules.md
│
├── pom.xml
├── .gitignore
├── .gitattributes
└── README.md
```

## Características por Módulo

### 🛍️ Product
- Entidad con nombre, descripción, precio, categoría
- Listar productos con filtros
- Detalle de producto
- Búsqueda por categoría

### 📂 Category
- Entidad con nombre y descripción
- Listar categorías activas
- Relación 1-N con Product

### 🚚 DeliveryZone
- Entidad con nombre, costo de envío, días de entrega
- Listar zonas activas
- Validación de zona en pedido
- Cálculo automático de costo

### 📦 Order
- Entidad con datos cliente (nombre, teléfono, dirección)
- Relación N-N con Product (mediante OrderItem)
- Integración con WhatsApp
- Generación automática de mensaje formateado
- Estados: PENDING, SENT_TO_WHATSAPP, CONFIRMED, etc.

## Endpoints MVP

```
GET    /api/categories                    → Listar categorías
GET    /api/products                      → Listar productos (con filtros)
GET    /api/products/{id}                 → Detalle producto
GET    /api/products/category/{catId}     → Productos por categoría
GET    /api/products/search?q=            → Búsqueda de productos

GET    /api/delivery-zones                → Listar zonas de entrega
GET    /api/delivery-zones/{id}           → Detalle zona

POST   /api/orders                        → Crear pedido + enviar WhatsApp
GET    /api/orders/{id}                   → Detalle pedido (futuro)
```

## Future Phases (Fase 2+)

Una vez MVP sea 80% funcional, agregar:

```yaml
├── user/                                 # 👤 Autenticación
│   ├── entity/
│   │   └── User.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequest.java
│   │   │   └── RegisterRequest.java
│   │   └── response/
│   │       └── LoginResponse.java
│   ├── repository/
│   │   └── UserRepository.java
│   ├── service/
│   │   └── UserService.java
│   └── controller/
│       └── UserController.java
│
├── auth/                                 # 🔐 JWT & Security
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── JwtConfig.java
│   ├── security/
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── CustomUserDetailsService.java
│   └── util/
│       └── SecurityUtil.java
│
├── address/                              # 📍 Direcciones usuario
│   ├── entity/
│   │   └── Address.java
│   ├── dto/
│   │   └── request/
│   │       └── CreateAddressRequest.java
│   ├── repository/
│   │   └── AddressRepository.java
│   ├── service/
│   │   └── AddressService.java
│   └── controller/
│       └── AddressController.java
│
├── cart/                                 # 🛒 Carrito
│   ├── entity/
│   │   ├── Cart.java
│   │   └── CartItem.java
│   ├── dto/
│   │   └── response/
│   │       └── CartResponse.java
│   ├── repository/
│   │   ├── CartRepository.java
│   │   └── CartItemRepository.java
│   ├── service/
│   │   └── CartService.java
│   └── controller/
│       └── CartController.java
│
├── review/                               # ⭐ Reseñas
│   ├── entity/
│   │   └── Review.java
│   ├── dto/
│   │   └── request/
│   │       └── CreateReviewRequest.java
│   ├── repository/
│   │   └── ReviewRepository.java
│   ├── service/
│   │   └── ReviewService.java
│   └── controller/
│       └── ReviewController.java
│
├── websocket/                            # ⚡ Real-time
│   ├── config/
│   │   └── WebSocketConfig.java
│   ├── controller/
│   │   └── OrderTrackingController.java
│   ├── service/
│   │   └── NotificationService.java
│   └── dto/
│       └── OrderStatusMessage.java
│
└── email/                                # 📧 Notificaciones
    ├── service/
    │   └── EmailService.java
    └── template/
        ├── order-confirmation.html
        └── order-status.html
```

## Decisiones de Diseño

1. **Modularidad por entidad**: Cada módulo es autónomo (entity, dto, repository, service, controller)
2. **DTO Request/Response**: Separación clara entre entrada y salida
3. **BaseEntity**: Auditoría automática (createdAt, updatedAt)
4. **Sin autenticación MVP**: Enfoque en funcionalidad core
5. **WhatsApp Integration**: No requiere API, solo URL encoding
6. **Flyway Migrations**: Versionado de BD desde el inicio
7. **Exception Handling centralizado**: GlobalExceptionHandler para todos los errores

## Convenciones

- **Paquetes**: Nombrados por entidad de negocio, no por capa técnica
- **DTOs**: Siempre usar para entrada/salida (nunca devolver entidades)
- **Repositories**: Extender JpaRepository, métodos custom con @Query si es necesario
- **Services**: Lógica de negocio, validaciones, orquestación
- **Controllers**: Solo mapeo de HTTP, delegación a service
- **Excepciones**: Custom extends RuntimeException, GlobalExceptionHandler captura todas
