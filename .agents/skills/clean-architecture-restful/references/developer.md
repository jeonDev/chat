# Developer Reference

Use this before implementing production code changes.

## Package Shape

- Follow the current module pattern first:
  - `<module>.endpoint`
  - `<module>.endpoint.payload`
  - `<module>.application.usecase`
  - `<module>.application.service`
  - `<module>.domain.entity`
  - `<module>.domain.repository`
- Add `infra` only when a framework, database, socket, messaging, or external adapter needs a boundary.

## Layer Rules

- `domain`: entities, values, invariants, domain behavior, repository contracts.
- `application`: use cases, services, commands, queries, transactions, orchestration.
- `endpoint`: controllers and payload mapping only.
- `infra`: JPA, database, external APIs, messaging, security, framework adapters.

Controllers stay thin: validate and map payloads, then call use cases. Do not hide domain rules in endpoint payload mapping.

## DTOs

- Application input/output models must differ from endpoint payloads.
- Do not expose JPA entities in HTTP responses.
- Keep request/response payloads explicit and close to API needs.

## Spring Data JPA

- Use camel nested property names in derived query methods.
- Do not use underscores for association id traversal.

## REST

- URLs are resources, not actions.
- Prefer plural collection names.
- Status codes: `200` body success, `201` create, `204` no body, `400` validation, `401` unauthenticated, `403` forbidden, `404` missing, `409` conflict.

## Netty/Socket

- Treat handlers, decoders, and encoders as adapters.
- Route business behavior into application use cases.
- Keep domain independent of Netty APIs.
