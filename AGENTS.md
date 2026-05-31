# Agent Index

Read before repo work:

- `.agents/rules/agent-workflow.md`
- `SKILLS.md`

For code changes, also read the relevant `.agents/skills/<skill-name>/SKILL.md` file listed in `SKILLS.md`.

## Flow

For code change requests, run these roles in order in the same turn:

1. Branch Setup: before planning or editing, switch to local `main` and create a new `codex/<description>` branch from `main`.
2. Planner: inspect code, define scope, plan validation, and report the plan before implementation.
3. Developer: implement with existing patterns and project skills.
4. Reviewer: inspect the diff for bugs, architecture drift, REST issues, and missing tests.
5. Publisher: after review and validation, publish the reviewed development scope as a draft GitHub PR.

Do not skip Reviewer after code changes unless the user asks.
Always create a new development branch from local `main`, even when the current branch is another feature branch. Do not switch branches when uncommitted changes make the requested development scope ambiguous. Report the existing changes and ask for the scope first.
Do not publish unrelated working tree changes. If GitHub CLI installation, authentication, or repository access is missing, report the exact command or user action required instead of running setup or registration commands automatically.
After review and validation, always create a local commit for the reviewed development scope before attempting to push or open a PR. If push or PR creation is blocked, keep the local commit and report the blocker.
Immediately push the new commit to the current remote development branch without asking for separate confirmation. If the branch has no upstream, push with upstream tracking.
Use the repository commit message format `<type> : <description>`, such as `feat : ...`, `fix : ...`, or `chore : ...`. Choose the type that matches the reviewed scope.

## Role Files

- `.agents/rules/planner.md`
- `.agents/rules/developer.md`
- `.agents/rules/reviewer.md`

## Commands

Do not run setup, install, registration, or activation commands automatically. Tell the user the exact command and reason.

Normal inspection and validation commands are allowed unless the user says otherwise.

## Priority

User request > `AGENTS.md` > `SKILLS.md` > `.agents/**` > existing code.
