---
name: pr
description: Create a GitHub pull request for the current branch using this repository's PR template. Use when the user explicitly invokes /pr or asks to create a PR, open a pull request, draft a PR, or prepare a PR body for this repo.
disable-model-invocation: true
---

# PR

## Overview

Create a GitHub pull request for the current branch with this repository's `.github/PULL_REQUEST_TEMPLATE.md`.
Use the same workflow for Codex `$pr` and Claude `/pr` users.

## Required First Response

When this skill is invoked, first ask for both required PR choices and wait for the user's answer:

- "PR target/base 브랜치는 어디로 할까요?"
- "Ready PR로 만들까요, Draft PR로 만들까요?"

Do not run `git push`, `gh pr create`, or any other PR creation command before the user answers both questions.
Do not infer a default base branch, even if the repository commonly uses one.
If the invocation already includes a base branch or PR state, restate those values as tentative and ask the user to confirm or correct them before continuing.

## Workflow

After the user provides the target/base branch and Ready/Draft choice:

1. Read `.github/PULL_REQUEST_TEMPLATE.md`.
   - Preserve the template sections: `타입`, `작업 사항`, `변경 내용`, `관련 이슈`.
   - Do not edit the template file.
2. Inspect the repository state.
   - Confirm the current branch with `git branch --show-current`.
   - Review `git status --short`.
   - Review recent commits with `git log --oneline -10`.
   - Review the committed change shape against the chosen base branch with `git diff --stat <base>...HEAD` and `git diff --name-only <base>...HEAD`.
3. If there are uncommitted changes, stop and tell the user:
   - "커밋된 내용만 PR에 포함됩니다. 현재 uncommitted change는 PR에 포함되지 않습니다. 계속할까요?"
   - Continue only after explicit confirmation.
   - Do not stage, commit, or discard those changes as part of this skill.
4. Check for an existing open PR for the current branch.
   - Use `gh pr list --head <current-branch> --state open --json url,title,baseRefName --limit 1`.
   - If an open PR exists, report its URL and stop. Do not create a duplicate PR.
5. If there are no committed changes compared with the chosen base branch, stop and explain that there is nothing to open a PR for.
6. Create a PR title and body in Korean using the rules below.
7. Push the current branch with `git push -u origin HEAD`.
8. Create the PR:
   - Ready PR: `gh pr create --base <base> --head <current-branch> --title "<title>" --body-file <body-file>`
   - Draft PR: add `--draft`.
   - Use a temporary body file outside the repository, such as under `/tmp`, if a body file is needed.
   - Report the created PR URL.

## PR Content Rules

Use `.github/PULL_REQUEST_TEMPLATE.md` as the source of truth for the body structure.

- Title format: `<TYPE>: 설명 <IssueNumber>`.
- Use one type only: `Feat`, `Fix`, `Refactor`, `Style`, `Rename`, `Remove`, `Comment`, `Design`, `Docs`, or `Core`.
- Check exactly one item in `## **타입**`.
- In `## **작업 사항**`, summarize the actual work as bullets.
- In `## **변경 내용**`, describe the important behavior or logic changes.
- In `## **관련 이슈**`, include only confirmed issue numbers from the user's request, branch name, commits, or existing context.
- Do not invent issue numbers. If no issue is confirmed, write `Resolves: 없음` and `See also: 없음`.
- Keep the PR body concise and review-friendly.

Example body shape:

```markdown
## **타입**
- [x] Feat
- [ ] Fix
- [ ] Refactor
- [ ] Style
- [ ] Rename
- [ ] Remove
- [ ] Comment
- [ ] Design
- [ ] Docs
- [ ] Core

## **작업 사항**
- 작업 요약

## **변경 내용**
- 변경된 동작이나 주요 로직

## **관련 이슈**
Resolves: 없음

See also: 없음
```
