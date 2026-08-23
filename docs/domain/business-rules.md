# Reglas de Negocio — VerdeDeMas

> Este documento reemplaza a `.github/bussines-rules/bussines-rules.md` (eliminado tras la Fase 0). A diferencia del original,
> cada regla indica su estado REAL verificado contra el código (no solo su estado documentado):
> ✅ Implementado · ⚠️ Parcialmente implementado · ⏳ Pendiente de implementar.
>
> Última verificación de código: 2026-08-23 (Fase 0 — ver `docs/phases/fase-0-auditoria-del-proyecto.md`).

## 1. Filosofía de la Marca

Guía de tono y copywriting, no una regla ejecutable por el backend. Se mantiene como referencia
para quien escriba descripciones de producto o mensajes de WhatsApp.

> "No vendemos comida. Vendemos alivio cotidiano."
> "Comer por elección, no por emoción" (sin negar la emoción).

Lineamientos: calma, elección, sostén, accesibilidad, cuidado sin culpa. Evitar urgencia falsa,
dietas restrictivas, culpa alimenticia, marketing sensacionalista, promesas milagrosas.

## 2. Productos (`Product`)

| Regla | Estado |
|---|---|
| Nombre emocional/claro (evitar "Mix vegetal 500g") | ⏳ Sin validación de forma en código; es convención editorial aplicada manualmente al cargar el seed |
| Descripción con "qué es / para qué sirve / rendimiento / usos" | ⏳ No hay estructura forzada; `description`, `servings`, `usages` son campos libres |
| Precio transparente, sin descuentos falsos | ⏳ No aplica al backend (no hay campo de descuento) |
| `price` obligatorio y mayor a 0 | ✅ Implementado — `@NotNull @Positive` en `Product.price` (`product/entity/Product.java:36-39`) |
| Nombre máx. 150 caracteres | ⏳ No implementado — `Product.name` solo tiene `@NotBlank`, sin `@Size` |
| Descripción máx. 1000 caracteres | ⏳ No implementado — sin `@Size` en `Product.description` |
| Sin sobreventa / control de stock | ⏳ No implementado — el modelo no tiene campo de stock |
| Crear producto vía API | ⏳ No implementado — no existe `POST /api/products`; `CreateProductRequest` existe pero está vacío. Los productos solo se cargan por `V2__seed.sql` |
| Listar productos activos | ✅ Implementado — `GET /api/products` → `ProductService.listActive()` → `findByIsActiveTrue()` |
| Obtener producto por ID | ⏳ No implementado — no existe `GET /api/products/{id}` (sí existe `ProductService.getProductById`, usado internamente por `OrderService`, pero no expuesto) |
| Producto debe estar activo para poder incluirse en una orden | ⚠️ Parcialmente — la regla existe en la documentación pero **no se verifica**: `ProductService.getProductById` no filtra por `isActive`, por lo que hoy se puede crear una orden con un producto desactivado |

## 3. Categorías (`Category`)

| Regla | Estado |
|---|---|
| CRUD completo (crear, listar, obtener, actualizar, desactivar) | ✅ Implementado — `CategoryController` + `CategoryService`, todos los métodos |
| Nombre único (no duplicar) | ⚠️ Parcialmente — `CategoryService.create/update` solo comparan contra categorías **activas** (`findByNameIgnoreCaseAndIsActiveTrue`), pero `categories.name` tiene `UNIQUE` **global** en BD (`V1__init.sql:3`). Recrear una categoría con el nombre de una desactivada pasa la validación de aplicación y falla en BD con un error no controlado (ver §7 y Fase 0, hallazgo de exception handling) |
| Categoría desactivada (soft delete) | ✅ Implementado — `CategoryService.delete()` marca `isActive=false` |
| No permitir desactivar categoría con productos activos asociados | ⏳ No implementado — `CategoryService.delete()` no verifica productos asociados |
| Toda categoría debe tener mínimo 2 productos | ⏳ No implementado y de difícil aplicación tal como está redactada (una categoría siempre nace con 0 productos) |
| Solo admins pueden crear categorías | ⏳ No implementado — no hay autenticación/autorización en el MVP; cualquiera puede llamar `POST /api/categories` |

## 4. Zonas de Entrega (`DeliveryZone`)

### 4.1 Ubicación geográfica

