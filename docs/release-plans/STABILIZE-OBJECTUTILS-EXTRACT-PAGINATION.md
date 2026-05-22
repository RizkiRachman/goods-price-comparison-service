# Release Plan: Stabilize ObjectUtils + Extract Pagination Resolution

**Date:** 2026-05-22
**PR:** [#90](https://github.com/RizkiRachman/goods-price-comparison-service/pull/90)
**Branch:** `ANEH-20260522-STABILIZE-OBJECTUTILS-EXTRACT-PAGINATION`

---

## Summary

Seal `ObjectUtils` as a stable never-change utility and extract the repeated pagination-parameter null-coalescing pattern into `PaginationUtils`. Reduces coupling and centralizes pagination defaults.

## Impact Analysis

| Scope | Impact |
|-------|--------|
| **Database schema** | None |
| **API contract** | None — all method signatures unchanged |
| **Event format** | None |
| **Compile errors** | None — additive refactor (new methods, no removals) |
| **Runtime behavior** | ✅ Identical — all pagination defaults preserved (page: 1 for 4 adapters, 0 for Store; size: 20) |
| **Rollback** | Revert PR #90 |

## Files Changed

| File | Change Type | Risk |
|------|-------------|------|
| `.gitignore` | Add entries | None |
| `ObjectUtils.java` | Javadoc only | None |
| `PaginationUtils.java` | +4 methods | Low — pure new code |
| `ActivityLogWebAdapter.java` | Replace calls | Low — same logic |
| `FeedbackQuestionWebAdapter.java` | Replace calls | Low — same logic |
| `UnitWebAdapter.java` | Replace calls | Low — same logic |
| `CategoryWebAdapter.java` | Replace calls | Low — same logic |
| `StoreWebAdapter.java` | Replace calls | Low — same logic |
| `HexagonalArchitectureTest.java` | +1 ArchUnit rule | None — test only |

## Quality Gates

| Gate | Status |
|------|--------|
| `mvn spotless:apply` | ✅ Passed |
| `mvn test` (ArchUnit + unit) | ✅ 138 tests, 0 failures |
| `mvn verify` (SpotBugs + PMD CPD) | ✅ Clean |
| `./scripts/check-conventions.sh` | ✅ All 7 patterns passed |
| **New ArchUnit rule**: `objectUtilsMustNotBeExtendedWithNewMethods` | ✅ Enforced |

## Pre-merge Checklist

- [x] All quality gates pass
- [x] No TODOs/FIXMEs/commented-out code
- [x] No secrets in diff
- [x] .gitignore updated for graphify-out/ and .gitnexus/

## Post-merge

- Tag: `git tag v1.8.0 && git push origin v1.8.0`
- Update CHANGELOG.md (unreleased section)
