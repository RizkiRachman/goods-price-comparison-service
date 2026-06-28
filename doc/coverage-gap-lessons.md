# Coverage Gap Campaign — Lessons Learned

**Date**: 2026-06-28
**Branch**: `feature/20260628-coverage-gap-fix`
**Commit**: `b6b72e2`
**Review**: `doc/coverage-gap-review.md`
**Retrospective Format**: Start/Stop/Continue

---

## Overview

Campaign closed 18 identified coverage gaps across 14 test files + 1 production file (580+ insertions, 35+ tests) to meet JaCoCo thresholds (instruction ≥ 90%, branch ≥ 85%). All gaps addressed. Verdict: PASS ✅.

---

## Start Doing

### 1. Prefer package-private visibility over `@VisibleForTesting` for testability

When a private method must be tested (e.g., `GeminiLlmProvider.parseResponse`), change visibility to **package-private** (no annotation). This is the minimal change, LOW risk if the method has only internal callers. Verify with `gitnexus_impact()`.

**Evidence**: `GeminiLlmProvider.parseResponse` changed from `private` → package-private. Impact analysis: LOW risk, 1 caller in the same class.

### 2. Add `verify(mock, never())` guards alongside `lenient()` mocks

When using `lenient()` on mocks in `@BeforeEach`, add explicit `verify(mock, never()).method()` guards on tests that should **not** trigger the mocked method. This catches regression where a test accidentally calls the mock.

**Evidence**: `ReceiptServiceTest` uses `lenient().doAnswer()` on `receiptApprovalInPort` in `@BeforeEach`. The `lenient()` setting suppresses `UnnecessaryStubbingException`, which means unintended calls to `approve()` from other tests would go undetected.

### 3. Document abstract base test limitations in Javadoc

`AbstractRepositoryAdapterTest` uses synthetic types (`JpaRepository<String, Long>`, trivial mappers). Add a Javadoc note: *"These tests verify structural behavior only. Subclasses must also add domain-specific mapping tests."*

---

## Stop Doing

### 1. Heavy Spring contexts for config switch testing (prematurely optimize if needed)

`LlmProviderConfigurationTest` uses 4 `@Nested @SpringBootTest(properties={...})` classes, each creating a separate Spring context (~30-60s total). The contexts cannot be shared because `properties` differ. This is acceptable for CI but worth optimizing if test suite runtime becomes a concern.

**Alternative**: Extract the switch logic into a `@VisibleForTesting` static method and unit-test it directly. This eliminates Spring context overhead entirely.

---

## Continue Doing

### 1. @Nested + @SpringBootTest(properties=...) pattern for config testing

Despite the Spring context overhead, this pattern is **clean and maintainable** for testing switch/branch expressions in configuration classes. Each nested class tests exactly one provider selection path with no mocking or reflection.

```
LlmProviderConfigurationTest
├── default (local)
├── @Nested GeminiProviderTest
├── @Nested GroqProviderTest
└── @Nested SumopodProviderTest
```

### 2. Parallel execution for disjoint test files

All 15 tasks touched different files and could be parallelized. The plan correctly identified this and split into 2 waves of 8 and 7 agents. This pattern should be reused for any multi-file test-only campaign.

### 3. Edge case exploration in boundary testing

The campaign delivered excellent boundary coverage:

| Test | Edge Case |
|------|-----------|
| `ActivityLogAspectTest` | `PriceService$` class (Java proxy `$` suffix) |
| `ProductRepositoryAdapterTest` | Empty list early-return guard (`verify(..., never())`) |
| `RateLimitInterceptorTest` | Full fallback chain: annotation → endpoint config → global default |
| `ReceiptServiceTest` | `extractTotalAmount`: null, Number, String, NumberFormatException |
| `GeminiLlmProviderTest` | `parseResponse`: null, empty, ```json, ```, non-JSON |

---

## Knowledge Artifacts

### Patterns Persisted

| Key | Category | What |
|-----|----------|------|
| `pattern/nested-config-switch-testing` | testing | @Nested + @SpringBootTest(properties=...) for config switch testing |
| `pattern/package-private-for-testability` | testing | Minimal production change for testing private methods |

### Gotchas Persisted

| Key | Category | What |
|-----|----------|------|
| `lenient-in-beforeeach-masks-interactions` | gotchas | lenient() in @BeforeEach masks unintended mock interactions |
| `abstract-repository-test-false-confidence` | gotchas | AbstractRepositoryAdapterTest tests structure, not mapping |

### Conventions Persisted

| Key | Category | What |
|-----|----------|------|
| `extractTotalAmount-private-method-testing` | conventions | Test private methods via public API by controlling mock returns |

---

## Scorecard

| Dimension | Score | Rationale |
|-----------|-------|-----------|
| Requirements fulfillment | 40/40 | All 18 gaps closed, thresholds met |
| Governance compliance | 30/30 | Only 1 minimal production change, proper impact analysis |
| Completeness | 18/20 | 3 minor gaps flagged (no X-Forwarded-For absence test, magic UUID, missing pagination assertions) |
| Edge cases | 10/10 | Excellent boundary exploration across all test files |
| **Combined** | **97/100** | **PASS** ✅ |

---

## Next Session Tips

On resume:
1. Address the 3 minor gaps identified in the review (X-Forwarded-For header absence test, NOT_FOUND_ID → UUID.randomUUID(), pagination assertions in PriceWebAdapterTest)
2. Consider static extraction of LlmProviderConfiguration switch logic if CI runtime is a concern
3. Address the lenient() anti-pattern in ReceiptServiceTest
