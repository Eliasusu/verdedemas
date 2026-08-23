# Architecture Rules – VerdeDeMas

## Principios fundamentales

- **Dependencias apuntan hacia adentro**: El dominio no debe depender de infraestructura.
- **Capa de aplicación** orquesta los casos de uso, pero no contiene lógica de negocio.
- **Infraestructura** implementa puertos (repositorios, servicios externos, mensajería).
- **Adaptadores** conectan la aplicación con el mundo exterior (controladores REST, event listeners, etc.).

## Estilo arquitectónico actual

- Inicialmente: **Monolito modular** (Modular Monolith) con separación clara de módulos por contexto acotado.
- No se introducen microservicios a menos que exista una razón de negocio o técnica que lo justifique (escalabilidad, equipos independientes, etc.).
- Se evaluará la evolución hacia una arquitectura hexagonal/clean a medida que el dominio se consolide.

## Decisiones de diseño

- **Aggregates**: Son el núcleo de la consistencia transaccional. Cada agregado tiene una raíz que protege las invariantes.
- **Value Objects**: Se utilizan para modelar conceptos inmutables y con identidad basada en sus atributos.
- **Repositorios**: Solo para agregados; se inyectan en los casos de uso mediante puertos.
- **Servicios de dominio**: Contienen lógica de negocio que no pertenece naturalmente a una entidad o valor.
- **Eventos de dominio**: Se disparan cuando ocurre un cambio significativo dentro del agregado.

## Capas (progresivas)

1. **Domain**: Entidades, value objects, aggregates, servicios de dominio, eventos de dominio, repositorios (interfaces).
2. **Application**: Casos de uso (servicios de aplicación), DTOs de entrada/salida, puertos para infraestructura.
3. **Infrastructure**: Implementaciones concretas de repositorios, persistencia (JPA), mensajería (Kafka), clientes HTTP, etc.
4. **Adapters**: Controladores REST, manejadores de eventos, configuración de seguridad, etc.

## Reglas de dependencia

- Domain → no depende de nada.
- Application → depende de Domain.
- Infrastructure → depende de Application y Domain.
- Adapters → depende de Application (y a veces de Infrastructure, pero mediando puertos).

## Gestión de errores

- Usar excepciones de dominio para violaciones de invariantes.
- En la capa de aplicación, mapear errores de dominio a respuestas HTTP apropiadas (400, 404, 409, etc.).
- No propagar excepciones técnicas (SQL, IO) a la capa superior sin envolverlas adecuadamente.

## API y contratos

- Diseño **API First**: primero definir el contrato (OpenAPI) y luego implementar.
- Versionado semántico de API (v1, v2, etc.) y mantener compatibilidad hacia atrás.
- Usar DTOs específicos para cada caso de uso; no exponer entidades de dominio directamente.

## Eventos y mensajería

- Los eventos de dominio son para comunicación dentro del mismo módulo (o entre módulos) de forma asíncrona.
- Los eventos de integración se usan para comunicarse con otros sistemas (si existe necesidad).
- No introducir Kafka sin un caso de uso claro que lo requiera (ej. notificaciones, procesamiento en background, desacoplamiento).