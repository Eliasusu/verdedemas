## Fase 1 — Entender el Dominio
**Estado:** 🔵 En curso
**Fecha:** 2026-09-28
**ADRs relacionados:** Ninguno (las decisiones de esta fase son de modelado conceptual; los ADRs nacerán cuando se implementen en la Fase 2+)

### Objetivo

Descubrir el dominio de VerdeDeMas conceptualmente, sin mirar Java: actores, entidades, value objects,
agregados, reglas de negocio, lenguaje ubicuo y bounded contexts. El resultado vive en
**[docs/domain/domain-model.md](../domain/domain-model.md)**.

### Actividades realizadas

1. Relectura de `docs/domain/business-rules.md` y `docs/domain/requirements.md` (as-is verificado en Fase 0).
2. Sesión de *grilling* (preguntas y respuestas en rondas, 25 decisiones) para pasar del modelo implícito en el código a un modelo to-be.
3. Redacción de `docs/domain/domain-model.md`: glosario, mapa de contextos, agregados, estados de Pedido y Ciclo, invariantes, eventos y preguntas abiertas.

### Conceptos involucrados

Lenguaje Ubicuo, Entity vs Value Object, Aggregate (y "agregados chicos" / referencia por ID),
Invariantes, Snapshot de datos (congelar precio/nombre/fecha), Bounded Context y Context Map,
subdominios núcleo / soporte / genérico, Domain Events, Read Models (vistas derivadas).

### Decisiones tomadas

| # | Decisión | Fundamento |
|---|---|---|
| Q1 | El modelo es **to-be conceptual**, no un reflejo del código. | La fase es "sin Java" y la Fase 2 necesita un modelo objetivo contra el cual refactorizar. Las brechas ya están en `business-rules.md`. |
| Q2 | Proceso en este documento, resultado en `domain-model.md`. | Separar el *por qué* del artefacto que consume la Fase 2. |
| Q3 | Lenguaje ubicuo en **español**, con columna de nombre en código. | El lenguaje ubicuo es el del negocio; la columna expone divergencias (ej. *Order* vs *Pedido*). |
| Q4 | Actores: **Cliente** y **Negocio**. El repartidor no es actor. | Vendedor y admin son el mismo rol en la práctica; el reparto no se registra en el sistema. |
| Q5 → Q17 | **Cliente es entidad con cuenta** (login con Google). El pedido guarda `ClienteId` **y** una *Foto de entrega* (VO). | El cliente debe poder volver a su pedido para editarlo; la dirección de un pedido pasado no debe cambiar si cambia el perfil. |
| Q6 | `PENDING` + `SENT_TO_WHATSAPP` se unifican en **Solicitado**. | "Se generó un link" no es observable ni accionable para el negocio. |
| Q7 | **Calendario operativo configurable**: ventana de pedidos, corte, producción y entrega definidos por el Negocio. | Reemplaza el "próximo viernes/sábado" hardcodeado; el negocio quiere versatilidad total. |
| Q8 / Q9 | Calendario **global**; **franja de entrega por zona** (el Negocio reparte zona por zona). La franja debe caer después de la producción. | Modelo más simple que cubre la operatoria real. |
| Q10 | El pedido **congela su fecha comprometida**. | Mismo principio que `priceAtTime`: el pedido registra lo acordado. |
| Q11 | Solo el Negocio avanza estados; se cancela hasta antes de *Despachado*; sin vencimiento automático. | Control operativo en manos del Negocio. |
| Q12 / Q18 | El cliente **edita/cancela desde la app** mientras el pedido está *Solicitado* **y** antes del corte. Líneas nuevas toman precio vigente; cambio de zona recalcula fecha. | Refleja la negociación real sin romper la producción ya planificada. |
| Q13 | Capacidad/stock por ciclo **fuera del modelo** (pregunta abierta). | No hay un problema real que lo justifique todavía. |
| Q14 | "Categoría con mínimo 2 productos" se reformula como **regla de visibilidad**; se mantiene "no desactivar con productos activos"; producto ∈ exactamente 1 categoría. | La regla original era incumplible (una categoría nace vacía). |
| Q15 | Visión completa etiquetada **MVP / Futuro**. | Brújula para Fases 2-6 sin forzar una reescritura masiva. |
| Q16 | **WhatsApp sale del dominio**: canal de notificación que reacciona a eventos. | La app pasa a ser e-commerce (cliente) + CRM (negocio); el checkout es *Solicitar pedido*. |
| Q19 | CRM MVP: pedidos por ciclo, **Lista de producción**, **Hoja de reparto**, gestión de catálogo/zonas/calendario. Ficha de cliente: Futuro. | Las vistas por tanda son el valor real para un negocio que produce por ciclo. |
| Q20 | Pago: *medio de pago* + marca *Cobrado* (ortogonal al estado). Pago online: Futuro. | Responder "¿quién me debe plata este ciclo?". |
| Q21 | **Ciclo es entidad** que congela la configuración; "En preparación" pasa al Ciclo. El Ciclo **no contiene** pedidos. | Corte, producción y bloqueo de edición son del ciclo; agregados chicos evitan contención. |
| Q22 | 7 agregados: Categoría, Producto, Zona, Calendario, Ciclo, Cliente, Pedido. La línea congela **nombre y precio**. | Consistencia transaccional por agregado; histórico fiel. |
| Q23 | Desactivar producto/zona solo bloquea **pedidos nuevos**. | Desactivar = "no se ofrece más"; las fotos protegen lo existente. |
| Q24 | **Admins se crean manualmente en BD** con rol ADMIN (también entran con Google). Sin gestión de roles. | Una o dos personas; una UI de roles sería overengineering. |
| Q25 | Bounded contexts: **Catálogo**, **Ventas**, **Operaciones** (+ genéricos **Identidad** y **Notificaciones**). *Cliente* vive en Ventas. | Serán los módulos del monolito modular (Fase 5). |

