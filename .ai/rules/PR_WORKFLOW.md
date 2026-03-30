# PR Workflow & Blocking Mechanism

## 🚫 PR Blocking Rules

**NO PR will be created if ANY check fails. Period.**

---

## Pre-PR Checklist

### Phase 1: Build Verification

```bash
# 1. Clean Compile
mvn clean compile -q
```
**Must Pass:**
- Zero compilation errors
- Zero warnings
- All dependencies resolved

**If Fails:**
1. Fix compilation errors
2. Address all warnings
3. Re-run until clean

### Phase 2: Test Execution

```bash
# 2. Run All Tests
mvn clean test
```
**Must Pass:**
- 100% tests passing (0 failures, 0 errors)
- 100% tests completed (0 skips without justification)
- Test time < 5 minutes

**Coverage Requirements:**
- Overall: >= 90%
- New Code: 100%
- Modified Code: 100%

```bash
# 3. Generate Coverage Report
mvn jacoco:report
# Check: target/site/jacoco/index.html
```

### Phase 3: Code Quality

```bash
# 4. Checkstyle
mvn checkstyle:check
```
**Must Pass:**
- 0 violations

```bash
# 5. SpotBugs
mvn spotbugs:check
```
**Must Pass:**
- 0 high priority bugs
- 0 medium priority bugs (without approval)

```bash
# 6. PMD (if configured)
mvn pmd:check
```

### Phase 4: Integration Tests

```bash
# 7. Full Integration Test
mvn verify -P integration-tests
```
**Must Pass:**
- Database migrations work
- API contracts maintained
- End-to-end flow works

### Phase 5: Documentation

```bash
# 8. Update README
./scripts/update-readme.sh
```
**Must Update:**
- Feature list
- API endpoints
- Test counts
- Coverage badges
- Changelog

---

## PR Creation Flow

```
┌─────────────────────────────────────────────────────────┐
│  START: Feature Complete                                │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────┐
│  1. Run: mvn clean compile -q                          │
│     ✓ Pass? → Continue                                  │
│     ✗ Fail? → Fix errors → Retry                        │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────┐
│  2. Run: mvn clean test                                │
│     ✓ Pass? → Continue                                  │
│     ✗ Fail? → Fix tests → Retry                         │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────┐
│  3. Check Coverage >= 90% (100% for new)               │
│     ✓ Pass? → Continue                                  │
│     ✗ Fail? → Add tests → Retry                         │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────┐
│  4. Run: Code Quality Checks                           │
│     - Checkstyle: 0 violations                          │
│     - SpotBugs: 0 high priority                         │
│     ✓ Pass? → Continue                                  │
│     ✗ Fail? → Fix issues → Retry                        │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────┐
│  5. Run: Integration Tests                             │
│     ✓ Pass? → Continue                                  │
│     ✗ Fail? → Fix integration → Retry                   │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────┐
│  6. Update README.md                                   │
│     ✓ Updated? → Continue                               │
│     ✗ Skip? → STOP: Must update documentation           │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────┐
│  7. Commit All Changes                                 │
│     git add -A                                          │
│     git commit -m "feat: description"                   │
└──────────────────┬──────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────┐
│  8. Create PR                                          │
│     git push origin feature/name                        │
│     gh pr create ...                                    │
└─────────────────────────────────────────────────────────┘
```

---

## GitHub Actions CI/CD

### Workflow File

Create `.github/workflows/ci.yml`:

