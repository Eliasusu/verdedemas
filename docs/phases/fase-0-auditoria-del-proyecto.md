## Fase 0 — Auditoría del Proyecto
**Estado:** ✅️ Completado
**Fecha:** 2026-08-23
**ADRs relacionados:** Ninguno (esta fase es de análisis/documentación; no involucra decisiones de diseño entre alternativas)

### Objetivo

Analizar el código actual de `verdedemas-backend` sin modificarlo, entender qué hace realmente,
detectar problemas y deuda técnica, y dejar una línea base documental confiable para las fases
siguientes.

### Actividades realizadas

1. Cruce de consistencia entre la documentación legacy de `.github/` y la de `docs/`/`CLAUDE.md`/
   `AGENTS.md` (contradicciones internas, datos desactualizados, endpoints fantasma) — corregido
   en una primera pasada dentro de `.github/`.
2. Auditoría profunda del código real en 4 frentes paralelos, usando las skills
   `domain-modeling`, `java-coding-standards` y `java-springboot`:
   - Dominio y reglas de negocio (modelo de negocio implícito, brechas doc↔código).
   - Arquitectura y estructura de paquetes (acoplamiento, convenciones Java/Spring, config).
   - Contrato de API real (endpoints, DTOs, validación, manejo de errores).
   - Tests, seguridad y deuda técnica general (cobertura, `SecurityConfig`, dependencias, código muerto).
3. Reconstrucción del modelo de negocio implícito directamente desde el código (no desde lo que
   los documentos decían que debería existir).
4. Migración de la documentación de negocio a `docs/`: `docs/domain/business-rules.md`,
   `docs/domain/requirements.md`, `docs/architecture.md`, `docs/api/endpoints.md` — reemplazando a
   `.github/bussines-rules/`, `.github/requirements/`, `.github/arquitecture/`,
   `.github/notes/project-structure.md` y `.github/ENDPOINTS.md`.
5. Eliminación completa de `.github/` (incluido `copilot-instructions.md`): se consolida la
   asistencia de IA en Claude Code, siguiendo el mismo criterio ya aplicado al retirar `.cursor/`.

### Conceptos involucrados

Reingeniería inversa, análisis de código, deuda técnica, Ubiquitous Language (para reconstruir el
modelo de negocio implícito sin usar los documentos como fuente de verdad).

### Modelo de negocio implícito reconstruido

**Entidades y relaciones reales** (todas heredan `createdAt`/`updatedAt` de `BaseEntity` vía
`@EntityListeners(AuditingEntityListener.class)`):

- **Category** (`name` único a nivel de BD —global—, `description`, `isActive`) `1 —— N`
  **Product**. Entidad de referencia simple, sin lógica de negocio más allá de "no duplicar
  nombre" (aplicada de forma inconsistente, ver hallazgos).
- **Product** (`name`, `description`, `price > 0`, `imageUrl`, `servings`, `usages`, `isActive`)
  pertenece a una `Category`. No tiene stock, no tiene endpoint de creación real (solo vía seed
  SQL), y su estado `isActive` **no se verifica** al usarlo en una orden.
- **DeliveryZone** (`name` único, `description`, `shippingCost > 0`, `deliveryDay` como `String`
  libre, `isActive`) es un catálogo estático (2 filas reales: Norte/Sur), sin endpoints de
  escritura. Su `isActive` tampoco se verifica al usarla en una orden, pese a estar documentado
  como obligatorio.
- **Order** (raíz de agregado real, único punto con lógica de negocio no trivial: cálculo de
  `subtotal`, `shippingCost`, `totalPrice`, generación de mensaje/link de WhatsApp y `status`)
  `1 —— N` **OrderItem** (`quantity`, `priceAtTime` — snapshot histórico del precio, correctamente
  desacoplado del precio actual del producto). `Order` referencia una `DeliveryZone` (congelada
  solo en `shippingCost`) y cada `OrderItem` referencia un `Product`.
