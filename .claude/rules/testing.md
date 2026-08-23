# Testing Rules – VerdeDeMas

## Filosofía

- Los tests son parte fundamental del diseño, no una comprobación posterior.
- Se prioriza **TDD** (Red-Green-Refactor) para el desarrollo de nuevo comportamiento.
- Se aplica **BDD** para los casos de uso principales (Given-When-Then) cuando tenga sentido.

## Tipos de tests

- **Tests unitarios**: Para el dominio (entidades, value objects, servicios de dominio). Aislados de infraestructura.
- **Tests de integración**: Para la capa de aplicación e infraestructura (repositorios con base de datos en memoria, clientes HTTP mockeados).
- **Tests de aceptación**: Para los endpoints REST (usando `@WebMvcTest` o `TestRestTemplate`).
- **Tests end-to-end**: Solo para flujos críticos, con entorno completo (pueden ser lentos, se ejecutan en CI).

## Reglas

- **No usar mocks a menos que sea necesario** para aislar una unidad. Preferir stubs o fakes.
- **Testear comportamiento, no implementación**. Los tests deben pasar si el comportamiento es correcto, aunque se refactorice internamente.
- **Cada test debe ser independiente** y no depender del orden de ejecución.
- **Usar nombres descriptivos**: `should...when...` o `given...when...then...`.
- **Cobertura**: No obsesionarse con el 100%, pero asegurar que todas las reglas de negocio críticas estén cubiertas.

## Herramientas

- JUnit 5, AssertJ, Mockito (para mocks), Testcontainers (para integración con DB real).
- Para BDD, se puede usar Cucumber o simplemente escribir escenarios en Gherkin en los tests.

## Proceso TDD sugerido

1. Escribir un test que falle (RED) para un nuevo comportamiento.
2. Implementar lo mínimo para que pase (GREEN).
3. Refactorizar manteniendo los tests verdes (REFACTOR).
4. Repetir.

La IA puede ayudar a generar los tests iniciales, pero el usuario debe entender el escenario y validar el comportamiento esperado.