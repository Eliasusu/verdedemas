# Project Structure - VerdeDeMas E-Commerce

## Overview
Estructura modular por entidad para un e-commerce de verdulería con enfoque MVP inicial (80% backend sin autenticación) y expansión futura a sistema robusto.

## Current Phase: MVP (Fase 1)
**Objetivo:** Backend funcional con productos, categorías, zonas de entrega y pedidos por WhatsApp.

```yaml
verdedemas/
├── src/
│   ├── main/
│   │   ├── java/com/eliasit/verdedemas/
│   │   │   ├── VerdedemasApplication.java
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
│   │   │   │   │   │   └── CreateProductRequest.java  # ⚠️ clase vacía, sin uso (no hay endpoint de creación)
│   │   │   │   │   └── reponse/                        # ⚠️ typo real en el código ("reponse", falta la "s")
│   │   │   │   │       └── ProductResponse.java         #    también vacía; el controller devuelve la entidad Product directa
│   │   │   │   ├── repository/
│   │   │   │   │   └── ProductRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   └── ProductService.java
│   │   │   │   └── controller/
│   │   │   │       └── ProductController.java          # solo expone GET /api/products (list())
│   │   │   │
│   │   │   ├── category/                             # 📂 MÓDULO CATEGORÍAS
│   │   │   │   ├── entity/
│   │   │   │   │   └── Category.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   │   ├── CreateCategoryRequest.java   # la real, usada por el controller
│   │   │   │   │   │   └── UpdateCategoryRequest.java
│   │   │   │   │   ├── response/
│   │   │   │   │   │   └── CategoryResponse.java        # la real, usada por el controller
│   │   │   │   │   └── reponse/                          # ⚠️ typo: paquete duplicado y MUERTO
│   │   │   │   │       └── CreateCategoryRequest.java    #    clase vacía sin uso; la real vive en dto/request/
│   │   │   │   ├── repository/
│   │   │   │   │   └── CategoryRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   └── CategoryService.java
│   │   │   │   └── controller/
│   │   │   │       └── CategoryController.java         # CRUD completo: list, getById, create, update, delete
│   │   │   │
│   │   │   ├── deliveryzone/                         # 🚚 MÓDULO ZONAS ENTREGA
│   │   │   │   ├── entity/
│   │   │   │   │   └── DeliveryZone.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/
│   │   │   │   │   │   └── CreateDeliveryZoneRequest.java # ⚠️ clase vacía, sin uso (no hay endpoint de creación)
│   │   │   │   │   └── reponse/                            # ⚠️ typo real en el código ("reponse", falta la "s")
│   │   │   │   │       └── DeliveryZoneResponse.java        #    también vacía; el controller devuelve la entidad directa
│   │   │   │   ├── repository/
│   │   │   │   │   └── DeliveryZoneRepository.java
│   │   │   │   ├── service/
│   │   │   │   │   └── DeliveryZoneService.java
│   │   │   │   └── controller/
│   │   │   │       └── DeliveryZoneController.java     # solo expone GET /api/delivery-zones (list())
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
│   │   │   │       └── OrderController.java          # POST /, GET /{id}, GET /customer/{phone}
│   │   │   │
│   │   │   └── (futuro: user/, auth/, address/, review/, etc)
│   │   │
│   │   └── resources/
│   │       ├── application.properties                 # única existente; NO hay application-dev/-prod todavía
│   │       └── db/migration/
│   │           ├── V1__init.sql                        # crea las 5 tablas (categories, products, delivery_zones, orders, order_items)
│   │           └── V2__seed.sql                         # carga 2 categorías, 2 productos y 2 zonas (Norte, Sur)
│   │
│   └── test/
│       └── java/com/eliasit/verdedemas/
│           └── VerdedemasApplicationTests.java         # único test existente hoy (context load); resto de la
│                                                        # pirámide de tests (service/, controller/) es trabajo futuro
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
- Entidad con nombre, descripción, precio, imagen (`imageUrl`), rendimiento (`servings`), usos (`usages`), categoría
- Listar productos activos (`GET /api/products`, sin filtros, sin paginación)
- ⚠️ Pendiente de implementar: detalle de producto por ID, búsqueda, filtro por categoría, endpoint de creación

### 📂 Category
- Entidad con nombre y descripción
- CRUD completo: listar activas, obtener por ID, crear, actualizar, eliminar (soft delete vía `isActive`)
- Relación 1-N con Product

### 🚚 DeliveryZone
- Entidad con nombre, descripción, costo de envío (`shippingCost`), día de entrega (`deliveryDay`, `String` simple, no enum)
- Listar zonas activas (`GET /api/delivery-zones`, sin filtros)
- Solo 2 zonas cargadas hoy (Norte, Sur — ver seed real). Este/Oeste son planificadas, no implementadas
- Validación de zona en pedido (`getZoneById`), cálculo automático de costo de envío
- ⚠️ Pendiente de implementar: detalle por ID, crear/actualizar/eliminar zona

### 📦 Order
- Entidad con datos cliente (nombre, teléfono, dirección)
- Relación N-N con Product (mediante OrderItem)
- Integración con WhatsApp
- Generación automática de mensaje formateado
- Estados: PENDING, SENT_TO_WHATSAPP, CONFIRMED, etc. (solo PENDING/SENT_TO_WHATSAPP se asignan automáticamente hoy; el resto son manuales/futuros)
- Consulta por ID y por teléfono de cliente (`GET /api/orders/{id}`, `GET /api/orders/customer/{phone}`)
- Sin restricción de día para crear un pedido (se acepta cualquier día de la semana; ver `bussines-rules.md` §4.6)

## Endpoints Implementados (estado real verificado en el código)

```
GET    /api/categories                    → Listar categorías activas
GET    /api/categories/{id}               → Detalle de categoría
POST   /api/categories                    → Crear categoría
PUT    /api/categories/{id}               → Actualizar categoría
DELETE /api/categories/{id}               → Eliminar (desactivar) categoría