- **OrderStatus**: enum con 7 estados (`PENDING → SENT_TO_WHATSAPP → CONFIRMED → PREPARING →
  DISPATCHED → DELIVERED`, más `CANCELLED`), pero **solo `PENDING` y `SENT_TO_WHATSAPP` se asignan
  por código**; el resto de transiciones son 100% manuales/futuras.
- **DeliveryDay**: enum de dominio (`FRIDAY_PM`, `SATURDAY_AM`, `SATURDAY_PM`) correctamente
  diseñado con `displayName`/`timeRange`, pero **no conectado** al modelo persistente
  (`DeliveryZone.deliveryDay` es `String`), por lo que en la práctica es código muerto duplicado
  por comparaciones de strings en `OrderService`.

**Lógica mal ubicada / dispersa:** ninguna lógica de negocio vive indebidamente en un controller
(los controllers son finos, delegan a services correctamente) ni hay acoplamiento directo entre
módulos vía repositorios ajenos (`OrderService` accede a `Product`/`DeliveryZone` siempre a través
de sus respectivos `Service`, patrón correcto). El problema no es "lógica en el lugar equivocado"
sino **lógica de negocio duplicada o ausente**: cálculo de horarios de entrega duplicado (enum vs.
switch), invariantes de activo/inactivo ausentes, y verificación de unicidad de categoría que no
coincide con la restricción real de BD.

Detalle completo de reglas y su estado real (✅/⚠️/⏳) en `docs/domain/business-rules.md` y
`docs/domain/requirements.md`.

### Diagnóstico / Hallazgos

#### Seguridad

- **[Alta] API completamente abierta: sin autenticación y con CORS a cualquier origen** —
  `SecurityConfig.java` hace `.anyRequest().permitAll()` con CSRF deshabilitado, y `WebConfig.java`
  configura `allowedOrigins("*")` para `/api/**` con todos los métodos HTTP. Cualquiera puede
  crear/editar/borrar categorías o crear pedidos sin credenciales, desde cualquier origen web.
  - *Sugerencia:* si el MVP realmente no requiere login todavía, documentarlo como decisión
    temporal explícita; evaluar restringir al menos los endpoints de escritura de catálogo
    (categorías) detrás de autenticación, y acotar `allowedOrigins` a los dominios reales del
    frontend/app una vez estén definidos.
  - *Fundamento:* principio de mínimo privilegio — sin auth y sin CORS acotado, la superficie de
    ataque incluye manipulación arbitraria del catálogo y scraping automatizado desde cualquier sitio.

- **[Alta] Credenciales de base de datos en texto plano, committeadas al repo** —
  `application.properties` fija `spring.datasource.username=postgres` y `password=root`
  hardcodeados, sin perfiles `dev`/`prod` ni variables de entorno, y sin entrada en `.gitignore`
  para excluir un archivo de configuración local.
  - *Sugerencia:* externalizar a variables de entorno (`spring.datasource.password=${DB_PASSWORD}`)
    e introducir `application-local.properties` ignorado por git para desarrollo.
  - *Fundamento:* nunca commitear secretos, incluso de desarrollo — el historial de git perdura;
    práctica estándar de 12-factor apps.

#### Manejo de errores / Excepciones

- **[Alta] `GlobalExceptionHandler` existe pero está completamente vacío** — sin `@ControllerAdvice`
  ni ningún `@ExceptionHandler`. `ResourceNotFoundException` y `BusinessException` (usadas en los
  4 services) no tienen `@ResponseStatus` propio, por lo que hoy **cualquier** "no encontrado" o
  error de negocio responde `500 Internal Server Error` en vez de `404`/`400`/`409`. Esto afecta a
  todos los endpoints documentados en `docs/api/endpoints.md`.
  - *Sugerencia:*
    ```java
    @RestControllerAdvice
    public class GlobalExceptionHandler {
        @ExceptionHandler(ResourceNotFoundException.class)
        ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException e) { ... } // 404

        @ExceptionHandler(BusinessException.class)
        ResponseEntity<ErrorResponse> handleBusiness(BusinessException e) { ... } // 400/409
    }
    ```
  - *Fundamento:* los códigos de estado HTTP deben comunicar semántica de la operación
    (RFC 9110); un 500 para "no encontrado" impide que el cliente diferencie errores de cliente
    vs. servidor, y expone potencialmente detalles internos.