### Diagnóstico / Hallazgos

Brechas entre el modelo to-be y el código actual (insumo para la Fase 2):

- **[Alta] No existe el concepto de Ciclo ni de Calendario** — la ventana de entrega se calcula con `TemporalAdjusters.next(...)` hardcodeado, sin corte.
  - *Sugerencia:* introducir `CalendarioOperativo` y `Ciclo` como agregados en el módulo Operaciones.
  - *Fundamento:* son el eje de la operatoria semanal (Q7, Q21).
- **[Alta] No existe Cliente ni autenticación** — los datos del cliente son campos sueltos en `Order` y todo el tráfico es `permitAll()`.
  - *Sugerencia:* agregado `Cliente` en Ventas + login Google en Identidad; `Order` pasa a referenciar `ClienteId` y a embeber `FotoDeEntrega`.
  - *Fundamento:* Q17, Q24.
- **[Alta] WhatsApp está acoplado al caso de uso de creación de orden** (`createAndSendToWhatsApp`).
  - *Sugerencia:* emitir `PedidoSolicitado` y mover WhatsApp a un adaptador de Notificaciones.
  - *Fundamento:* Q16; habilita Fases 7/8 con un caso de uso real.
- **[Media] Estados del pedido no coinciden** — el enum tiene `PENDING`, `SENT_TO_WHATSAPP`, `PREPARING`, que desaparecen o se mueven al Ciclo.
  - *Sugerencia:* redefinir `EstadoPedido` según `domain-model.md` §4.
  - *Fundamento:* Q6, Q21.
- **[Media] La franja de entrega vive en un String libre y un enum no usado** (`deliveryDay`, `DeliveryDay`).
  - *Sugerencia:* VO `FranjaDeEntrega` dentro de la zona.
  - *Fundamento:* Q8.
- **[Media] La línea de pedido solo congela el precio, no el nombre.**
  - *Sugerencia:* agregar nombre congelado a `OrderItem`.
  - *Fundamento:* Q22.

### Notas

- Preguntas abiertas: capacidad de producción por ciclo, pedido despachado no entregado, reprogramación de pedidos (ver `domain-model.md` §8).
- `PedidoSolicitado` y demás eventos son el primer caso de uso real para la Fase 7 (eventos) y posiblemente la Fase 8 (Kafka).
- Lista de producción / Hoja de reparto son candidatas naturales a read models para evaluar en la Fase 9 (CQRS).

### To do

- [ ] **[Alta] Validar el modelo con el negocio real** (el cliente de VerdeDeMas) antes de cerrar la fase
  - *Sugerencia de implementación:*
    ```
    Recorrer glosario + diagramas de estado con el cliente; registrar ajustes en este documento.
    ```
  - *Fundamento:* el lenguaje ubicuo se valida con los expertos del dominio, no solo con el equipo técnico.
- [ ] **[Media] Resolver las preguntas abiertas** de `domain-model.md` §8
  - *Sugerencia de implementación:*
    ```
    Nueva ronda de grilling enfocada en excepciones de entrega (no entregado / reprogramar).
    ```
  - *Fundamento:* son flujos que el Negocio va a necesitar apenas opere con el CRM.
- [ ] **[Media] Priorizar el orden de implementación para la Fase 2**
  - *Sugerencia de implementación:*
    ```
    1. Pedido (estados, invariantes, fotos)  2. Zona + Franja  3. Calendario + Ciclo  4. Cliente + Identidad
    ```
  - *Fundamento:* empezar por el agregado núcleo que ya existe en el código minimiza el riesgo de reescritura masiva.
