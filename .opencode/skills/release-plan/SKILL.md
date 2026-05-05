---
name: release-plan
description: End-to-end release process — unit testing, quality gates (PMD, SpotBugs, Spotless, ArchUnit), PR templates, commit conventions, documentation, and token optimization
license: MIT
compatibility: opencode
---

# Release Plan — Goods Price Comparison Service

End-to-end workflow for shipping a clean, verified release. Every phase must complete before the next begins.

---

## Phase 0: Token Optimization

Before starting, load the token optimization skill to manage context efficiently:

```
/skill token-optimize
```

**Key practices during release:**
- Search before reading: use `grep`/`glob` to locate files, then `read` with `limit`/`offset`
- Batch parallel reads for independent files (e.g., all port interfaces at once)
- Reuse `task_id` for related subtasks within a release
- Keep summaries concise — avoid verbose explanations

---

## Phase 1: Planning

### Entry Criteria
- [ ] Feature request or bug report is clear and scoped
- [ ] Acceptance criteria defined and testable
- [ ] Impact analysis done (what services/contracts change)
- [ ] Architecture alignment checked against `docs/ARCHITECTURE_HYBRID.md`

### Deliverables
- Brief implementation plan (2-5 bullet points)
- List of files to change (generated via `grep`/`glob`)

### Artifact
```
planning/FEATURE_NAME_MASTER_PLAN.md
```

---

## Phase 2: Development

### Order of Operations (per AGENTS.md)
1. **Port interface** — `*InPort`, `*RepositoryPort`, `*EventOutPort`
2. **Domain service** — implements `*InPort`, pure Java
3. **Mapper** — `*DtoMapper` (domain ↔ API), `*Mapper` (domain ↔ entity)
4. **Adapter** — web, persistence, event orchestration
5. **Constants** — extract magic strings to `*Constants` or enums
6. **Events** — domain service publishes via `*EventOutPort`, handler uses `@Async @TransactionalEventListener(AFTER_COMMIT)`
7. **Tests** — unit tests for domain service, adapter tests for infrastructure

### Development Conventions
- ✅ Use `ObjectUtils.getOrNull()`, `ObjectUtils.getOrDefault()` for null safety
- ✅ Return `null` from ports (never `Optional<T>`)
- ✅ Domain models: `@Builder @Getter @Setter` — zero JPA annotations
- ✅ FK columns are primitives: `Long storeId`, `UUID receiptId` — no `@ManyToOne`
- ✅ `String.formatted()` over `String.format()`
- ✅ `.toList()` over `.collect(Collectors.toList())`
- ✅ Method references over lambdas where possible
- ✅ Avoid redundant variable assignments
- ✅ No unused code (YAGNI)

---

## Phase 3: Quality Gates

Run in this order. **Fix all issues in one gate before moving to the next.**

### Gate 1: Formatting

```bash
mvn spotless:apply
```

- Enforces Google Java Style
- Run this first so all other gates see formatted code
- Check: zero files changed after `spotless:apply` (idempotent)

### Gate 2: Architecture

```bash
mvn test
```

This runs:
| Test | Enforces |
|------|----------|
| ArchUnit (7 rules) | Hexagonal layers, no Optional in ports, no JPA in domain |
| Unit tests | Business logic correctness |

**ArchUnit rules** (all must pass):
1. `domainMustNotDependOnInfrastructure` — `application/` never imports `infrastructure/`
2. `domainModelsMustNotHaveJpaAnnotations` — no `@Entity`, `@Id`, `@Column` in domain POJOs
3. `portsMustNotReturnOptional` — port methods return nullable, never `Optional<T>`
4. `entitiesMustNotUseJpaRelationshipAnnotations` — no `@ManyToOne`, `@OneToMany`, etc.
5. `layeredArchitectureShouldRespectHexagonalBoundaries` — reverse dependency direction
6. `domainServicesMustBeAnnotatedWithService` — all domain services have `@Service`
7. `repositoryAdaptersMustBeAnnotatedWithComponent` — all repository adapters have `@Component`

### Gate 3: Static Analysis

```bash
mvn verify
```

Runs all previous gates plus:

| Tool | Fails On |
|------|----------|
| **SpotBugs** | Null safety violations, encoding issues, exposed internals (`failOnError: true`) |
| **PMD CPD** | Duplicate code >100 tokens |

**SpotBugs suppressions**: `config/spotbugs/exclude.xml`

### Gate 4: Convention Checks

```bash
./scripts/check-conventions.sh
```

