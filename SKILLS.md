# Skill Index

For production code changes, read:

- `.agents/skills/clean-architecture-restful.md`

Default package boundaries:

- `domain`: entities, rules, repository contracts.
- `application`: use cases, services, commands, queries.
- `endpoint`: HTTP controllers and payload mapping.
- `infra`: persistence, external APIs, messaging, framework adapters.

Prefer the existing `domain/application/endpoint` layout before adding new structure.

Use REST for HTTP APIs. For Netty/socket work, keep protocol semantics but preserve the same boundaries.

Spring Data JPA derived query methods should use camel nested property names without underscores.
Example: use `existsByRequesterIdAndFriendId`, not `existsByRequester_IdAndFriend_Id`.