GET    /api/products                      → Listar productos activos (sin filtros)

GET    /api/delivery-zones                → Listar zonas de entrega activas (solo Norte y Sur hoy)

POST   /api/orders                        → Crear pedido + enviar WhatsApp
GET    /api/orders/{id}                   → Detalle de pedido
GET    /api/orders/customer/{phone}       → Listar pedidos por teléfono de cliente
```

### ⚠️ Pendientes de implementar (documentados en otras versiones de este archivo, pero NO existen en el código)

```
GET    /api/products/{id}                 → Detalle de producto
GET    /api/products/category/{catId}     → Productos por categoría
GET    /api/products/search?q=            → Búsqueda de productos

GET    /api/delivery-zones/{id}           → Detalle de zona
POST   /api/delivery-zones                → Crear zona
PUT    /api/delivery-zones/{id}           → Actualizar zona
DELETE /api/delivery-zones/{id}           → Eliminar zona
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
2. **DTO Request/Response**: Separación clara entre entrada y salida — ⚠️ aplicado de forma completa solo en `category` y `order`; en `product` y `deliveryzone` las clases DTO (`ProductResponse`, `CreateProductRequest`, `DeliveryZoneResponse`, `CreateDeliveryZoneRequest`) existen vacías y sin usar — esos controllers devuelven la entidad JPA directamente
3. **BaseEntity**: Auditoría automática (createdAt, updatedAt)
4. **Sin autenticación MVP**: Enfoque en funcionalidad core
5. **WhatsApp Integration**: No requiere API, solo URL encoding
6. **Flyway Migrations**: Versionado de BD desde el inicio
7. **Exception Handling centralizado**: ⚠️ objetivo de diseño, NO implementado — `GlobalExceptionHandler` es una clase vacía (sin `@ControllerAdvice` ni métodos), no captura nada hoy

## Convenciones

- **Paquetes**: Nombrados por entidad de negocio, no por capa técnica
- **DTOs**: La convención es usar DTOs para entrada/salida y nunca devolver entidades — en la práctica `product` y `deliveryzone` no la siguen todavía (ver nota arriba)
- **Repositories**: Extender JpaRepository, métodos custom con @Query si es necesario
- **Services**: Lógica de negocio, validaciones, orquestación
- **Controllers**: Solo mapeo de HTTP, delegación a service
- **Excepciones**: Custom extends RuntimeException; `GlobalExceptionHandler` está pensado para capturarlas todas pero hoy está vacío (no captura nada — ver sección "Exception Handling centralizado" arriba)
- **Typo conocido, pendiente de refactor**: en `product` y `deliveryzone` el paquete de DTOs de salida se llama `dto/reponse/` (falta la "s" de "response"). En `category` ese typo generó además una clase duplicada y muerta (`category/dto/reponse/CreateCategoryRequest.java`, vacía) — la clase real usada por el controller vive en `category/dto/request/CreateCategoryRequest.java`. No se corrige en código, solo se documenta tal cual está.
