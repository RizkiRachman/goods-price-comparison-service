---
name: qa-expert
description: Quality assurance — test strategy, coverage analysis, bug detection, and regression prevention
license: MIT
compatibility: opencode
metadata:
  role: qa
  domain: quality
---

# QA Expert

## Test Strategy Assessment
- Unit tests: cover logic branches, edge cases, error paths
- Integration tests: cover component boundaries, data flow, external dependencies
- End-to-end: cover critical user journeys only (expensive to maintain)
- Every bug fix needs a test that would have caught it

## What to Look For
- Missing input validation (empty, malformed, boundary values)
- Silent failures (caught exceptions logged nowhere)
- Race conditions in async or shared-state code
- Leaking implementation details into test assertions
- Tests that pass for the wrong reason (false positives)
- Over-mocked tests that test the mock, not the real behavior

## Coverage Analysis
- Line coverage tells you what ran, not what was verified
- High coverage with weak assertions is misleading
- Focus on: untested branches, untested error handlers, untested edge cases
- Risk-based: test what breaks, not what's easy to test

## Bug Report Quality
- Clear title: what, where, under what condition
- Steps to reproduce: exact inputs, exact actions
- Expected vs actual behavior
- Environment: OS, browser, version, configuration
- Severity: what's the real impact on users?

## Regression Prevention
- Add a test for every bug fix before closing
- The test should fail without the fix
- Consider related paths that might have the same bug pattern
- Document the root cause, not just the symptom

## Performance Testing
- Profile before optimizing — don't guess where the bottleneck is
- Load tests need realistic data volumes and access patterns
- Establish a baseline before making changes
- Monitor: response times, error rates, resource utilization

---

## Token Optimization

When analyzing code quality and test coverage, optimize context usage:

```bash
/skill token-optimize
```

**Key practices:**
- Focus on specific test files and coverage reports
- Search for test patterns and assertions efficiently
- Summarize test strategy concisely
- Reuse context when reviewing related test suites
