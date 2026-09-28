# Contexto de VerdeDeMas

**Última actualización:** 2026-09-28 (Fase 1 en curso)

## Propósito

VerdeDeMas es un proyecto que empezo para solucionar un problema puntual, una persona que queria empezar a empreder, necesitaba un sitio rapido y que no tenga compliance de pagos. El proyecto luego evoluciono para ser uno de ejemplo para aprender y demostrar habilidades de Backend Engineering. El objetivo final es tener un sistema con dominio modelado, arquitectura limpia, tests, eventos y documentación, que sirva como portfolio profesional.

## Estado actual

- Repositorio clonado desde `https://github.com/Eliasusu/verdedemas`.
- El código actual es un **MVP funcional** con Spring Boot, sin una arquitectura definida ni pruebas significativas (0% de cobertura más allá de un test de carga de contexto).
- **Fase 0 (Auditoría) completada.** El diagnóstico completo está en `docs/phases/fase-0-auditoria-del-proyecto.md`. La documentación de negocio/arquitectura/API fue migrada a `docs/` (ver sección "Documentación" abajo) y `.github/` (Copilot, docs legacy) fue eliminado por completo — la asistencia de IA quedó consolidada en Claude Code.
- **Fase 1 (Entender el Dominio) en curso.** Se definió el modelo de dominio to-be en
  `docs/domain/domain-model.md`; las 25 decisiones de modelado están en
  `docs/phases/fase-1-entender-el-dominio.md`. Falta validarlo con el negocio real y resolver las
  preguntas abiertas antes de cerrar la fase.

## Dominio

### Modelo objetivo (to-be, Fase 1)

Detalle completo en `docs/domain/domain-model.md`. Resumen:

- **Visión**: app con dos caras — **e-commerce** para el *Cliente* (login con Google) y **CRM**
  para el *Negocio* (admins creados manualmente en BD). WhatsApp deja de ser el checkout y pasa a
  ser un canal de notificación que reacciona a eventos de dominio.
- **Operatoria por ciclos**: un *Calendario operativo* configurable define ventana de pedidos,
  corte y producción; cada *Zona de entrega* tiene su propia *Franja*. Cada semana es un *Ciclo*
  que congela esa configuración.
- **Agregados**: Categoría, Producto, Zona de entrega, Calendario operativo, Ciclo, Cliente, Pedido.
- **Pedido**: `Solicitado → Confirmado → Despachado → Entregado` (+ *Cancelado*). El cliente lo
  edita/cancela mientras está *Solicitado* y antes del corte. Congela precio y nombre por línea,
  foto de entrega y fecha comprometida.
- **Bounded contexts**: Catálogo, Ventas, Operaciones (+ genéricos Identidad y Notificaciones).
- Cada concepto está etiquetado **MVP** o **Futuro**.

### Modelo actual (as-is, Fase 0)

Reconstruido desde el código real (no desde documentación aspiracional) durante la Fase 0. Ver
`docs/domain/business-rules.md` para el detalle completo con estado ✅/⚠️/⏳ de cada regla.

- **Entidades reales**: `Category` 1—N `Product`; `DeliveryZone` (catálogo estático); `Order`
  (raíz de agregado) 1—N `OrderItem`. `OrderStatus` (7 estados, solo 2 alcanzables por código hoy)
  y `DeliveryDay` (enum ya modelado pero sin usar en la entidad) completan el dominio.
- **Casos de uso implementados**: CRUD de categorías, listado de productos/zonas, creación y
  consulta de pedidos con generación de link de WhatsApp.
- **Brechas doc↔código detectadas**: varias reglas de negocio documentadas (zona/producto activos
  al crear orden, mínimo de productos por categoría, integridad al desactivar categoría) no están
  aplicadas en el código — ver diagnóstico de Fase 0 para el detalle priorizado.

## Arquitectura actual

- Capas técnicas simples: Controller → Service → Repository → Entity, organizadas por módulo de
  negocio (`category`, `product`, `deliveryzone`, `order`).
- No hay separación entre dominio, aplicación e infraestructura (aspiración de Fase 2/5, no estado
  actual).
- La lógica de negocio SÍ está concentrada correctamente en los `Service` (no dispersa en
  controllers), pero hay deuda real: `GlobalExceptionHandler` vacío, DTOs faltantes en
  `Product`/`DeliveryZone`, y varios anti-patrones de Lombok en entidades JPA.
- Detalle completo en `docs/architecture.md`.

## Documentación

- `docs/domain/business-rules.md` — reglas de negocio con estado real verificado.
- `docs/domain/requirements.md` — requerimientos del MVP con estado real verificado.
- `docs/architecture.md` — arquitectura actual + arquitectura objetivo (Fase 2/5).
- `docs/api/endpoints.md` — contrato real de la API.
- `docs/domain/domain-model.md` — modelo de dominio to-be (glosario, agregados, estados, invariantes).
- `docs/phases/fase-0-auditoria-del-proyecto.md` — diagnóstico completo de la Fase 0.
- `docs/phases/fase-1-entender-el-dominio.md` — decisiones de modelado de la Fase 1.

## Próximos pasos

1. ~~Realizar la auditoría completa del código (Fase 0).~~ ✅ Completado — ver
   `docs/phases/fase-0-auditoria-del-proyecto.md`.
2. Entender el dominio y modelarlo (Fase 1). 🔵 En curso — falta validación con el negocio y preguntas abiertas.
3. Aplicar DDD progresivamente (Fase 2).
4. Introducir TDD (Fase 3) y BDD (Fase 4).
5. Rediseñar la arquitectura (Fase 5).
6. Mejorar el diseño de API (Fase 6).
7. Evaluar eventos y mensajería (Fases 7-8).
8. Considerar CQRS (Fase 9).
9. Dockerizar y configurar CI/CD (Fase 10).
10. Documentar decisiones (Fase 11).
11. Code review continuo (Fase 12).
12. Preparar para entrevistas (Fase 13-14).

## Decisiones importantes ya tomadas

- No se introducirán microservicios sin una razón sólida.
- Se priorizará un monólito modular bien diseñado.
- Se usará Kafka solo si hay un caso de uso real (eventos asíncronos).
- El aprendizaje es el objetivo principal; la velocidad es secundaria.
- El modelo de dominio es to-be y en español (lenguaje ubicuo); la Fase 2 refactoriza hacia él
  de forma incremental, empezando por el agregado Pedido.
- Los bounded contexts (Catálogo, Ventas, Operaciones) serán los módulos del monolito modular.

## Plan detallado

Para conocer el plan completo de aprendizaje y el estado de cada fase, consulta el archivo:
➡️ **[docs/roadmap.md](../docs/roadmap.md)**

## Enlaces

- Backend: https://github.com/Eliasusu/verdedemas
- Frontend web: https://github.com/Eliasusu/verdedemas-astro
- App móvil: https://github.com/Eliasusu/verdedemas-app