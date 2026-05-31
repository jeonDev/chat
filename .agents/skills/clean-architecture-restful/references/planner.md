# Planner Reference

Use this when planning production code changes.

## Scope

- Inspect existing packages and tests before choosing a structure.
- Prefer the smallest useful change that preserves existing conventions.
- Name likely packages, files, API resources, persistence changes, and tests.
- Ask only when a reasonable default would be risky.

## Boundaries

- Default packages:
  - `com.jh.chat.<module>.domain`
  - `com.jh.chat.<module>.application`
  - `com.jh.chat.<module>.endpoint`
  - `com.jh.chat.<module>.infra` only for persistence, external systems, messaging, or framework adapters.
- Dependencies:
  - `endpoint` -> `application`
  - `application` -> `domain`
  - `infra` -> `application`/`domain`
  - `domain` must not depend on endpoint, infra, web, persistence adapters, or external systems.

## API Planning

- Keep HTTP APIs under `/api/v1`.
- Use resource-oriented URLs and plural collection names.
- Match method semantics: `GET` read, `POST` create/non-idempotent command, `PUT` replace, `PATCH` partial update, `DELETE` remove/leave.
- For Netty/socket work, keep protocol semantics and route behavior into application use cases.

## Validation Plan

- Prefer focused tests for changed behavior.
- Use `./gradlew test` for production behavior changes unless the user says otherwise.
- Call out runtime requirements such as PostgreSQL, environment variables, or external services.
