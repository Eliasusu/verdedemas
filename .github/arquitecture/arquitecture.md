# Arquitectura - VerdeDeMas E-Commerce

## 1. Stack Tecnológico

### Backend
```
Java 25
├─ Spring Boot 3.5.9          (Framework web)
├─ Spring Data JPA             (ORM)
├─ Spring Security 3.x         (Futuro: autenticación)
├─ JWT (jjwt 0.12.3)          (Futuro: tokens)
├─ Spring WebSocket            (Futuro: real-time)
└─ Validation (Bean Validation)
```

### Database
```
PostgreSQL 14+
├─ Flyway                      (Migrations)
├─ Timescaledb (opcional)      (Futuro: time-series)
└─ pgAdmin (admin, opcional)
```

### Build & Dependency Management
```
Maven 3.9+
└─ pom.xml                     (Gestor de dependencias)
```

### Testing (Futuro)
```
JUnit 5
├─ Mockito
├─ AssertJ
└─ Spring Boot Test
```

### Frontend (Futuro)
```
React Native
├─ Axios                       (HTTP client)
├─ AsyncStorage                (Local storage)
└─ React Navigation            (Routing)
```

---

## 2. Arquitectura de Capas

```
┌─────────────────────────────────────────────┐
│         HTTP Requests (REST API)            │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│    Controller Layer                          │
│  (ProductController, OrderController, etc)  │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│    Service Layer                             │
│  (ProductService, OrderService, etc)        │
│  - Lógica de negocio                        │
│  - Validaciones                             │
│  - Orquestación                             │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│    Repository Layer (Data Access)           │
│  (ProductRepository, OrderRepository, etc)  │
│  - JPA Queries                              │
│  - Custom @Query                            │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│    Database Layer                            │
│  (PostgreSQL)                               │
└─────────────────────────────────────────────┘
```

---

## 3. Flujo de Solicitud - Ejemplo: Crear Orden

```
Frontend (React Native)
      │
      │ POST /api/orders
      │ Content-Type: application/json
      │ {
      │   "customerName": "Juan",
      │   "customerPhone": "+5491123456789",
      │   "customerAddress": "Calle 123, La Plata",
      │   "deliveryZoneId": 1,
      │   "items": [
      │     { "productId": 5, "quantity": 2 },
      │     { "productId": 10, "quantity": 1 }
      │   ]
      │ }
      ▼
OrderController.createOrder()
      │
      ├─ @Valid → Valida CreateOrderRequest
      │          (Si falla → GlobalExceptionHandler retorna 400)
      │
      ▼
OrderService.createAndSendToWhatsApp()
      │
      ├─ getZoneById(deliveryZoneId)          (Busca zona)
      ├─ getProductById(productIds[])         (Busca productos)
      ├─ calculateSubtotal()                  (Suma precios)
      ├─ calculateTotal(subtotal + shipping)  (Total)
      ├─ save(Order)                          (Persiste en BD)
      ├─ saveAll(OrderItems)                  (Líneas de orden)
      ├─ generateWhatsAppMessage()            (Formatea mensaje)
      ├─ generateWhatsAppLink()               (Codifica URL)
      └─ updateOrderStatus(SENT_TO_WHATSAPP)  (Actualiza estado)
      │
      ▼
OrderRepository.save()
OrderItemRepository.saveAll()
      │
      ▼
PostgreSQL (INSERT)
      │
      ▼
OrderController retorna OrderResponse
      │
      │ 201 CREATED
      │ {
      │   "id": 42,
      │   "customerName": "Juan",
      │   "subtotal": 450.00,
      │   "shippingCost": 150.00,
      │   "totalPrice": 600.00,
      │   "deliveryZone": { "id": 1, "name": "Zona Centro" },
      │   "whatsappLink": "https://wa.me/5491123456789?text=..."
      │ }
      │
      ▼
Frontend
      │
      ├─ Abre WhatsApp con link: Linking.openURL(whatsappLink)
      │
      ▼
Usuario confirma en WhatsApp
```

