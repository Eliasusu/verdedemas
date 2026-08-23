# Requerimientos — VerdeDeMas E-Commerce

> Este documento reemplaza a `.github/requirements/requirements.md` (eliminado tras la Fase 0). Los
> checkboxes reflejan el estado real verificado contra el código fuente (`src/main/java`), no el
> estado aspiracional del documento original.
>
> Última verificación de código: 2026-08-23 (Fase 0 — ver `docs/phases/fase-0-auditoria-del-proyecto.md`).
> `[x]` = implementado, `[ ]` = no implementado, `[~]` = parcial/inconsistente.

## 1. Propuesta de Valor

Sin cambios — es contenido de marca, no de implementación.

> No vendemos comida. Vendemos alivio cotidiano.
> Comer por elección, no por emoción (sin negar la emoción, acompañándola con conciencia).

## 2. MVP — Funcionalidades Fase 1

### 2.1 Gestión de Productos

- [ ] Crear productos vía API (nombre, descripción, precio, categoría, imagen) — no existe endpoint;
      `CreateProductRequest` está vacío. Los productos solo se cargan hoy vía `V2__seed.sql`.
- [x] Listar productos activos — `GET /api/products` (sin filtros, sin paginación).
- [ ] Listar productos con filtros (categoría, precio, búsqueda) — no implementado.
- [ ] Obtener detalle de producto — no existe `GET /api/products/{id}`.
- [ ] Búsqueda de productos por texto — no existe `GET /api/products/search`.

### 2.2 Gestión de Categorías

- [x] CRUD completo: crear, listar, obtener por ID, actualizar, desactivar (soft delete).
- [x] Listar categorías activas.
- [ ] Obtener productos por categoría — no existe `GET /api/products/category/{catId}`.
- [~] Nombre de categoría único — implementado de forma **inconsistente**: la aplicación solo
      compara contra categorías activas, pero la base de datos exige unicidad global; recrear una
      categoría con el nombre de una desactivada rompe en producción con un error 500 no controlado.

### 2.3 Gestión de Zonas de Entrega

- [ ] Crear zonas vía API (nombre, descripción, costo, día de entrega) — no existe endpoint;
      `CreateDeliveryZoneRequest` está vacío. Las zonas solo se cargan vía `V2__seed.sql`.
- [x] Listar zonas de entrega activas.
- [ ] Obtener detalle de zona — no existe `GET /api/delivery-zones/{id}`.
- [x] Aceptar pedidos cualquier día y agendar la próxima entrega (`validateOrderPeriod()` es
      intencionalmente un no-op).
- [x] Calcular fecha/franja y ventana (`díasMin`/`díasMax`) según la zona.
- [ ] Validar que la zona esté activa al crear una orden — **documentado como obligatorio, no
      implementado**: `DeliveryZoneService.getZoneById` no filtra por `isActive`.

**Zonas reales (seed):** Norte ($300, Viernes PM) y Sur ($300, Sábado AM). Este y Oeste son solo
diseño futuro, sin fila en base de datos.

### 2.4 Sistema de Pedidos por WhatsApp

- [x] Formulario de pedido (nombre, teléfono, dirección, zona, productos + cantidad).
- [x] Validación de datos del formulario vía Bean Validation (`@Valid`).
- [x] Validación de existencia de zona de entrega — **sin** validar que esté activa (ver 2.3).
- [x] Validación de existencia de producto — **sin** validar que esté activo.
- [x] Generación automática de mensaje WhatsApp formateado.
- [x] Cálculo automático de subtotal, costo de envío y total.
- [x] Guardado del pedido en BD para historial.
- [x] Link directo a WhatsApp (`https://wa.me/...`).
- [~] El link generado siempre apunta al número del negocio — cierto para `POST /api/orders` y
      `GET /api/orders/{id}`; **falso** para `GET /api/orders/customer/{phone}`, que hoy genera el
      link con el propio teléfono del cliente (bug, ver Fase 0).

## 3. Validaciones

### 3.1 Validaciones de Entrada

- [x] Nombre cliente: no vacío, máx 100 caracteres.
- [~] Teléfono: formato válido, mínimo 10 dígitos — implementado el mínimo; **no** hay cota máxima
      (documentada en 20 caracteres) pese a que la columna de BD es `VARCHAR(50)`.
- [x] Dirección: no vacía, máx 200 caracteres.
- [~] Zona de entrega: debe existir en BD — implementado; "debe estar activa" — no implementado.
- [~] Productos: mínimo 1 producto, cantidad mínimo 1 — implementado; "producto debe estar activo"
      — no implementado.
- [x] Periodo de pedidos: se acepta cualquier día; se informa la ventana (hasta viernes/sábado).

### 3.2 Validaciones de Negocio

- [x] Precio de producto: mayor a 0 (Bean Validation en la entidad; no reforzado por `CHECK` en BD).
- [x] Costo de envío: mayor a 0 (ídem).
- [x] Orden: `Total = Subtotal + Costo envío` (correcto en el cálculo de `OrderService`; sin
      invariante reforzada en BD ni en la entidad `Order`).

