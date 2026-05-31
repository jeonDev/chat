# Reviewer Reference

Use this when reviewing a diff or finishing production code changes.

## Review Priority

1. Bugs or regressions.
2. Architecture boundary drift.
3. REST design issues.
4. Missing or weak tests.
5. Naming and readability.

## Architecture Checks

- Controllers stay thin and delegate to use cases.
- Application services own orchestration and transactions.
- Domain logic is not hidden in endpoints, DTO mapping, JPA adapters, or socket handlers.
- Domain does not depend on web, Netty, JPA, or external system APIs.
- Persistence/framework-specific repositories stay behind an appropriate boundary when the current code allows it.

## API Checks

- Endpoints use resource names and `/api/v1`.
- HTTP methods and status codes match the operation.
- Request/response DTOs do not leak JPA entities or internal persistence details.

## JPA Checks

- Derived query methods use camel nested property names.
- No underscore traversal such as `existsByRequester_IdAndFriend_Id`.

## Validation Checks

- Focused tests cover behavior changes where practical.
- `./gradlew test` has run for production behavior changes, or the final response explains why it was skipped.
- Runtime-only requirements are called out when tests cannot cover them.