```
CIUDAD: Rosario, Santa Fe, Argentina

ZONAS IMPLEMENTADAS (cargadas en V2__seed.sql):
├─ Zona Norte (Fisherton, Alberdi, Rucci) — $300 — Viernes 17:00-20:00 (FRIDAY_PM)
└─ Zona Sur (Echesortu, Azcuénaga)        — $300 — Sábado 09:00-13:00 (SATURDAY_AM)

ZONAS PLANIFICADAS (⏳ sin fila en BD/seed, solo diseño futuro):
├─ Zona Este (Funes, Roldán, Pérez)
└─ Zona Oeste (Villa Gobernador Gálvez, Pérez Millán)
```

| Regla | Estado |
|---|---|
| Listar zonas activas | ✅ Implementado — `GET /api/delivery-zones` → `findByIsActiveTrue()` |
| Crear/editar/eliminar zona vía API | ⏳ No implementado — `CreateDeliveryZoneRequest` existe pero está vacío; solo hay seed SQL |
| Obtener detalle de zona (`GET /api/delivery-zones/{id}`) | ⏳ No implementado |
| Zona debe existir para crear una orden | ✅ Implementado — `DeliveryZoneService.getZoneById` lanza `ResourceNotFoundException` si no existe |
| Zona debe estar activa para crear una orden | ⏳ **No implementado pese a estar documentado como obligatorio** — `getZoneById` no filtra por `isActive` |
| Zona debe tener costo de envío > 0 | ✅ Implementado — `@Positive` en `DeliveryZone.shippingCost` (solo aplica si se persiste vía JPA con validación activa; hoy solo vía seed) |
| Zona debe tener día de entrega asignado | ⚠️ Parcialmente — `deliveryDay` es `@NotBlank` (no vacío), pero es un `String` libre sin restricción a los valores válidos del enum `DeliveryDay` (no hay `@Enumerated` ni `CHECK` en BD) |
| No se puede cambiar zona después de crear la orden | ✅ Implementado (por omisión) — no existe ningún endpoint de actualización de `Order` |
| No se puede eliminar zona con órdenes asociadas | ⏳ No implementado — no existe endpoint de eliminación de zona en absoluto (ni con ni sin esta protección) |

### 4.2 Ciclo semanal y ventana de entrega

```
Elaboración: Jueves y Viernes AM
Entregas:
  - Viernes PM (17:00-20:00) → zonas FRIDAY_PM
  - Sábado AM (09:00-13:00)  → zonas SATURDAY_AM
  - Sábado PM (15:00-19:00)  → zonas SATURDAY_PM
```

| Regla | Estado |
|---|---|
| Se aceptan pedidos cualquier día de la semana (regla vigente) | ✅ Implementado — `OrderService.validateOrderPeriod()` (`order/service/OrderService.java:275-278`) es un no-op intencional; no bloquea ningún día |
| Ventana dinámica (`díasMin` = próximo viernes, `díasMax` = próximo sábado) | ✅ Implementado — calculado con `TemporalAdjusters.next(...)` tanto en `OrderResponse` como en el mensaje de WhatsApp |
| Rango horario mostrado según `deliveryDay` de la zona | ⚠️ Parcialmente — funciona correctamente hoy (switch hardcodeado en `getDeliveryTimeRange()`), pero duplica información que ya existe en el enum `DeliveryDay` sin usarlo, generando riesgo de que ambos diverjan en el futuro |

> ⚠️ **Nota histórica:** una versión anterior de este documento restringía los pedidos a "Domingo a
> Miércoles" y rechazaba Jueves-Sábado. Esa regla quedó descartada; la vigente es "se acepta pedir
> cualquier día", confirmada por el código (`validateOrderPeriod()` es un no-op).

## 5. Órdenes (`Order` / `OrderItem`)

### 5.1 Creación de orden — datos obligatorios

