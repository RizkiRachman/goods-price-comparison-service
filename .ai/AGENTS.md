# AI Agent Guidelines for goods-price-comparison-service

Quick reference for AI agents working on this Spring Boot service.

---

## 🚨 Critical Rules

### 1. NEVER Push to Main
**ABSOLUTELY FORBIDDEN:**
- ❌ Direct commits to `main`
- ❌ Direct pushes to `main`  
- ❌ Using `--force` or `--admin` on main

**ALWAYS:**
- ✅ Create feature branch: `git checkout -b feature/description`
- ✅ Push to feature branch: `git push origin feature/description`
- ✅ Create Pull Request
- ✅ Wait for CI + Review

### 2. NEVER Auto-Merge
**AI CANNOT MERGE WITHOUT EXPLICIT PERMISSION**

**❌ ABSOLUTELY FORBIDDEN:**
- Auto-merging when CI passes
- Merging without user saying "merge this"
- Assuming approval from "looks good" or 👍
- **Using approval from previous sessions** - Each PR needs fresh approval
- **Assuming implicit permission** - Must ask explicitly every time

**✅ REQUIRED:**
- **Ask for permission explicitly:** "PR is ready, should I merge?"
- Wait for clear instruction: "merge this" or "approved, please merge"
- **No exceptions** - even if previous PRs were approved
- Only then execute merge command

**If user says "stop", "wait", or doesn't respond:**
- STOP immediately
- Do NOT merge
- Wait for explicit "merge" instruction
- **Do not proceed without clear permission**

---

## 📋 Strict Merge Policy Summary

**Every Single PR Must Follow This:**

1. ✅ Create feature branch
2. ✅ Make changes
3. ✅ Run all checks (build, test, coverage)
4. ✅ Push to feature branch
5. ✅ Create PR
6. ✅ **ASK: "PR is ready, should I merge?"**
7. ⏳ **Wait for explicit "merge" or "approved"**
8. ✅ Only then merge

**⚠️ CRITICAL REMINDERS:**
- **Previous approvals do NOT apply** - Ask every time
- **CI passing does NOT mean merge** - Wait for human approval
- **No assumptions** - Must get explicit permission
- **When in doubt, ask** - Better to ask than assume

---

## 🚫 PR Blocking Rules

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

## 📁 Documentation Structure

| Folder | Purpose | Files |
|--------|---------|-------|
| [rules/](./rules/) | **Standards** (MUST follow) | CODING_STANDARDS.md, PR_WORKFLOW.md |
| [skills/](./skills/) | **How-to guides** | JAVA.md, TESTING.md |
| [context/](./context/) | **Project info** | PROJECT_OVERVIEW.md |

### Quick Links

**Start Here:**
1. [rules/PR_WORKFLOW.md](./rules/PR_WORKFLOW.md) - How to create PRs
2. [rules/CODING_STANDARDS.md](./rules/CODING_STANDARDS.md) - Code style
3. [context/PROJECT_OVERVIEW.md](./context/PROJECT_OVERVIEW.md) - What this project is

**Reference:**
- [skills/JAVA.md](./skills/JAVA.md) - Java 17+ features
- [skills/TESTING.md](./skills/TESTING.md) - Testing guide with 100% coverage

**Coverage Requirements:**
- [skills/TESTING.md#coverage-requirements](./skills/TESTING.md) - Detailed coverage standards

---

## 🛠️ Common Commands

```bash
# Development
mvn clean compile -q    # Build project
mvn clean test          # Run tests  
mvn clean verify        # Full verification with coverage
mvn spring-boot:run -Dspring-boot.run.profiles=local  # Run locally

# Code quality
mvn spotbugs:check      # Static analysis
mvn checkstyle:check    # Style check

# Git workflow
git checkout -b feature/name
git add .
git commit -m "type: description"
git push -u origin feature/name
gh pr create
```

---

## 📝 Commit and PR Messages

### Rule: Write Meaningful, Natural Messages

Your commits and PRs must be **clear, simple, and meaningful**. Write as if explaining to a teammate.

### ✅ Good Examples

```bash
# Commits
Add Store entity with JPA annotations
Fix null pointer in price calculation
Update README with local setup instructions
Remove deprecated OAuth config
Add unit tests for Product repository

# PR Titles
Add price comparison endpoint with pagination
Fix H2 database configuration for local dev
Update dependencies to latest stable versions
```

### ❌ Bad Examples

```bash
# Too vague
Fix bug
Update code
Changes
Refactor stuff

# Not meaningful
WIP
Fix
Update v2
```

### Guidelines

- **Start with a verb** (Add, Fix, Update, Remove, Refactor)
- **Describe what changed** in plain English
- **Be specific** - "Fix price rounding error" not "Fix bug"
- **Keep first line under 50 characters**
- **Add details in body** if the change needs explanation
type(scope): description

types: feat, fix, docs, style, refactor, test, chore, ci

examples:
- feat(prices): Add pagination to price search
- fix(receipts): Handle null image data
- docs(readme): Update API examples
- test(entities): Add 100% coverage for Store entity
```

---

## 📊 Code Coverage Requirements

### Strict Enforcement

| Code Type | Minimum Coverage | Target Coverage |
|-----------|------------------|-----------------|
| **Existing Code** | 90% | 95% |
| **New Code** | **100%** | **100%** |
| **Critical Path** | 95% | 100% |
| **Utilities** | 95% | 100% |

**Check coverage:**
```bash
mvn clean test jacoco:report
cat target/site/jacoco/index.html | grep -o "[0-9]*%"
```

---

## 🎯 Key Points

**Project Type:** Spring Boot Microservice
- OCR receipt processing
- Price comparison and tracking
- PostgreSQL database with Flyway migrations
- REST API with OpenAPI specs (from separate repo)

**Architecture:**
- Spring Boot 3.x with Java 17+
- Layered architecture (Controller → Service → Repository)
- JPA/Hibernate for database access
- Testcontainers for integration testing
- JaCoCo for coverage enforcement

---

## 🆘 Help

**Check documentation first:**
1. `rules/` - Standards to follow
2. `skills/` - How to do things  
3. `context/` - Project information

**Ask human for:**
- Architectural decisions
- Breaking changes
- Security concerns
- Performance optimizations

---

## 👤 Maintainer

**Rizki Rachman**  
📧 rizkifaizalr@gmail.com  
🔗 [@RizkiRachman](https://github.com/RizkiRachman)

---

**Remember: Quality > Speed. A perfect PR is better than a fast PR.**

*Last updated: 2026-03-30*