---

## 4. Mapeo de Entidades a Base de Datos

```
┌─ Product (Entidad JPA)
│  └─ @Entity @Table("products")
│     ├─ id: Long
│     ├─ name: String
│     ├─ description: String
│     ├─ price: BigDecimal
│     ├─ category: Category (ManyToOne)
│     └─ timestamps (heredados de BaseEntity)
│
├─ Category
│  └─ @Entity @Table("categories")
│     ├─ id: Long
│     ├─ name: String
│     ├─ description: String
│     ├─ products: List<Product> (OneToMany)
│     └─ timestamps
│
├─ DeliveryZone
│  └─ @Entity @Table("delivery_zones")
│     ├─ id: Long
│     ├─ name: String
│     ├─ shippingCost: BigDecimal
│     ├─ deliveryDaysMin: Integer
│     ├─ deliveryDaysMax: Integer
│     ├─ isActive: Boolean
│     └─ timestamps
│
├─ Order
│  └─ @Entity @Table("orders")
│     ├─ id: Long
│     ├─ customerName: String
│     ├─ customerPhone: String
│     ├─ customerAddress: String
│     ├─ deliveryZone: DeliveryZone (ManyToOne)
│     ├─ items: List<OrderItem> (OneToMany)
│     ├─ subtotal: BigDecimal
│     ├─ shippingCost: BigDecimal
│     ├─ totalPrice: BigDecimal
│     ├─ status: OrderStatus (Enum)
│     ├─ sentToWhatsappAt: LocalDateTime
│     └─ timestamps
│
└─ OrderItem
   └─ @Entity @Table("order_items")
      ├─ id: Long
      ├─ order: Order (ManyToOne)
      ├─ product: Product (ManyToOne)
      ├─ quantity: Integer
      ├─ priceAtTime: BigDecimal
      └─ timestamps
```

---

## 5. Diagrama de Relaciones (ER)

```
categories (1) ──────── (N) products
                              │
                              │ (N)
                              │
                    order_items (N) ──── (1) orders
                              │
                              │ (1)
                              │
                        delivery_zones (1)
```

```sql
categories
   │
   └─ PK: id
      
products
   ├─ PK: id
   └─ FK: category_id → categories(id)
   
order_items
   ├─ PK: id
   ├─ FK: order_id → orders(id)
   └─ FK: product_id → products(id)
   
orders
   ├─ PK: id
   ├─ FK: delivery_zone_id → delivery_zones(id)
   └─ status: ENUM
   
delivery_zones
   └─ PK: id
```

---

## 6. Patrón de Diseño: Domain-Driven Design (DDD)

Cada módulo es un **Bounded Context** autónomo:

```
product/                    ← Contexto: Gestión de Productos
├── entity/
├── dto/
├── repository/
├── service/
└── controller/

category/                   ← Contexto: Gestión de Categorías
├── entity/
├── dto/
├── repository/
├── service/
└── controller/

deliveryzone/              ← Contexto: Gestión de Entregas
├── entity/
├── dto/
├── repository/
├── service/
└── controller/

order/                     ← Contexto: Gestión de Órdenes
├── entity/
├── dto/
├── repository/
├── service/
└── controller/
```

Cada contexto es independiente pero puede colaborar con otros a través del service layer.

---

## 7. Flujo de Validación

```
Cliente (React Native)
      │
      ▼ POST /api/orders
┌──────────────────────────┐
│   @Valid Annotation      │  ← Valida estructura JSON
│   (Bean Validation)      │    Si falla → 400 Bad Request
└──────────────────────────┘
      │ ✅ JSON válido
      ▼
┌──────────────────────────┐
│   OrderService           │  ← Valida lógica de negocio
│   - Zona existe?         │
│   - Productos existen?   │
│   - Precio válido?       │
│   - Stock disponible?    │  (Futuro)
└──────────────────────────┘
      │ ✅ Validaciones OK
      ▼
┌──────────────────────────┐
│   Guardar en BD          │
│   Generar WhatsApp       │
│   Retornar OrderResponse │
└──────────────────────────┘
```

