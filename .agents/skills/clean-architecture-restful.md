# Clean Architecture + REST

Use for production code, package structure, use cases, controllers, DTOs, persistence, and HTTP APIs.

## Dependencies

- `endpoint` -> `application`
- `application` -> `domain`
- `infra` -> `application`/`domain`
- `domain` must not depend on endpoint, infra, web, persistence adapters, or external systems.

Default packages:

- `com.jh.chat.<module>.domain`
- `com.jh.chat.<module>.application`
- `com.jh.chat.<module>.endpoint`
- Add `infra` only for persistence/external/messaging/framework adapters.

## Layers

- `domain`: entities, values, invariants, domain behavior, repository contracts.
- `application`: use cases, services, commands, queries, transactions, orchestration.
- `endpoint`: controllers and payload mapping only.
- `infra`: JPA, database, external APIs, messaging, security, framework adapters.

Application input/output models must differ from endpoint payloads. Do not expose JPA entities in HTTP responses.

## Spring Data JPA

- Use camel nested property names in derived query methods.
- Do not use underscores for association id traversal.

## REST

- URLs are resources, not actions.
- Prefer plural collection names.
- Methods: `GET` read, `POST` create/non-idempotent command, `PUT` replace, `PATCH` partial update, `DELETE` remove/leave.
- Status: `200` body success, `201` create, `204` no body, `400` validation, `401` unauthenticated, `403` forbidden, `404` missing, `409` conflict.
- Keep `/api/v1`.

## Project Shape

Current module pattern:

- `<module>.endpoint`
- `<module>.endpoint.payload`
- `<module>.application.usecase`
- `<module>.application.service`
- `<module>.domain.entity`
- `<module>.domain.repository`

Extend this pattern first. If a repository is framework-specific, prefer an interface at the boundary and an `infra` implementation.

## Netty/Socket

Do not force REST semantics onto socket protocols. Treat handlers/decoders as adapters, route behavior into application use cases, and keep domain independent of Netty APIs.