## 4. Datos Persistidos (PostgreSQL, vía Flyway — `ddl-auto=validate`)

Sin cambios estructurales respecto al esquema real (`V1__init.sql`): `categories`, `products`,
`delivery_zones`, `orders`, `order_items`, todas con `id` autoincremental y timestamps de auditoría.
Una diferencia relevante frente al documento legado: **no existen `CHECK` constraints** para
`price > 0`, `shipping_cost > 0` ni `total_price = subtotal + shipping_cost` — esas invariantes
viven únicamente en Bean Validation (parcial) y en la lógica de `OrderService` (para el total).

## 5. Comportamientos No Funcionales

- [x] API REST con respuestas JSON.
- [x] CORS habilitado (`WebConfig`, `allowedOrigins("*")` — solo apto para desarrollo).
- [ ] Manejo centralizado de excepciones — `GlobalExceptionHandler` existe como archivo pero está
      **completamente vacío** (sin `@ControllerAdvice` ni métodos); `ResourceNotFoundException` y
      `BusinessException` no son capturadas y terminan en un 500 genérico.
- [x] Validación de entrada con `@Valid` (funciona vía el manejo por defecto de Spring Boot para
      `MethodArgumentNotValidException`, que sí produce un 400 razonable — es la única vía de error
      controlada del sistema hoy).
- [x] Logs básicos (`java.util.logging.Logger` en cada service/controller — pendiente migrar a SLF4J, ver Fase 0).
- [x] Sin autenticación en el MVP (`SecurityConfig.permitAll()`).
- [x] Sin carrito persistente (se calcula en tiempo real en cada `POST /api/orders`).
- [x] Sin pago online.

## 6. Matriz de Responsabilidad (RACI)

Sin cambios respecto al documento legado; la matriz descriptiva de responsabilidades
Backend/Frontend sigue siendo válida para el alcance actual (listar, validar, calcular, generar
WhatsApp, persistir — todo del lado backend; formulario y apertura del link — frontend).

## 7. Estados de Orden

Ver `docs/domain/business-rules.md` §5.3 — enum completo definido (`OrderStatus`), pero **solo
`PENDING` y `SENT_TO_WHATSAPP` son alcanzables por código hoy**. El resto de transiciones
(`CONFIRMED`, `PREPARING`, `DISPATCHED`, `DELIVERED`, `CANCELLED`) están documentadas como manuales
por vendedor, y no tienen ningún endpoint ni mecanismo de actualización en el sistema actual.

## 8. Fases de Desarrollo

### Fase 1: MVP (actual)

- [~] Backend funcional — funcional para el flujo feliz principal (crear/consultar orden, CRUD de
      categorías, listados), pero con brechas de integridad (zona/producto inactivos no bloqueados),
      manejo de errores incompleto (excepciones sin capturar) y cero tests automatizados.
- [x] Categorías: CRUD completo.
- [~] Productos: solo lectura vía API (creación solo por seed SQL).
- [~] Zonas de entrega: solo lectura vía API (creación solo por seed SQL).
- [x] Pedidos por WhatsApp (con el bug de número destino en la consulta por cliente).
- [ ] Frontend simple.

### Fase 2 y Fase 3

Sin cambios respecto al documento legado — siguen siendo trabajo futuro no iniciado
(autenticación JWT, carrito persistente, tracking en tiempo real, pagos online, panel admin,
analytics).

## 9. Qué NO Incluir en el MVP

Sin cambios respecto al documento legado.

## 10. Formato de Errores

El formato JSON documentado (`timestamp`, `status`, `message`, `errors[]`) es el que produce por
defecto Spring Boot ante fallos de `@Valid`, pero **no** es el formato que se obtiene hoy ante un
`ResourceNotFoundException` o `BusinessException` (que devuelven un 500 genérico sin ese formato,
por la ausencia de `GlobalExceptionHandler`). Este documento describe el comportamiento objetivo;
implementarlo requiere resolver el hallazgo de manejo de excepciones de la Fase 0 antes de darlo
por cumplido.

## 11. Criterios de Aceptación

- [x] Todos los endpoints existentes funcionan sin autenticación.
- [x] La BD se genera/valida automáticamente con Flyway (`ddl-auto=validate` + migraciones).
- [~] Validaciones retornan errores claros en JSON — cierto solo para fallas de `@Valid`; falso
      para `ResourceNotFoundException`/`BusinessException` (500 genérico).
- [x] Mensaje de WhatsApp es legible y completo.
- [~] Link de WhatsApp abre correctamente — cierto en `POST /api/orders` y `GET /api/orders/{id}`;
      incorrecto en `GET /api/orders/customer/{phone}` (apunta al teléfono del cliente, no al del
      negocio).
- [x] El pedido se guarda correctamente en BD.
- [x] CORS permite requests desde cualquier origen (apto para desarrollo, revisar antes de producción).