| Campo | Estado |
|---|---|
| `customerName`: no vacío, máx 100 caracteres | ✅ Implementado — `@NotBlank @Size(max=100)` |
| `customerPhone`: formato válido, mínimo 10 dígitos | ✅ Implementado (mínimo) — `@Pattern("^\+?[0-9]{10,}$")` |
| `customerPhone`: máximo 20 caracteres (documentado) | ⏳ No implementado — sin cota superior en el DTO; columna real es `VARCHAR(50)` |
| `customerAddress`: no vacía, máx 200 caracteres | ✅ Implementado — `@NotBlank @Size(max=200)` |
| `deliveryZoneId`: debe existir | ✅ Implementado |
| `deliveryZoneId`: debe estar activa | ⏳ No implementado (ver §4) |
| `items[]`: mínimo 1 producto | ✅ Implementado — `@NotEmpty` |
| `items[].quantity`: entre 1 y 999 | ✅ Implementado — `@Min(1) @Max(999)` |
| `items[].productId`: debe existir | ✅ Implementado |
| `items[].productId`: debe estar activo | ⏳ No implementado (ver §2) |

### 5.2 Cálculo de valores

| Regla | Estado |
|---|---|
| `Subtotal = Σ(precio_producto × cantidad)` | ✅ Implementado — `OrderService.createAndSendToWhatsApp`, líneas 60-67 |
| `Costo_envío = delivery_zone.shipping_cost` (fijo, tomado en el momento de la orden) | ✅ Implementado |
| `Total = Subtotal + Costo_envío` | ✅ Implementado, pero **sin invariante reforzada** a nivel de BD ni de entidad (ver §7) |
| `price_at_time` congela el precio del producto en el momento de la orden | ✅ Implementado — `OrderItem.priceAtTime` (protege órdenes pasadas de cambios de precio del producto) |

### 5.3 Estados de orden

```
PENDING → SENT_TO_WHATSAPP → CONFIRMED → PREPARING → DISPATCHED → DELIVERED
                                                              (CANCELLED en cualquier punto anterior a DELIVERED)
```

| Regla | Estado |
|---|---|
| `PENDING` al crear, `SENT_TO_WHATSAPP` automático tras generar el link | ✅ Implementado — únicas dos transiciones que ejecuta el código, dentro del mismo método `createAndSendToWhatsApp` |
| `CONFIRMED` / `PREPARING` / `DISPATCHED` / `DELIVERED` / `CANCELLED` | ⏳ No implementado — no existe ningún endpoint para transicionar el estado; son valores del enum `OrderStatus` sin flujo de actualización (previsto como "actualización manual por vendedor", fuera del MVP actual) |
| `sentToWhatsappAt` se registra al generar el link | ✅ Implementado — `order.setSentToWhatsappAt(LocalDateTime.now())` |

### 5.4 Consultas de orden

| Regla | Estado |
|---|---|
| `GET /api/orders/{id}` | ✅ Implementado |
| `GET /api/orders/customer/{phone}` | ✅ Implementado, **con bug**: el link de WhatsApp generado usa `order.getCustomerPhone()` como número destino en vez de `Constants.MANAGER_PHONE` (inconsistente con los otros dos métodos que sí usan el número del negocio) |

## 6. WhatsApp

| Regla | Estado |
|---|---|
| Mensaje formateado con emojis, negritas, secciones | ✅ Implementado — `OrderService.generateWhatsAppMessage` |
| Incluye cliente, dirección, zona, teléfono, productos, subtotal, envío, total, ventana de entrega, pregunta de confirmación | ✅ Implementado |
| Link `https://wa.me/{numero}?text={mensaje_encoded}` | ✅ Implementado — `generateWhatsAppLink` con `URLEncoder.encode(..., UTF_8)` |
| Número destino = número del negocio (`Constants.MANAGER_PHONE`) | ⚠️ Parcialmente — correcto en `createAndSendToWhatsApp` y `getOrderById`; **incorrecto** en `getOrdersByCustomerPhone` (usa el teléfono del cliente) |
| Límite de 4096 caracteres de WhatsApp | ⏳ No verificado en código (no hay truncado ni validación de longitud del mensaje generado) |

## 7. Integridad de Datos