#### Contrato de API / DTOs

- **[Alta] `Product` y `DeliveryZone` devuelven entidades JPA crudas en vez de DTOs** —
  `ProductController.list()` y `DeliveryZoneController.list()` serializan la entidad `@Entity`
  directamente, exponiendo la relación `Category` completa anidada y campos de auditoría interna
  (`createdAt`/`updatedAt`). Los DTOs pensados para esto (`ProductResponse`,
  `DeliveryZoneResponse`) existen pero están vacíos y sin usar.
  - *Sugerencia:* implementar `ProductResponse`/`DeliveryZoneResponse` con los campos necesarios y
    mapear en el controller o un mapper, siguiendo el patrón que ya usa `CategoryController`.
  - *Fundamento:* separar el modelo de persistencia del modelo de API evita que un cambio interno
    de esquema rompa el contrato público (regla propia de `docs/architecture.md`).

- **[Alta] Typo de paquete `dto/reponse` (falta la "s") con una clase duplicada y muerta** —
  Aparece en `product`, `category` y `deliveryzone`. En `category` convive
  `dto/reponse/CreateCategoryRequest.java` (vacía, sin uso) con la clase real y usada
  `dto/request/CreateCategoryRequest.java`. En `product`/`deliveryzone` no hay duplicado, pero los
  archivos en `dto/reponse/` están vacíos y sin conectar.
  - *Sugerencia:* eliminar la clase duplicada muerta en `category`, renombrar los paquetes
    `reponse` → `response` en los tres módulos, e implementar el contenido real de los DTOs de
    `product`/`deliveryzone` cuando se agreguen sus endpoints de escritura.
  - *Fundamento:* código muerto y namespaces inconsistentes aumentan el costo cognitivo de
    navegar el proyecto y el riesgo de completar el archivo equivocado.

- **[Media] `POST /api/orders` responde `200 OK` en vez de `201 Created`** — a diferencia de
  `POST /api/categories`, que sí anota `@ResponseStatus(HttpStatus.CREATED)`. Mismo tipo de
  operación (creación de recurso), dos contratos de status distintos.
  - *Sugerencia:* agregar `@ResponseStatus(HttpStatus.CREATED)` a `OrderController.create`.
  - *Fundamento:* consistencia de contrato — toda creación exitosa debería devolver `201` (RFC
    9110 §15.3.2).

- **[Media] Sin versionado de API ni paginación en los listados** — todos los paths son
  `/api/...` (sin `/api/v1/...`), y `GET /api/products`, `/api/delivery-zones` y `/api/categories`
  devuelven arrays completos sin `page`/`size`. Hoy es inofensivo (catálogo de 2-2 filas), pero no
  hay ningún mecanismo de paginación en el código.
  - *Sugerencia:* adoptar `/api/v1/...` antes de tener consumidores externos atados al contrato, e
    introducir `Pageable`/`Page<T>` cuando el catálogo crezca.
  - *Fundamento:* ambos cambios son baratos de introducir temprano y muy caros después de tener
    clientes (app móvil, web) en producción.

#### Modelo de dominio / Reglas de negocio