```yaml
name: CI - PR Blocking

on:
  pull_request:
    branches: [ main ]
  push:
    branches: [ main ]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    
    services:
      postgres:
        image: postgres:14
        env:
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
          POSTGRES_DB: testdb
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Cache Maven dependencies
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    
    - name: Install shared libraries
      run: |
        # Install common-utils-java
        git clone https://github.com/RizkiRachman/common-utils-java.git /tmp/common-utils-java
        cd /tmp/common-utils-java && mvn clean install -DskipTests
        
        # Install common-exception-java
        git clone https://github.com/RizkiRachman/common-exception-java.git /tmp/common-exception-java
        cd /tmp/common-exception-java && mvn clean install -DskipTests
    
    - name: Compile
      run: mvn clean compile -q
      
    - name: Run tests
      run: mvn test
      env:
        SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/testdb
        SPRING_DATASOURCE_USERNAME: test
        SPRING_DATASOURCE_PASSWORD: test
    
    - name: Generate coverage report
      run: mvn jacoco:report
    
    - name: Check coverage threshold
      run: |
        COVERAGE=$(cat target/site/jacoco/index.html | grep -o 'Total[^%]*%' | grep -o '[0-9]*' | head -1)
        if [ "$COVERAGE" -lt "90" ]; then
          echo "Coverage $COVERAGE% is below threshold 90%"
          exit 1
        fi
    
    - name: Run Checkstyle
      run: mvn checkstyle:check
    
    - name: Run SpotBugs
      run: mvn spotbugs:check
    
    - name: Integration tests
      run: mvn verify -P integration-tests
      env:
        SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/testdb
        SPRING_DATASOURCE_USERNAME: test
        SPRING_DATASOURCE_PASSWORD: test
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        file: ./target/site/jacoco/jacoco.xml
        fail_ci_if_error: true

  readme-check:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Check README updated
      run: |
        # Check if README was modified in last commit
        if ! git diff --name-only HEAD~1 | grep -q "README.md"; then
          echo "WARNING: README.md not updated in this PR"
          echo "Please update documentation before merging"
          # Uncomment to make this a hard failure:
          # exit 1
        fi
```

---

## Branch Protection Rules

Configure in GitHub Settings:

```
Branch: main

Protection Rules:
✅ Require pull request reviews before merging (1 approval)
✅ Require status checks to pass before merging
   - ci/build-and-test
   - ci/readme-check
✅ Require branches to be up to date before merging
✅ Include administrators
✅ Allow force pushes: NO
✅ Allow deletions: NO
```

---

## Failure Scenarios

### Scenario 1: Build Fails

```
[ERROR] COMPILATION ERROR
[ERROR] /src/main/java/.../PriceService.java:[25,10] cannot find symbol

AI Action:
1. Read compilation error
2. Fix missing import or class
3. Run: mvn clean compile -q
4. Verify clean build
5. Commit fixes
6. Retry PR
```

### Scenario 2: Tests Fail

```
Tests run: 50, Failures: 3, Errors: 0, Skipped: 0

AI Action:
1. Run: mvn test -Dtest=FailedTestClass
2. Analyze test failure
3. Fix code or test
4. Verify: mvn test (all pass)
5. Commit fixes
6. Retry PR
```

### Scenario 3: Coverage Low

```
Coverage: 78% (Required: 90%)
New Code Coverage: 85% (Required: 100%)

AI Action:
1. Identify uncovered lines: mvn jacoco:report
2. Open: target/site/jacoco/index.html
3. Find classes with < 90% coverage
4. Add unit tests for uncovered code
5. Verify: mvn test jacoco:report
6. Commit new tests
7. Retry PR
```

### Scenario 4: Checkstyle Violations

```
[ERROR] src/main/java/.../PriceService.java:25:5:
        'METHOD_DEF' has more than 50 lines.

AI Action:
1. Run: mvn checkstyle:checkstyle
2. Open: target/checkstyle-result.xml
3. Fix all violations
4. Verify: mvn checkstyle:check
5. Commit fixes
6. Retry PR
```

---

## Success Criteria

**PR is ready when:**

- [ ] Build: Clean compile (0 errors, 0 warnings)
- [ ] Tests: 100% passing (0 failures)
- [ ] Coverage: >= 90% overall, 100% new code
- [ ] Quality: 0 Checkstyle violations
- [ ] Bugs: 0 SpotBugs high/medium priority
- [ ] Integration: All integration tests pass
- [ ] Docs: README.md updated with changes
- [ ] Git: Clean commit history, no merge conflicts

**Only then create PR!**

---

*Remember: Quality over speed. A perfect PR is worth the wait.*