---

## 8. Manejo de Excepciones

```
┌─ Exception
│  │
│  ├─ ResourceNotFoundException
│  │  └─ Cuando product, category, zone no existen
│  │     Código: 404 NOT_FOUND
│  │
│  ├─ BusinessException
│  │  └─ Errores de lógica de negocio
│  │     Código: 400 BAD_REQUEST
│  │
│  ├─ DataIntegrityViolationException
│  │  └─ Violación de constraints BD
│  │     Código: 409 CONFLICT
│  │
│  └─ MethodArgumentNotValidException
│     └─ Fallan @Valid validations
│        Código: 400 BAD_REQUEST
│
└─ GlobalExceptionHandler
   ├─ Captura todas las excepciones
   ├─ Formatea respuesta JSON
   └─ Retorna status HTTP apropiado
```

**Ejemplo de respuesta de error:**
```json
{
  "timestamp": "2026-01-17T10:30:00Z",
  "status": 404,
  "message": "Zona de entrega con ID 99 no encontrada",
  "path": "/api/orders"
}
```

---

## 9. Integración WhatsApp

```
┌─────────────────────────────────────────────┐
│  OrderService.generateWhatsAppMessage()     │
└────────────────────┬────────────────────────┘
                     │
         StringBuilder para armar mensaje
              con datos de orden
                     │
                     ▼
┌─────────────────────────────────────────────┐
│  🌱 *NUEVO PEDIDO VERDEDEMAS*               │
│  👤 *Cliente:* Juan García                  │
│  📍 *Dirección:* Calle 123                  │
│  🚚 *Zona:* Centro                          │
│  ─────────────────────                      │
│  *PRODUCTOS:*                               │
│  • Base Vital x2                            │
│  • Legumbres x1                             │
│  ─────────────────────                      │
│  💰 *Subtotal:* $450                        │
│  🚚 *Envío:* $150                           │
│  *TOTAL:* $600                              │
│  ¿Confirmás?                                │
└──────────────────┬──────────────────────────┘
                   │
    URLEncoder.encode(mensaje, UTF-8)
                   │
                   ▼
┌──────────────────────────────────────────────────┐
│  https://wa.me/5491123456789?text=...mensaje... │
└──────────────────┬───────────────────────────────┘
                   │
      Frontend: Linking.openURL(whatsappLink)
                   │
                   ▼
          Abre WhatsApp con mensaje
                prearmado
```

---

## 10. Configuración por Ambiente

```yaml
# application.properties (desarrollo local)
spring.datasource.url=jdbc:postgresql://localhost:5432/verdedemas
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=validate
logging.level.root=INFO

# application-dev.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/verdedemas_dev
spring.jpa.show-sql=true
logging.level.com.verdedemas=DEBUG

# application-prod.properties
spring.datasource.url=jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
logging.level.root=WARN
server.ssl.enabled=true
```

---

## 11. Flujo de Deployment (Futuro)

```
Código en GitHub
      │
      ▼
GitHub Actions (CI)
      ├─ Maven test
      ├─ Maven build
      └─ Docker build
            │
            ▼
Docker Registry (DockerHub / ECR)
      │
      ▼
Deploy a servidor
      ├─ Flyway migrations
      ├─ Start Spring Boot
      └─ Health check
            │
            ▼
Producción
```

---

## 12. Consideraciones de Escalabilidad

### Fase 1 (Actual)
- Single instance Spring Boot
- PostgreSQL single node
- No caching
- API simple REST

### Fase 2 (Mediano)
- Load balancer (NGINX)
- Multiple Spring Boot instances
- Redis cache
- Database read replicas
- CDN para imágenes

### Fase 3 (Grande)
- Kubernetes
- Microservicios (product, order, user)
- Event-driven (Kafka)
- Database sharding
- API Gateway