- **[Alta] `OrderService` no valida que la zona de entrega ni el producto estén activos al crear
  una orden** — `DeliveryZoneService.getZoneById` y `ProductService.getProductById` hacen
  `findById` sin filtrar por `isActive`, pese a que `docs/domain/business-rules.md` §4.4/§5.3 los
  documenta como obligatorios.
  - *Sugerencia:* agregar el filtro `isActive` en ambos métodos (o crear variantes específicas
    usadas por el flujo de creación de orden) y lanzar `BusinessException` si no se cumple.
  - *Fundamento:* una regla de negocio documentada como invariante debe vivir en el dominio/
    aplicación, no solo en un `.md`.

- **[Alta] Bug: el link de WhatsApp de `getOrdersByCustomerPhone` apunta al cliente, no al
  negocio** — a diferencia de `createAndSendToWhatsApp` y `getOrderById`, que usan
  `Constants.MANAGER_PHONE`, este método usa `order.getCustomerPhone()` como destinatario.
  - *Sugerencia:* unificar los tres métodos para usar siempre `Constants.MANAGER_PHONE`, y
    extraer la construcción del link a un único helper reutilizado.
  - *Fundamento:* DRY / Single Source of Truth — "a quién le hablamos por WhatsApp" es una regla
    de negocio única que no debería divergir según el método que la invoque.

- **[Alta] Verificación de unicidad de nombre de categoría inconsistente con la restricción real
  de BD** — `CategoryService.create/update` validan duplicados solo contra categorías **activas**,
  pero `categories.name` tiene `UNIQUE` **global** en el esquema. Recrear una categoría con el
  nombre de una desactivada pasa la validación de aplicación y falla en BD (sin capturar, por el
  hallazgo de `GlobalExceptionHandler` vacío).
  - *Sugerencia:* decidir una única semántica (unicidad global vs. índice único parcial
    `WHERE is_active`) y testear explícitamente el caso "recrear categoría con nombre de una
    desactivada".
  - *Fundamento:* dos capas (aplicación y esquema) que dicen cosas distintas sobre la misma regla
    es fuente clásica de bugs de producción.

- **[Media] `deliveryDay` es un `String` libre pese a existir el enum `DeliveryDay` sin usar** —
  `OrderService` compara strings mágicos (`"FRIDAY_PM".equals(...)`) y duplica el mapeo
  string→horario en dos métodos distintos que reproducen exactamente `DeliveryDay.getTimeRange()`.
  - *Sugerencia:* cambiar el campo a `@Enumerated(EnumType.STRING) private DeliveryDay
    deliveryDay;` y delegar a los métodos ya existentes del enum, eliminando la duplicación.
  - *Fundamento:* el dominio ya nombró el concepto (Ubiquitous Language); la implementación lo
    ignora y lo reemplaza por strings mágicos dispersos.

- **[Media] Categoría desactivada no verifica productos activos asociados** —
  `CategoryService.delete()` marca `isActive=false` sin comprobar productos activos con esa
  categoría, lo que puede dejar productos visibles cuya categoría ya no aparece en el catálogo.
  - *Sugerencia:* antes de desactivar, verificar productos activos asociados y lanzar
    `BusinessException`, o documentar explícitamente que es un comportamiento aceptado.
  - *Fundamento:* integridad referencial a nivel de dominio, no solo de FK en base de datos.

- **[Media] Persistencia redundante de `OrderItem`** — se guarda manualmente vía
  `orderItemRepository.save()` y además `Order.items` tiene `cascade = ALL`, por lo que el
  posterior `orderRepository.save()` vuelve a persistir los mismos items por cascada.
  - *Sugerencia:* apoyarse únicamente en la cascada del Aggregate Root (`Order`), sin guardar
    `OrderItem` de forma independiente.
  - *Fundamento:* patrón Aggregate Root — las modificaciones a una entidad "parte" no deberían
    tener un camino de persistencia propio fuera de su raíz.

