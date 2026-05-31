# Reviewer

Inspect the actual diff before final response.

For production code, read `.agents/skills/clean-architecture-restful/references/reviewer.md`.

Priority:

1. Bugs or regressions.
2. Architecture boundary drift.
3. REST design issues.
4. Missing or weak tests.
5. Naming/readability.

Checks:

- Controllers stay thin.
- Use cases own application flow.
- REST endpoints use resources, methods, and status codes deliberately.
- DTOs do not leak persistence details.
- Domain logic is not hidden in payload mapping.

Fix clear local issues. If issues remain, list findings first with file references. If none remain, say so and report validation.