| Regla | Estado |
|---|---|
| PK auto-increment en todas las tablas | ✅ Implementado (`BIGSERIAL`) |
| FKs `order→delivery_zone`, `order_items→order`, `order_items→product`, `products→category` | ✅ Implementado — presentes en `V1__init.sql` |
| `UNIQUE` en `categories.name` y `delivery_zones.name` | ✅ Implementado a nivel de BD — pero ver inconsistencia con la lógica de aplicación de categorías (§3) |
| `NOT NULL` en `customer_name`, `customer_phone`, `customer_address` | ✅ Implementado |
| `CHECK: price > 0` | ⏳ No implementado a nivel de BD (solo Bean Validation en la entidad `Product`, no ejecutada sobre inserts vía seed) |
| `CHECK: shipping_cost > 0` | ⏳ No implementado a nivel de BD (ídem) |
| `CHECK: total_price = subtotal + shipping_cost` | ⏳ No implementado en ningún nivel — es responsabilidad exclusiva de `OrderService`, sin refuerzo |
| Auditoría `created_at`/`updated_at` automática | ✅ Implementado — `BaseEntity` + `@EnableJpaAuditing` (`config/JpaConfig.java`) |
| Retención de historial (órdenes, productos dados de baja, precios históricos) | ✅ Implementado — soft-delete (`isActive`) + `priceAtTime` |
| No se guarda IP, geolocalización ni datos de pago | ✅ Implementado (por omisión — no hay ningún campo ni lógica relacionada) |

## 8. Búsqueda y Filtrado

⏳ **Ninguna regla de esta sección está implementada.** `ProductController` solo expone `GET /api/products`
(lista simple, sin parámetros) y `DeliveryZoneController` solo expone `GET /api/delivery-zones`
(también simple). Quedan como diseño deseado:

- `GET /api/products/search?q=...` (búsqueda por nombre/descripción/categoría).
- `GET /api/products/category/{categoryId}`.
- `GET /api/delivery-zones?active=true` — hoy el listado de zonas ya devuelve únicamente activas
  (filtro fijo en el service), pero sin soportar el query param ni ordenamiento explícito.

## 9. Acceso (MVP sin autenticación)

✅ Implementado en su totalidad: `SecurityConfig` permite todo tráfico sin autenticación
(`anyRequest().permitAll()`, `csrf` deshabilitado). Endpoints realmente expuestos hoy:

```
GET    /api/products
GET    /api/categories
GET    /api/categories/{id}
POST   /api/categories
PUT    /api/categories/{id}
DELETE /api/categories/{id}
GET    /api/delivery-zones
POST   /api/orders
GET    /api/orders/{id}
GET    /api/orders/customer/{phone}
```

⏳ No existen (documentados como pendientes): `GET /api/products/{id}`, `GET /api/products/category/{catId}`,
`GET /api/products/search`, `POST/PUT/DELETE /api/delivery-zones`, `GET /api/delivery-zones/{id}`,
ningún endpoint de cambio de estado de orden.

> ⚠️ Ver Fase 0 (`docs/phases/fase-0-auditoria-del-proyecto.md`) para el hallazgo de seguridad
> completo: la ausencia de autenticación se combina con CORS abierto a cualquier origen.

## 10. Límites de Datos

| Campo | Límite documentado | Estado |
|---|---|---|
| Nombre cliente | 100 caracteres | ✅ Implementado |
| Dirección | 200 caracteres | ✅ Implementado |
| Teléfono | 20 caracteres | ⏳ No implementado (sin `@Size` máximo) |
| Nombre producto | 150 caracteres | ⏳ No implementado |
| Descripción producto | 1000 caracteres | ⏳ No implementado |
| Cantidad por item | 1-999 | ✅ Implementado |
| Items por orden | máximo 100 | ⏳ No implementado (sin `@Size` en la lista `items`) |

## 11. Casos de Uso Especiales (verificados)

- Dos órdenes con el mismo producto: ✅ permitido, sin restricción de ningún tipo.
- Cambiar el precio de un producto no afecta órdenes previas: ✅ garantizado por `priceAtTime`.
- Desactivar una zona no afecta órdenes previas, pero **sí debería bloquear nuevas** y hoy no lo hace: ⏳ ver §4.
- Teléfono mal formado: ✅ rechazado por `@Pattern`, con mensaje de error claro en el `message` de la anotación (aunque `ResourceNotFoundException`/`BusinessException` sí devuelven hoy un 500 genérico en vez de un JSON de error claro — ver Fase 0).

## 12. Legal / AFIP / Consumidor (Argentina)

Sin cambios respecto al documento legado: son ítems 100% pendientes y fuera del alcance del código
actual (facturación electrónica, IVA, política de privacidad, términos y condiciones, política de
devoluciones). No se audita código porque no existe ninguna implementación relacionada.