- **[Media] Sin `CHECK` constraints en BD para invariantes de dinero** — ni `price > 0`, ni
  `shipping_cost > 0`, ni `total_price = subtotal + shipping_cost` están reforzadas a nivel de
  esquema; con `ddl-auto=validate`, Hibernate nunca las agregará por sí solo.
  - *Sugerencia:* decidir conscientemente si estas invariantes ameritan una migración Flyway con
    `CHECK`, o si basta con reforzarlas por test sobre `OrderService`.
  - *Fundamento:* defensa en profundidad — invariantes de dinero no deberían depender únicamente
    de que todo el tráfico pase por un único servicio.

#### Calidad de código / Estilo

- **[Alta] `@Data` de Lombok en la relación bidireccional `Order`↔`OrderItem` → riesgo real de
  `StackOverflowError`** — ambas entidades usan `@Data` (genera `toString`/`equals`/`hashCode`
  incluyendo la relación mutua) sin `@ToString.Exclude`/`@EqualsAndHashCode.Exclude`. Cualquier
  log, debug o breakpoint que invoque `toString()` sobre una `Order` con items cargados entra en
  recursión infinita.
  - *Sugerencia:* excluir el lado "padre" de la relación (`OrderItem.order`) de
    `toString`/`equals`/`hashCode`, o reemplazar `@Data` por `@Getter/@Setter` + identidad basada
    solo en `id` en las entidades JPA.
  - *Fundamento:* anti-patrón conocido de Lombok + JPA en relaciones bidireccionales.

- **[Media] Uso de `java.util.logging.Logger` en vez de SLF4J, con concatenación de strings** —
  en todos los controllers y services (`log.info("Obteniendo categoría: " + id)`).
  - *Sugerencia:* migrar a SLF4J (`@Slf4j` de Lombok, ya en el classpath) con placeholders `{}`.
  - *Fundamento:* logging parametrizado con SLF4J es el estándar de facto en Spring Boot; evita
    concatenaciones innecesarias y es compatible con cualquier binding de logging.

- **[Media] `logging.level.com.verdedemas=DEBUG` no controla el logging real de la app** — apunta
  al `groupId` de Maven (`com.verdedemas`), no al paquete Java real (`com.eliasit.verdedemas`);
  es un no-op silencioso.
  - *Sugerencia:* corregir a `logging.level.com.eliasit.verdedemas=DEBUG`.
  - *Fundamento:* configuración externalizada debe ser correcta y verificable; este mismatch es
    la misma clase de error (copy-paste del groupId) que causó otras confusiones ya documentadas.

- **[Baja] Dependencias declaradas sin ningún uso real (JWT, WebSocket, testing avanzado)** —
  `jjwt-*` y `spring-boot-starter-websocket` no tienen ninguna clase que las use; las dependencias
  de testing (`AssertJ`, `Mockito`) tampoco se usan porque no hay tests reales más allá de
  `contextLoads()`.
  - *Sugerencia:* retirarlas del `pom.xml` hasta tener un caso de uso concreto, o documentar la
    decisión de dejarlas como placeholder deliberado.
  - *Fundamento:* "no introducir tecnologías sin un problema real que las justifique" (regla
    propia del proyecto) aplica también a dependencias latentes.

- **[Baja] Valor de negocio hardcodeado como constante Java** — `Constants.MANAGER_PHONE` (número
  de WhatsApp del encargado) está compilado en código en vez de ser configurable.
  - *Sugerencia:* mover a `application.properties` inyectado con `@Value`.
  - *Fundamento:* configuración operativa que puede cambiar sin tocar código no debería requerir
    una recompilación.

#### Testing

