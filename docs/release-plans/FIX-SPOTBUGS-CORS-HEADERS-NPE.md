# Release Plan: Fix SpotBugs FindSecBugs CORS Headers NPE

**Date**: 2026-05-15
**Branch**: `ANEH-20260515-FIX-SPOTBUGS-CORS-HEADERS-NPE`
**PR**: [#85](https://github.com/RizkiRachman/goods-price-comparison-service/pull/85)

---

## Summary

Fix recurring FindSecBugs `CorsRegistryCORSDetector` NPE that appears as an analysis error during `mvn verify`:

```
Exception analyzing com.example.goodsprice.config.CorsConfiguration using detector com.h3xstream.findsecbugs.spring.CorsRegistryCORSDetector
  java.lang.NullPointerException: Cannot invoke "java.lang.Integer.intValue()" because "argumentsNum" is null
```

**Root Cause**: `CorsRegistryCORSDetector.getStringArray()` expects `String[]` arguments to inspect method calls. When `.allowedHeaders(String)` receives a single `String` (not `String[]`), the variable `argumentsNum` stays null and causes an NPE.

**Fix**: Change `CorsProperties.allowedHeaders` from `String` to `List<String>` and pass `toArray(new String[0])` in `CorsConfiguration`, matching the exact pattern used by `allowedOrigins` and `allowedMethods`. This makes the detector happy since all three calls now use the same bytecode pattern.

---

## Changes

| File | Change |
|------|--------|
| `CorsProperties.java` | `String allowedHeaders` → `List<String> allowedHeaders` |
| `CorsConfiguration.java` | `.allowedHeaders(...)` → `allowedHeaders(...toArray(new String[0]))` |
| `CHANGELOG.md` | Added entry under `[Unreleased]` → `Fixed` |

---

## Quality Gates (All Passed)

| Gate | Result |
|------|--------|
| `mvn spotless:apply` | ✅ No changes |
| `mvn test` | ✅ 137/137 pass |
| `mvn verify` | ✅ Error size 0 (no more NPE) |

---

## Impact Analysis

- **No behavioral change**: `allowedHeaders("*")` and `allowedHeaders(List.of("*").toArray(...))` are functionally equivalent in Spring CORS
- **Test impact**: None — all 137 tests pass unchanged
- **API impact**: None — this is build/infra config only
- **No schema, event, or API changes**

---

## Deployment

Standard rollout via existing pipeline. No migration needed. Trivial change — can be fast-tracked.
