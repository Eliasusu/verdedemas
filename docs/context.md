# Contexto de VerdeDeMas

**Última actualización:** 2026-08-22 (inicio del proyecto de aprendizaje)

## Propósito

VerdeDeMas es un proyecto de ejemplo para aprender y demostrar habilidades de Backend Engineering. El objetivo final es tener un sistema con dominio modelado, arquitectura limpia, tests, eventos y documentación, que sirva como portfolio profesional.

## Estado actual

- Repositorio clonado desde `https://github.com/Eliasusu/verdedemas`.
- El código actual es un **MVP funcional** con Spring Boot, sin una arquitectura definida ni pruebas significativas.
- No se ha realizado ninguna refactorización todavía; estamos en la **Fase 0: Auditoría**.

## Dominio (conocido hasta ahora)

- **Entidad principal**: probablemente relacionada con plantas/verduras (el nombre "VerdeDeMas" sugiere un negocio de verdulería o similar).
- **Casos de uso identificados** (a confirmar): gestión de productos, pedidos, usuarios, etc.
- **Modelo implícito**: se extraerá durante la auditoría.

## Arquitectura actual

- Capas típicas de Spring Boot: Controller → Service → Repository (JPA).
- No hay separación clara entre dominio, aplicación e infraestructura.
- Lógica de negocio dispersa en servicios y controladores.

## Próximos pasos

1. Realizar la auditoría completa del código (Fase 0).
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