- **[Alta] Cobertura de tests prácticamente nula** — el único test del repositorio es
  `VerdedemasApplicationTests.contextLoads()` (vacío). El 100% de las 8 clases de negocio
  principales (4 services + 4 controllers) no tiene ningún test, incluyendo el cálculo de totales
  de `OrderService`, que es la lógica más crítica del dominio.
  - *Sugerencia:* empezar por tests unitarios de `OrderService` (cálculo de subtotal/total,
    validación de zona/producto) y tests de aceptación `@WebMvcTest` para los controllers,
    siguiendo TDD (Red-Green-Refactor) como indica `.claude/rules/testing.md` — por ejemplo, un
    test que espere `404` en `GET /api/categories/999` (hoy falla porque da `500`) es un buen
    punto de partida para justificar la implementación de `GlobalExceptionHandler`.
  - *Fundamento:* regla propia del proyecto ("todo nuevo comportamiento debe estar cubierto por
    tests"); sin tests no hay red de seguridad para refactorizar nada de lo diagnosticado acá.

### Aspectos correctos verificados

Para no perder de vista lo que sí está bien hecho:

- `OrderService` colabora con `ProductService`/`DeliveryZoneService` en vez de acceder a sus
  repositorios directamente — comunicación correcta entre módulos.
- No hay riesgo de inyección SQL: todos los repositorios usan Spring Data JPA con métodos
  derivados, parametrizados automáticamente.
- `priceAtTime` congela correctamente el precio histórico en `OrderItem`, protegiendo órdenes
  pasadas de cambios de precio.
- El cálculo `Total = Subtotal + Costo de envío` es correcto en `OrderService`.
- Bean Validation (`@Valid`) funciona correctamente hoy para `CreateOrderRequest`/
  `CreateCategoryRequest`, devolviendo `400` sin necesidad de un handler custom.
- El endpoint `GET /api/orders/customer/{phone}` (no documentado en la doc legacy) sí existe y
  quedó incorporado a `docs/api/endpoints.md`.

### Notas

- La documentación de negocio legacy (`.github/bussines-rules/`, `.github/requirements/`,
  `.github/arquitecture/`, `.github/notes/`, `.github/ENDPOINTS.md`, `.github/copilot-instructions.md`)
  fue reemplazada por `docs/domain/business-rules.md`, `docs/domain/requirements.md`,
  `docs/architecture.md` y `docs/api/endpoints.md`, y `.github/` fue eliminado por completo.
- Los hallazgos "Alta" de esta fase son el punto de partida natural para las Fases 1-3 (Entender
  el Dominio, DDD, TDD) — varios de ellos ya apuntan directamente a Value Objects/Aggregates
  candidatos (`DeliveryDay`, `Order`+`OrderItem`) que se detallan en `docs/architecture.md` §
  "Arquitectura objetivo".
- Esta fase es solo de documentación y análisis: ningún hallazgo fue corregido en el código.
  La ejecución de los to-do queda a cargo del usuario.

### To do

- [ ] **[Alta] Implementar `GlobalExceptionHandler` real** (`@RestControllerAdvice` con handlers
      para `ResourceNotFoundException` → 404 y `BusinessException` → 400/409)
  - *Sugerencia de implementación:* ver bloque de código en el hallazgo correspondiente arriba.
  - *Fundamento:* desbloquea el contrato de errores correcto para TODOS los endpoints a la vez.

- [ ] **[Alta] Completar DTOs de `Product`/`DeliveryZone` y dejar de exponer entidades JPA**
  - *Sugerencia de implementación:* implementar `ProductResponse`/`DeliveryZoneResponse` y mapear
    en el controller, siguiendo el patrón de `CategoryController.convertToResponse()`.
  - *Fundamento:* evita acoplar el contrato público al esquema de persistencia.

- [ ] **[Alta] Corregir el typo de paquete `dto/reponse` → `dto/response` y borrar la clase
      duplicada muerta en `category`**
  - *Sugerencia de implementación:* renombrar paquetes en `product`, `category`, `deliveryzone`;
    eliminar `category/dto/reponse/CreateCategoryRequest.java`.
  - *Fundamento:* elimina código muerto y ruido de navegación.

- [ ] **[Alta] Validar `isActive` de zona y producto al crear una orden**
  - *Sugerencia de implementación:* filtrar por `isActive` en `getZoneById`/`getProductById` (o
    variantes específicas para el flujo de creación) y lanzar `BusinessException`.
  - *Fundamento:* cierra la brecha entre lo documentado como obligatorio y lo realmente aplicado.

- [ ] **[Alta] Corregir el bug de `whatsappLink` en `getOrdersByCustomerPhone`**
  - *Sugerencia de implementación:* usar `Constants.MANAGER_PHONE` como en los otros dos métodos;
    extraer un helper único.
  - *Fundamento:* consistencia semántica de un mismo campo de respuesta entre endpoints.

- [ ] **[Alta] Resolver la inconsistencia de unicidad de nombre de categoría (app vs. BD)**
  - *Sugerencia de implementación:* elegir semántica única (global vs. índice parcial) y agregar
    un test para el caso "recrear categoría con nombre de una desactivada".
  - *Fundamento:* evita un 500 no controlado en producción ante un caso de uso plausible.

- [ ] **[Alta] Excluir la relación bidireccional `Order`↔`OrderItem` de `toString`/`equals`/`hashCode`**
  - *Sugerencia de implementación:* `@ToString.Exclude`/`@EqualsAndHashCode.Exclude` en
    `OrderItem.order`, o reemplazar `@Data` por `@Getter/@Setter` + identidad por `id`.
  - *Fundamento:* previene un `StackOverflowError` real ante cualquier log/debug de una `Order`.

- [ ] **[Alta] Externalizar credenciales de base de datos**
  - *Sugerencia de implementación:* `spring.datasource.password=${DB_PASSWORD}` +
    `application-local.properties` ignorado por git.
  - *Fundamento:* nunca commitear secretos, ni siquiera de desarrollo.

- [ ] **[Alta] Decidir y documentar (ADR) la postura de seguridad temporal del MVP**
  - *Sugerencia de implementación:* si se mantiene sin auth por ahora, registrarlo como decisión
    consciente; acotar `allowedOrigins` a los dominios reales cuando existan.
  - *Fundamento:* "no esconder trade-offs" — que sea una decisión explícita, no un olvido.

- [ ] **[Alta] Empezar la suite de tests por `OrderService` y `CategoryService`**
  - *Sugerencia de implementación:* TDD sobre cálculo de totales, validación de zona/producto
    activo, y unicidad de categoría — los tres invariantes de negocio más frágiles detectados acá.
  - *Fundamento:* sin tests, cualquier corrección de los puntos anteriores no tiene red de
    seguridad.

- [ ] **[Media] Migrar `deliveryDay` a `@Enumerated(EnumType.STRING)` usando el enum `DeliveryDay`**
  - *Fundamento:* elimina strings mágicos y lógica duplicada en `OrderService`.

- [ ] **[Media] Migrar logging a SLF4J parametrizado y corregir `logging.level.com.verdedemas`**
  - *Fundamento:* logging correcto y activable de verdad en desarrollo.

- [ ] **[Media] Agregar `@ResponseStatus(HttpStatus.CREATED)` a `POST /api/orders`**
  - *Fundamento:* consistencia de contrato con `POST /api/categories`.

- [ ] **[Media] Evaluar versionado (`/api/v1`) y paginación antes de sumar consumidores externos**
  - *Fundamento:* mucho más barato de introducir ahora que después de tener clientes en producción.

- [ ] **[Media] Verificar productos activos antes de desactivar una categoría**
  - *Fundamento:* integridad referencial a nivel de dominio.

- [ ] **[Media] Unificar la persistencia de `OrderItem` a través de la cascada de `Order`**
  - *Fundamento:* respeta el patrón Aggregate Root, evita doble escritura.

- [ ] **[Media] Decidir si las invariantes de dinero necesitan `CHECK` en BD**
  - *Fundamento:* defensa en profundidad más allá de `OrderService`.

- [ ] **[Baja] Retirar o justificar (ADR) las dependencias JWT/WebSocket sin uso**
- [ ] **[Baja] Mover `Constants.MANAGER_PHONE` a configuración externa**
