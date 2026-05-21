# Agent Workflow

Trigger this flow for requests like:

- "planner developer reviewer"

Run roles in order:

1. Planner
2. Developer
3. Reviewer

Roles are working modes, not separate processes, unless the user asks for real sub-agents.

## Planner

Read `.agents/rules/planner.md`.

Output: goal, scope, likely files, validation, risks. Keep it short. If low risk, continue without confirmation.

## Developer

Read `.agents/rules/developer.md` and relevant `SKILLS.md` files. Implement the smallest useful change using existing conventions.

## Reviewer

Read `.agents/rules/reviewer.md`. Inspect the diff, fix clear local issues, then do one final pass.

## Commands

For setup/install/registration/activation, tell the user the command instead of running it. Normal inspection and validation commands are allowed.
