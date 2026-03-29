# AI Agent Guidelines for goods-price-comparison-service

## 🎯 AI Agent Mission

Build a production-ready price comparison service with the highest code quality standards. Every line of code must be tested, documented, and optimized.

---

## 🚫 CRITICAL: PR Blocking Rules

**AI CANNOT create a PR if ANY of these conditions are not met:**

### 1. Build Verification ❌
```bash
# Must pass - NO EXCEPTIONS
mvn clean compile -q
```
- Zero compilation errors
- Zero compilation warnings
- All dependencies resolved

### 2. Test Verification ❌
```bash
# Must pass - NO EXCEPTIONS
mvn clean test
```
- All tests must pass (0 failures)
- Coverage must meet thresholds (see Coverage Requirements)
- No test skips without justification

### 3. Code Quality ❌
```bash
# Must pass - NO EXCEPTIONS
mvn spotbugs:check
mvn checkstyle:check
```
- No SpotBugs violations
- No Checkstyle violations
- No code smells

### 4. Integration Test ❌
```bash
# Must pass for PRs with DB changes
mvn verify -P integration-tests
```
- Database migrations work
- API contracts maintained
- End-to-end flow works

**⚠️ IF ANY CHECK FAILS:**
1. STOP immediately
2. Fix all issues
3. Re-run verification
4. Only then proceed to PR

---

## 📊 Code Coverage Requirements

### Strict Enforcement

| Code Type | Minimum Coverage | Target Coverage |
|-----------|------------------|-----------------|
| **Existing Code** | 90% | 95% |
| **New Code** | **100%** | **100%** |
| **Critical Path** | 95% | 100% |
| **Utilities** | 95% | 100% |

### Coverage Breakdown

**Line Coverage:**
- Every line must be executed by tests
- No dead code allowed
- All branches covered

**Branch Coverage:**
- All if/else branches tested
- All switch cases tested
- All exception paths tested

**Mutation Coverage (PIT):**
- 80%+ mutation score
- Code changes must fail tests

### How to Check Coverage

```bash
# Run with coverage
mvn clean test jacoco:report

# Check coverage report
cat target/site/jacoco/index.html | grep -o "[0-9]*%"

# Coverage must show:
# - Total: 90%+
# - New Files: 100%
```

### Coverage Report Location
```
target/site/jacoco/index.html
```

---

## 📝 Documentation Structure

### Root Level (ONLY these files)

```
goods-price-comparison-service/
├── README.md          ← MAIN DOCUMENTATION (keep updated!)
├── LICENSE
├── .gitignore
├── pom.xml
└── ... (source code)
```

**RULE: Only README.md in root. Everything else in subfolders.**

### Documentation Links in README

Your README.md must contain these links:

```markdown
## 📚 Documentation

- [Architecture & Design](docs/ARCHITECTURE.md)
- [API Documentation](docs/API.md)
- [Database Schema](docs/DATABASE.md)
- [Deployment Guide](docs/DEPLOYMENT.md)
- [Testing Guide](docs/TESTING.md)
- [Contributing Guidelines](CONTRIBUTING.md)
- [AI Agent Guidelines](.ai/AGENTS.md) ← For AI agents only

## 🤖 AI Documentation

> **For AI agents working on this project:**
> See [.ai/README.md](.ai/README.md) for complete AI guidelines
```

### Subfolder Structure

```
docs/
├── ARCHITECTURE.md      # System design, diagrams
├── API.md              # API endpoints, examples
├── DATABASE.md         # Schema, migrations
├── DEPLOYMENT.md       # Docker, K8s, CI/CD
├── TESTING.md          # Test strategy, examples
└── CHANGELOG.md        # Version history

.ai/                    # AI-specific (HUMANS: DON'T EDIT)
├── README.md           # AI doc index
├── AGENTS.md           # This file - main guidelines
├── RULES.md            # Coding standards
├── SKILLS.md           # Required capabilities
├── PR_WORKFLOW.md      # PR blocking mechanism
├── COVERAGE.md         # Coverage requirements detail
└── README_TEMPLATE.md  # Auto-update template
```

---

## 🔄 Auto README Update Before PR

**MANDATORY: Update README.md before every PR**

### What to Update

1. **Features Section**
   - Add new features implemented
   - Mark completed roadmap items

2. **API Endpoints**
   - Add new endpoints
   - Update examples

3. **Changelog**
   - Add version entry
   - List all changes

4. **Statistics**
   - Update test counts
   - Update coverage %
   - Update line counts

### Auto-Update Script

Before PR, run:

```bash
# This script auto-updates README sections
./scripts/update-readme.sh
```

Script will:
1. Count test files → Update test count
2. Get coverage % → Update badge
3. Count LOC → Update metrics
4. Update changelog with git log

### README Update Checklist

- [ ] New features documented
- [ ] API changes reflected
- [ ] Test count updated
- [ ] Coverage badge current
- [ ] Changelog entry added
- [ ] Screenshots updated (if UI changes)

---

## 🎨 Code Quality Standards

### 1. Code Style

**Follow Google Java Style Guide:**
- 4 spaces indentation
- 100 char line limit
- Javadoc for all public APIs
- Final variables by default

**Check with:**
```bash
mvn checkstyle:check
```

### 2. Static Analysis

**SpotBugs Configuration:**
- High priority bugs = ERROR (blocks PR)
- Medium priority bugs = WARNING
- Low priority bugs = INFO

