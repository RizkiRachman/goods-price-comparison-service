# Release Plan — Service Code Quality Remediation

**Date**: 2026-06-04
**Source Analysis**: `docs/quality-analysis-2026-06-04.md`
**Branch**: `feature/20260604-quality-analysis-report`
**PR**: #114

---

## Priority Tiers

### Tier 1 — Immediate (High ROI, Low Risk)

Estimated effort: ~30 minutes. No new files needed — just use existing constants.

| # | Finding | Files | Action | 
|---|---------|-------|--------|
| 1 | Cache name `"stores"` literal | `StoreRepositoryAdapter:50,57,82` | Replace with `CacheConfiguration.STORES_CACHE` |
| 2 | Cache name `"feedback-questions"` literal | `FeedbackQuestionRepositoryAdapter:47,53` | Replace with `CacheConfiguration.FEEDBACK_QUESTIONS_CACHE` |
| 3 | Default page size `20` hardcoded | `PriceWebAdapter:84`, `PaginationUtils:43` | Replace with `AppConstants.DEFAULT_PAGE_SIZE` |
| 4 | `"asc"`/`"desc"` sort direction strings | `PriceWebAdapter:86`, `AbstractCrudWebAdapter:35`, `PaginationHelper:47`, `PriceRepositoryAdapter:101` | Replace with `SortConstants.ASC`/`DESC` |
| 5 | `"name"`/`"status"`/`"id"`/`"category"` in specs | `StoreRepositoryAdapter`, `UnitRepositoryAdapter`, `ProductSpecification` | Replace with `EntityConstants.NAME`/`STATUS`/`ID`/`CATEGORY` |

### Tier 2 — Short-term (Add Missing Constants)

Estimated effort: ~1 hour. Add new constants, then reference them.

| # | Finding | Files | Action |
|---|---------|-------|--------|
| 6 | Missing cache constants | `UnitRepositoryAdapter("units")`, `ShoppingOptimizer("shopping-optimization")`, `BillSplitService("bill-splits")`, `LlmService("llm-responses")` | Add to `CacheConfiguration`, then reference in adapters |
| 7 | Missing EntityConstants entries | `"brand"`, `"location"`, `"chain"`, `"address"`, `"symbol"`, `"type"` | Add to `EntityConstants`, then replace in `ProductSpecification`, `StoreRepositoryAdapter`, `UnitRepositoryAdapter` |
| 8 | Missing ErrorMessageConstants | `AdminService` (3 msgs), `PriceWebAdapter` (1 msg) | Add to `ErrorMessageConstants`, then replace |

### Tier 3 — Medium-term (Logic Changes)

Estimated effort: ~2 hours. Requires understanding of caching behavior.

| # | Finding | Files | Action |
|---|---------|-------|--------|
| 9 | Missing `@CacheEvict` on deleteById | `UnitRepositoryAdapter`, `CategoryRepositoryAdapter`, `FeedbackQuestionRepositoryAdapter` | Add `@CacheEvict(cacheNames = "...", key = "#id")` to deleteById overrides |

### Tier 4 — Long-term (Architecture)

Estimated effort: ~4-8 hours. Requires design discussion.

| # | Finding | Action |
|---|---------|--------|
| 10 | `AbstractGenericService` only 33% adoption | Evaluate extending to StoreService, ProductService, PriceService. Verify if `@ActivityLog` annotation and cross-service DI are blockers. |

---

## Quality Gates

Since this is a code remediation task, each tier should be released with standard gates:
1. `mvn spotless:apply` — formatting
2. `mvn compile` — compilation
3. `mvn test` — ArchUnit + unit tests (some pre-existing failures in activity/category/admin/alert)
4. `mvn verify` — static analysis

---

## Dependency Order

```
Tier 1 ──► Tier 2 ──► Tier 3 ──► Tier 4
(no deps)  (needs T1)  (needs T2)  (independent)
```

Each tier can be released as its own PR. Tiers 1-2 are independent of Tiers 3-4.
