# Agent Index

Read before repo work:

- `.agents/rules/agent-workflow.md`
- `SKILLS.md`

For code changes, also read the relevant skill file from `SKILLS.md`.

## Flow

When the user asks for the agent flow, run these roles in order in the same turn:

1. Planner: inspect code, define scope, plan validation.
2. Developer: implement with existing patterns and project skills.
3. Reviewer: inspect the diff for bugs, architecture drift, REST issues, and missing tests.

Do not skip Reviewer after code changes unless the user asks.

## Role Files

- `.agents/rules/planner.md`
- `.agents/rules/developer.md`
- `.agents/rules/reviewer.md`

## Commands

Do not run setup, install, registration, or activation commands automatically. Tell the user the exact command and reason.

Normal inspection and validation commands are allowed unless the user says otherwise.

## Priority

User request > `AGENTS.md` > `SKILLS.md` > `.agents/**` > existing code.
