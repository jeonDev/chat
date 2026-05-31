---
name: clean-architecture-restful
description: Apply this project's Clean Architecture and REST conventions for Java/Spring production code. Use when adding or changing domain, application, endpoint, infra, HTTP APIs, DTOs, JPA repositories, persistence adapters, Netty/socket handlers, or reviewing architecture and REST drift.
compatibility: Designed for this netty-chat Spring Boot project and Agent Skills-compatible clients that scan .agents/skills.
metadata:
  project: netty-chat
  short-description: Clean Architecture and REST rules
---

# Clean Architecture + REST

Use this skill for production code, package structure, use cases, controllers, DTOs, persistence, Netty/socket adapters, and HTTP APIs.

## Role References

- Planner: read `references/planner.md` when defining scope, package boundaries, API shape, validation, and risks.
- Developer: read `references/developer.md` before implementation.
- Reviewer: read `references/reviewer.md` before reviewing a diff or finishing code changes.

If the user asks for the full planner -> developer -> reviewer flow, load each reference only when that role starts.
