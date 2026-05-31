# Agent Workflow

Trigger this flow for code change requests, including requests like:

- "planner developer reviewer"

Run roles in order:

1. Branch Setup
2. Planner
3. Developer
4. Reviewer
5. Publisher

Roles are working modes, not separate processes, unless the user asks for real sub-agents.

## Branch Setup

Before planning or editing, prepare the development branch.

- Inspect `git status -sb` and the current branch.
- Switch to local `main`, then create a new `codex/<description>` branch derived from the requested development scope.
- Always branch from local `main`, even when the current branch is another feature branch.
- Do not switch branches when uncommitted changes make the requested development scope ambiguous. Report the existing changes and ask for the scope first.

## Planner

Read `.agents/rules/planner.md`.

Output a short planning report before implementation: goal, scope, likely files, implementation path, validation, and risks. If low risk, continue without confirmation.

## Developer

Read `.agents/rules/developer.md` and relevant skill folders listed in `SKILLS.md`. Implement the smallest useful change using existing conventions.

## Reviewer

Read `.agents/rules/reviewer.md`. Inspect the diff, fix clear local issues, then do one final pass.

## Publisher

After review and validation, publish only the reviewed development scope as a draft GitHub PR.

- Inspect `git status -sb` and the diff before staging.
- Do not include unrelated working tree changes.
- Commit and push the reviewed files, then open a draft PR.
- If GitHub CLI installation, authentication, or repository access is missing, report the exact command or user action required instead of running setup or registration commands.

## Commands

For setup/install/registration/activation, tell the user the command instead of running it. Normal inspection and validation commands are allowed.