**Check with:**
```bash
mvn spotbugs:check
```

### 3. Code Metrics

**Maximum Values:**
- Method length: 50 lines
- Class length: 500 lines
- Cyclomatic complexity: 10
- Method parameters: 5

**Check with:**
```bash
mvn pmd:check
```

---

## 🧪 Testing Standards

### Test Requirements

**Every public method MUST have:**
- Unit test with @Test
- Happy path test
- Edge case test(s)
- Exception test(s)

**Test Naming:**
```java
@Test
@DisplayName("Should calculate total price when quantity is positive")
void shouldCalculateTotalPrice_WhenQuantityIsPositive() {
    // given
    // when
    // then
}
```

### Test Categories

1. **Unit Tests** (80%)
   - Individual methods
   - Mocked dependencies
   - Fast execution (< 100ms)

2. **Integration Tests** (15%)
   - Database interactions
   - API endpoints
   - External services (mocked)

3. **E2E Tests** (5%)
   - Full user flows
   - Real database
   - Actual OCR calls (limited)

### Test Data

**Use:**
- @DataJpaTest for DB tests
- Testcontainers for integration
- @MockBean for external services

**DON'T Use:**
- Production data in tests
- Hardcoded IDs (use sequences)
- Real external APIs (mock them)

---

## 🚀 Performance Standards

### Response Time Requirements

| Operation | Max Response Time |
|-----------|------------------|
| Receipt Upload | < 3 seconds |
| OCR Processing | < 10 seconds |
| Price Query | < 100ms |
| Shopping Optimization | < 500ms |
| Health Check | < 50ms |

### Database Performance

- Query time: < 10ms
- Connection pool: HikariCP (min 5, max 20)
- Query timeout: 5 seconds
- Index usage: 100% on WHERE clauses

### Memory Usage

- Max heap: 512MB (development)
- Max heap: 1GB (production)
- No memory leaks in tests (check with JProfiler)

---

## 🔒 Security Standards

### Data Protection

- **NEVER** log API keys or secrets
- **NEVER** commit .env files
- Use Spring @ConfigurationProperties for secrets
- Validate all inputs (XSS, SQL injection)

### API Security

- Rate limiting: 100 req/min per IP
- Input validation on all endpoints
- CORS configured properly
- No stack traces in error responses (production)

---

## 📋 Pre-PR Checklist

**AI MUST complete this checklist before creating PR:**

### Build & Test
- [ ] `mvn clean compile -q` passes
- [ ] `mvn test` passes (100% success)
- [ ] Coverage >= 90% (existing), 100% (new)
- [ ] `mvn verify` passes

### Code Quality
- [ ] Checkstyle passes
- [ ] SpotBugs passes (no high priority)
- [ ] PMD passes
- [ ] No TODO/FIXME comments (or create issues)

### Documentation
- [ ] README.md updated
- [ ] JavaDoc for all public methods
- [ ] API documentation updated
- [ ] Architecture docs updated (if structural changes)

### Git
- [ ] Meaningful commit messages
- [ ] Branch named properly: `feature/description` or `fix/description`
- [ ] Rebased on latest main
- [ ] No merge conflicts

### Testing
- [ ] Unit tests for all new code (100% coverage)
- [ ] Integration tests for DB changes
- [ ] E2E tests for critical paths
- [ ] Manual testing completed

---

## 🚫 Forbidden Practices

**NEVER DO:**

1. **Code Quality**
   - Commit commented-out code
   - Use `System.out.println()` (use logging)
   - Ignore exceptions (always handle)
   - Use magic numbers/strings

2. **Testing**
   - Skip tests to save time
   - Write tests after code (TDD preferred)
   - Use Thread.sleep() in tests
   - Test private methods directly

3. **Git**
   - Commit to main directly
   - Force push to shared branches
   - Commit large binary files
   - Include credentials in code

4. **Documentation**
   - Leave placeholder text
   - Copy-paste without updating
   - Skip documentation for "obvious" things
   - Use outdated screenshots

---

## 🎯 Success Metrics

**AI Agent success measured by:**

1. **Code Quality**
   - Zero critical bugs
   - < 5 medium bugs per PR
   - 100% test coverage on new code

2. **Performance**
   - All response times within SLA
   - No performance regression
   - Memory usage stable

3. **Documentation**
   - README always current
   - JavaDoc complete
   - Architecture docs accurate

4. **Process**
   - PR approval rate > 95%
   - Review turnaround < 2 days
   - Zero breaking changes

---

## 📞 Getting Help

**If stuck or unclear:**
1. Check [.ai/RULES.md](RULES.md) for coding standards
2. Check [.ai/SKILLS.md](SKILLS.md) for capabilities
3. Check [.ai/COVERAGE.md](COVERAGE.md) for testing details
4. Review [docs/ARCHITECTURE.md](../docs/ARCHITECTURE.md) for design

**When to ask human:**
- Architectural decisions
- Breaking changes
- Security concerns
- Performance bottlenecks

---

## 🎓 Continuous Improvement

**After every PR:**
1. Review what went well
2. Identify areas for improvement
3. Update these guidelines if needed
4. Share learnings with other AI agents

---

*Remember: Quality over speed. A perfect PR is better than a fast PR.*

*Last updated: [AUTO-UPDATED BY CI]*