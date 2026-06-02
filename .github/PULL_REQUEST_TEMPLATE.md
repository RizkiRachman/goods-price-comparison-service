## Branching

<!-- Branch naming: feature/YYYYMMDD-<short-description> or bugfix/YYYYMMDD-<short-description> -->

## Title

<!-- Compelling, under 100 characters. Format: type(scope): brief description -->

## Summary

<!-- What changed and why. Lead with the most critical information (who, what, when, where, why). -->

## Related Issues

- Closes #
- Relates to #

## Type of Change

- [ ] feat
- [ ] fix
- [ ] refactor
- [ ] test
- [ ] docs
- [ ] chore
- [ ] perf
- [ ] style

## Quality Checklist

- [ ] `mvn spotless:apply` — formatting is clean (Google Java Style)
- [ ] `mvn clean test` — all tests pass, ArchUnit (7 rules)
- [ ] `mvn clean verify` — full quality gates pass (SpotBugs, PMD CPD)
- [ ] `mvn verify -P security-check` — OWASP Dependency-Check passes (no CVSS >= 7)
- [ ] `./scripts/check-conventions.sh` — convention checks pass
- [ ] New code has unit tests (100% coverage for new code)
- [ ] CHANGELOG.md updated under `[Unreleased]`
- [ ] Documentation updated if user-facing change (README.md, docs/)
- [ ] No TODOs, FIXMEs, or commented-out code
- [ ] No secrets or credentials committed

## How to Test

<!-- Steps for reviewer -->
