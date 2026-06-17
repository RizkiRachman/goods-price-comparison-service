# Contributing to Goods Price Comparison Service

Thank you for your interest in contributing to this project! This document provides guidelines and instructions for contributing.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Pull Request Process](#pull-request-process)
- [Coding Standards](#coding-standards)
- [Testing Requirements](#testing-requirements)
- [Commit Message Guidelines](#commit-message-guidelines)
- [Documentation](#documentation)

## Code of Conduct

This project adheres to a code of conduct. By participating, you are expected to uphold this code:

- Be respectful and inclusive
- Welcome newcomers and help them learn
- Focus on constructive feedback
- Respect different viewpoints and experiences

## Getting Started

### Prerequisites

Before you begin, ensure you have:

- **Java 21+** installed
- **Maven 3.9+** installed
- **PostgreSQL 14+** (for production profile)
- **Git** configured

### Setting Up Development Environment

1. **Fork and clone the repository:**
   ```bash
   git clone https://github.com/YOUR_USERNAME/goods-price-comparison-service.git
   cd goods-price-comparison-service
   ```

2. **Install shared libraries (if not already installed):**
   ```bash
   # Clone and install common-utils-java
   git clone https://github.com/RizkiRachman/common-utils-java.git
   cd common-utils-java && mvn clean install
   
   # Clone and install common-exception-java
   git clone https://github.com/RizkiRachman/common-exception-java.git
   cd common-exception-java && mvn clean install
   ```

3. **Build the project:**
   ```bash
   mvn clean compile -q
   ```

4. **Run tests:**
   ```bash
   mvn clean test
   ```

5. **Run locally with H2 database:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=local
   ```

## Development Workflow

### 1. Create a Feature Branch

Always create a new branch for your work:

```bash
# Pull latest changes
git checkout main
git pull origin main

# Create feature branch
git checkout -b feature/YYYYMMDD-your-feature-name
```

**Branch naming conventions:**
- `feature/YYYYMMDD-<short-description>` - New features
- `bugfix/YYYYMMDD-<short-description>` - Bug fixes
- `fix/description` - Bug fixes (alternative)
- `docs/description` - Documentation updates
- `refactor/description` - Code refactoring
- `test/description` - Test additions/improvements

### 2. Make Your Changes

- Write clean, maintainable code
- Follow the [Coding Standards](#coding-standards)
- Add tests for new functionality
- Update documentation as needed

### 3. Test Your Changes

Before submitting a PR, run all quality gates:

```bash
# Fix formatting
mvn spotless:apply

# Run all tests + ArchUnit (7 rules)
mvn clean test

# Full quality gates: SpotBugs + PMD CPD
mvn clean verify

# Convention checks
./scripts/check-conventions.sh

# Smoke tests (requires app running on localhost:8080)
npx newman run "postman/Goods Price Comparison Service.postman_collection.json"

# SAST + dependency scan
mvn verify -P security-check
```

### 4. Commit Your Changes

```bash
git add .
git commit -m "type(scope): description"
```

See [Commit Message Guidelines](#commit-message-guidelines) for details.

### 5. Push and Create PR

```bash
git push -u origin feature/your-feature-name
```

Then create a Pull Request via GitHub UI or CLI:
```bash
gh pr create --title "feat: your feature title" --body "Description of changes"
```

## Pull Request Process

### Before Creating a PR

Ensure the following checks pass:

- [ ] `mvn spotless:apply` - Formatting is clean (Google Java Style)
- [ ] `mvn clean test` - All tests pass (0 failures, 1,041+ tests), ArchUnit (7 rules)
- [ ] `mvn clean verify` - Full quality gates pass (SpotBugs, PMD CPD, JaCoCo ≥90% INSTRUCTION / ≥80% BRANCH)
- [ ] `./scripts/check-conventions.sh` - Convention checks pass (no log injection, no JPA in domain)
- [ ] `npx newman run "postman/Goods Price Comparison Service.postman_collection.json"` - Smoke tests pass (requires app running on localhost:8080)
- [ ] `mvn verify -P security-check` - OWASP Dependency-Check passes (no CVSS >= 7)
- [ ] User-controlled values sanitized before log/exception output (Log Injection prevention)
- [ ] New code has unit tests (100% coverage for new code)
- [ ] Documentation updated (CHANGELOG.md, README.md, docs/ if applicable)
- [ ] CHANGELOG.md updated under `[Unreleased]`
- [ ] CI checks pass: Build & Test, CodeQL, Dependency Review (all visible on PR page)

### PR Requirements

Your PR must include:

1. **Clear title** following commit message format
2. **Description** explaining:
   - What changes were made
   - Why they were made
   - How to test them
3. **Reference to issues** (if applicable): `Fixes #123`

### PR Review Process

1. **Automated checks** must pass (CI/CD)
2. **Code review** by at least one maintainer
3. **Approval** required before merge
4. **No direct pushes to main** - PRs only

## Coding Standards

We follow the **Google Java Style Guide** with these specifics:

### Code Style

- **Indentation:** 4 spaces (no tabs)
- **Line length:** 100 characters (soft), 120 (hard)
- **Braces:** Opening brace on same line
- **Naming:**
  - Classes: `PascalCase`
  - Methods/variables: `camelCase`
  - Constants: `UPPER_SNAKE_CASE`

### Code Organization

```
src/main/java/com/example/goodsprice/
├── <service>/                  # 8 services: receipt, price, product, store, llm, shopping, alert, system
│   ├── application/           # Pure Java, no Spring/JPA
│   │   ├── domain/model/      # @Builder @Getter @Setter, zero JPA
│   │   ├── domain/service/    # Implements *InPort
│   │   ├── port/in/           # Driving ports (*InPort)
│   │   ├── port/out/          # Driven ports (*RepositoryPort, *EventOutPort)
│   │   └── exception/         # Domain exceptions
│   └── infrastructure/        # Adapters
│       ├── adapter/web/       # REST controllers, DTO mappers
│       ├── adapter/persistence/ # JPA entities, repositories, entity mappers
│       ├── adapter/event/     # Event publishers/listeners
│       └── handler/event/     # @Async @TransactionalEventListener handlers
├── common/                    # Shared code
│   ├── constant/              # AppConstants, ErrorCodes, ErrorMessageConstants
│   ├── dto/                   # PageRequestDto, PageResponse
│   ├── exception/             # NotFoundException (unified static factories)
│   ├── persistence/           # PaginationHelper
│   ├── repository/            # AbstractRepositoryAdapter, GenericRepositoryPort
│   ├── service/               # AbstractGenericService
│   └── util/                  # ObjectUtils, SpecificationBuilder, etc.
├── config/                    # Configuration classes
└── Application.java           # Main entry point
```

### JavaDoc Requirements

All public APIs must have JavaDoc:

```java
/**
 * Calculates the total price for a shopping cart.
 *
 * @param items the list of items in the cart
 * @param storeId the ID of the store
 * @return the total price after discounts
 * @throws IllegalArgumentException if items is null
 * @since 1.0.0
 */
public BigDecimal calculateTotal(List<CartItem> items, Long storeId) {
    // implementation
}
```

### Code Quality Rules

- **Method length:** Max 50 lines
- **Class length:** Max 500 lines
- **Method parameters:** Max 5
- **Cyclomatic complexity:** Max 10
- **Constructor injection only** (no field injection)

See [AGENTS.md §3 - Non-Negotiable Rules](AGENTS.md) for complete details.

## Testing Requirements

### Coverage Standards

| Code Type | Minimum | Target |
|-----------|---------|--------|
| Existing Code | 90% | 95% |
| **New Code** | **100%** | **100%** |
| Critical Path | 95% | 100% |

### Test Structure

Use **Given-When-Then** format:

```java
@Test
@DisplayName("Should calculate total price when quantity is positive")
void shouldCalculateTotalPrice_WhenQuantityIsPositive() {
    // Given
    int quantity = 10;
    BigDecimal unitPrice = new BigDecimal("100.00");
    
    // When
    BigDecimal total = calculator.calculate(quantity, unitPrice);
    
    // Then
    assertEquals(new BigDecimal("1000.00"), total);
}
```

### Test Categories

1. **Unit Tests** (80%) - Fast, isolated, mocked dependencies
2. **Integration Tests** (15%) - Database, API endpoints
3. **E2E Tests** (5%) - Full user flows

See [docs/DEVELOPER_GUIDE.md §Testing](docs/DEVELOPER_GUIDE.md#testing) for complete testing guide.

## Commit Message Guidelines

### Format

```
<type>(<scope>): <description>

<optional body>
```

**Types**: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `perf`, `style`

**Examples**:
```bash
feat(price): add cheapest price comparison endpoint
fix(receipt): handle null imageHash during create
refactor(store): extract buildSpecification to SpecificationBuilder
test(alert): add unit tests for AlertService
docs: update CHANGELOG with ActivityLog criteria impl
```

### Good Examples

```bash
# Simple and clear - tells exactly what was done
feat: add Store entity with JPA annotations

fix: null pointer exception in price calculation

docs: update README with local development instructions

refactor: remove deprecated OAuth configuration

test: add unit tests for Product repository
```

### Bad Examples

```bash
# Too vague - what was fixed?
Fix bug

# Too technical - what does this do?
Refactor XYZUtils

# Missing context - why?
Update code

# Not meaningful
Changes
```

### Tips for Good Messages

- **Start with a verb** (Add, Fix, Update, Remove, Refactor)
- **Describe what changed** in plain English
- **Keep it under 50 characters** for the first line
- **Add details in the body** if needed (blank line, then explanation)
- **Be specific** - "Fix price calculation rounding error" not "Fix bug"

### When You Need More Details

```bash
Add Flyway migrations for database schema

Created initial migration scripts for stores, products,
and price_records tables. Includes indexes for performance.

Fix connection timeout in HikariCP configuration

Increased connection timeout from 20s to 30s to handle
slow PostgreSQL startup in Docker environments.
```

### PR Messages

Same rules apply to PR titles - make them clear and descriptive:

**Good:**
- "Add price comparison endpoint with pagination"
- "Fix H2 database configuration for local development"
- "Update project dependencies to latest stable versions"

**Bad:**
- "Feature update"
- "Fix stuff"
- "Changes v2"

## Documentation

### When to Update Documentation

Update documentation when you:
- Add new features
- Change existing behavior
- Add new API endpoints
- Modify database schema
- Update build/deployment process

### Documentation Files

- **README.md** - Main project documentation
- **CHANGELOG.md** - Version history
- **AGENTS.md** - AI agent guide and conventions
- **PROJECT.md** - Project vision and scope
- **STATE.md** - Current focus and active decisions
- **docs/ARCHITECTURE_HYBRID.md** - System design
- **docs/DEVELOPER_GUIDE.md** - Developer guide
- **docs/USER_GUIDE.md** - User guide
- **docs/ERD.md** - Entity relationship diagram

## Questions?

If you have questions:

1. Check existing [documentation](docs/)
2. Open an issue for discussion
3. Contact maintainer: rizkifaizalr@gmail.com

## License

By contributing, you agree that your contributions will be licensed under the [MIT License](LICENSE).

---

**Thank you for contributing!**

*Remember: Quality over quantity. A well-tested, well-documented PR is better than a fast one.*