Checks for:
- Verbose ternary null-check → should use `ObjectUtils.getOrNull`
- Verbose lambda → should use method reference
- JPA annotations in domain models
- `Optional<T>` in port interfaces
- `.collect(Collectors.toList())` → should use `.toList()`
- `String.format()` → should use `String.formatted()`
- `@Data` in domain models → should use `@Getter @Setter @Builder`

### Gate 5: Tests

```bash
mvn test                    # Unit + ArchUnit
mvn verify                  # Full suite (Gate 3 covers this, run separately for speed)
```

All 94+ tests must pass with 0 failures.

---

## Phase 4: Documentation Updates

### Required Updates

| File | When to Update |
|------|----------------|
| `README.md` | New features, API changes, config changes |
| `CHANGELOG.md` | **Every release** — add entry under `[Unreleased]` |
| `docs/DEVELOPER_GUIDE.md` | Workflow changes, new conventions |
| `docs/ARCHITECTURE_HYBRID.md` | Architecture changes |
| `docs/ERD.md` | Schema changes |
| `docs/USER_GUIDE.md` | API changes visible to users |
| `CONTRIBUTING.md` | Process changes |
| `AGENTS.md` | Build/verify commands changes |
| `config/` property files | New configuration keys |

### Changelog Format (Keep a Changelog)

```markdown
## [Unreleased]

### Added
- New feature X with Y capability

### Changed
- Refactored Z to improve performance

### Fixed
- Bug where A caused B under C conditions

### Removed
- Deprecated endpoint /api/v1/old

### Security
- Fixed vulnerability in dependency D
```

### Commit Message Format (used in Phase 5 body)

```
type(scope): brief description

Optional body with details. Wrap at 72 chars.
- Bullet points for list items
- Reference issues: Fixes #123
```

**Types**: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `perf`, `style`

---

## Phase 5: Pull Request

### PR Template

Create PR using this structure:

```markdown
## Summary

<1-3 bullet points describing what changed and why>

## Related Issues

- Closes #<issue-number>
- Relates to #<issue-number>

## Type of Change

- [ ] feat: new feature
- [ ] fix: bug fix
- [ ] refactor: code restructuring
- [ ] test: test additions/changes
- [ ] docs: documentation
- [ ] chore: build/tooling

## Quality Checklist

- [ ] `mvn spotless:apply` — formatting is clean
- [ ] `mvn test` — all tests pass (0 failures)
- [ ] `mvn verify` — full quality gates pass (SpotBugs, PMD CPD)
- [ ] `./scripts/check-conventions.sh` — convention checks pass
- [ ] New code has unit tests (100% coverage for new code)
- [ ] CHANGELOG.md updated under `[Unreleased]`
- [ ] Documentation updated (README, docs/, AGENTS.md if applicable)
- [ ] No unused code, parameters, or variables (YAGNI)
- [ ] No redundant variable assignments

## How to Test

1. Step-by-step instructions
2. To verify this change
3. Expected outcomes
```

### PR Creation Command

```bash
gh pr create \
  --title "feat(scope): concise description" \
  --body "$(cat <<'EOF'
<PR body from template above>
EOF
)"
```

---

## Phase 6: Release Review

### Pre-merge Checklist

- [ ] All quality gates pass on CI
- [ ] At least one reviewer has approved
- [ ] No TODOs or FIXMEs in new code
- [ ] No commented-out code
- [ ] Logging added for important operations (info/warn/error levels appropriately)
- [ ] Error messages use `ErrorMessageConstants`
- [ ] Error codes use `ErrorCodes` constants
- [ ] No secrets or credentials committed
- [ ] Branch is up to date with target branch

### Post-merge

- [ ] Tag the release: `git tag v<version>`
- [ ] Push tag: `git push origin v<version>`
- [ ] Create GitHub Release with changelog entry
- [ ] Deploy to staging/production as applicable

---

## Quick Reference Card

```
1. PLAN    → planning/MASTER_PLAN.md
2. DEV     → port → service → mapper → adapter → constants → events → tests
3. QUALITY → spotless:apply → test → verify → check-conventions.sh
4. DOCS    → CHANGELOG → README → docs/ → AGENTS.md
5. PR      → gh pr create --title "feat(scope): msg" --body "template"
6. REVIEW  → approve → merge → tag → release
```

## See Also

- `/skill token-optimize` — Context management for efficient development
- `/skill java-developer` — Full Java/Spring conventions and anti-patterns
- `/skill qa-expert` — Test strategy and coverage analysis
- `AGENTS.md` — Project-specific agent instructions
- `docs/ARCHITECTURE_HYBRID.md` — Architecture reference
