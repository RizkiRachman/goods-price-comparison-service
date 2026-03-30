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

- **Java 17+** installed
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
git checkout -b feature/your-feature-name
```

**Branch naming conventions:**
- `feature/description` - New features
- `fix/description` - Bug fixes
- `docs/description` - Documentation updates
- `refactor/description` - Code refactoring
- `test/description` - Test additions/improvements

### 2. Make Your Changes

- Write clean, maintainable code
- Follow the [Coding Standards](#coding-standards)
- Add tests for new functionality
- Update documentation as needed

### 3. Test Your Changes

Before submitting a PR, ensure all tests pass:

```bash
# Run all tests
mvn clean test

# Run with coverage
mvn clean verify

# Check code quality
mvn spotbugs:check
mvn checkstyle:check
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

- [ ] `mvn clean compile -q` - No compilation errors
- [ ] `mvn clean test` - All tests pass (0 failures)
- [ ] Code coverage ≥ 90% (100% for new code)
- [ ] `mvn spotbugs:check` - No SpotBugs violations
- [ ] `mvn checkstyle:check` - No Checkstyle violations
- [ ] Documentation updated (if applicable)
- [ ] CHANGELOG.md updated (if applicable)

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
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/             # Data transfer objects
├── exception/       # Custom exceptions
├── model/           # Entity classes
├── repository/      # JPA repositories
├── service/         # Business logic
│   ├── impl/       # Service implementations
│   └── mapper/     # Object mappers
└── util/           # Utility classes
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

See [`.ai/rules/CODING_STANDARDS.md`](.ai/rules/CODING_STANDARDS.md) for complete details.

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

See [`.ai/skills/TESTING.md`](.ai/skills/TESTING.md) for complete testing guide.

## Commit Message Guidelines

We follow [Conventional Commits](https://www.conventionalcommits.org/):

### Format

```
type(scope): description

[optional body]

[optional footer(s)]
```

### Types

- **feat:** New feature
- **fix:** Bug fix
- **docs:** Documentation changes
- **style:** Code style (formatting, no logic change)
- **refactor:** Code refactoring
- **test:** Test additions/changes
- **chore:** Build, CI, dependencies
- **ci:** CI/CD changes

### Examples

```bash
feat(prices): Add pagination to price search endpoint

fix(receipts): Handle null image data in OCR processing

docs(readme): Update API examples and add diagrams

test(entities): Add 100% coverage for Store entity

refactor(service): Extract price calculation logic

chore(deps): Update Spring Boot to 3.3.0
```

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
- **docs/API.md** - API documentation
- **docs/ARCHITECTURE.md** - System design
- **docs/DATABASE.md** - Schema documentation
- **docs/TESTING.md** - Testing guide
- **docs/DEPLOYMENT.md** - Deployment guide

## Questions?

If you have questions:

1. Check existing [documentation](docs/)
2. Check [AI guidelines](.ai/AGENTS.md)
3. Open an issue for discussion
4. Contact maintainer: rizkifaizalr@gmail.com

## License

By contributing, you agree that your contributions will be licensed under the [MIT License](LICENSE).

---

**Thank you for contributing!** 🎉

*Remember: Quality over quantity. A well-tested, well-documented PR is better than a fast one.*
