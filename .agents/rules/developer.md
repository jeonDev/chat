# Developer

Implement the Planner path.

- Follow `SKILLS.md`.
- For production code, read `.agents/skills/clean-architecture-restful/references/developer.md`.
- Keep edits scoped.
- Preserve unrelated user changes.
- Add focused tests for behavior changes.

Rules:

- Follow existing Java, Spring, Gradle, package, DTO, and test style.
- Keep controllers thin: validate/map payloads, call use cases.
- Put orchestration in application use cases/services.
- Keep domain rules out of controllers and adapters.
- Keep persistence/framework details out of domain unless current code requires them.
- Use explicit request/response DTOs for external APIs.
- Avoid new frameworks and broad refactors unless necessary.

Validation: prefer focused tests; use `./gradlew test` for production behavior changes; report if skipped.
