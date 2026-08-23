# Contexto de VerdeDeMas

**Última actualización:** 2026-08-23 (Fase 0 completada)

## Propósito

VerdeDeMas es un proyecto de ejemplo para aprender y demostrar habilidades de Backend Engineering. El objetivo final es tener un sistema con dominio modelado, arquitectura limpia, tests, eventos y documentación, que sirva como portfolio profesional.

## Estado actual

- Repositorio clonado desde `https://github.com/Eliasusu/verdedemas`.
- El código actual es un **MVP funcional** con Spring Boot, sin una arquitectura definida ni pruebas significativas (0% de cobertura más allá de un test de carga de contexto).
- **Fase 0 (Auditoría) completada.** El diagnóstico completo está en `docs/phases/fase-0-auditoria-del-proyecto.md`. La documentación de negocio/arquitectura/API fue migrada a `docs/` (ver sección "Documentación" abajo) y `.github/` (Copilot, docs legacy) fue eliminado por completo — la asistencia de IA quedó consolidada en Claude Code.
- Próximo paso: Fase 1 (Entender el Dominio).

## Dominio (conocido hasta ahora)

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
- `docs/phases/fase-0-auditoria-del-proyecto.md` — diagnóstico completo de la Fase 0.

## Próximos pasos

1. ~~Realizar la auditoría completa del código (Fase 0).~~ ✅ Completado — ver
   `docs/phases/fase-0-auditoria-del-proyecto.md`.
2. Entender el dominio y modelarlo (Fase 1).
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

## Plan detallado

Para conocer el plan completo de aprendizaje y el estado de cada fase, consulta el archivo:
➡️ **[docs/roadmap.md](../docs/roadmap.md)**

## Enlaces

- Backend: https://github.com/Eliasusu/verdedemas
- Frontend web: https://github.com/Eliasusu/verdedemas-astro
- App móvil: https://github.com/Eliasusu/verdedemas-app