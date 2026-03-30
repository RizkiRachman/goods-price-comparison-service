# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial Spring Boot project setup with Maven
- PostgreSQL database configuration with Flyway migrations
- H2 in-memory database for local development (`application-local.properties`)
- Application entry point (`Application.java`)
- Test configuration with JUnit 5 and Spring Boot Test
- Code quality tools: JaCoCo, Checkstyle, SpotBugs
- AI documentation structure with `.ai/` directory
  - `AGENTS.md` - AI agent guidelines
  - `rules/CODING_STANDARDS.md` - Coding standards
  - `rules/PR_WORKFLOW.md` - PR workflow
  - `skills/JAVA.md` - Java guidelines
  - `skills/TESTING.md` - Testing and coverage requirements
  - `context/PROJECT_OVERVIEW.md` - Project overview

### Changed
- Reorganized `.ai/` directory structure to match `goods-price-comparison-api` layout

### Fixed
- Fixed test configuration (`application-test.properties`) - removed invalid `spring.profiles.active`
- Fixed H2 database dependency scope (changed from `test` to `runtime`)
- Temporarily disabled JaCoCo coverage check for initial setup phase

## [0.1.0] - 2026-03-30

### Added
- Project initialization
- Basic Spring Boot 3.3.0 setup with Java 17
- Maven configuration with dependencies:
  - Spring Boot Starter Web
  - Spring Boot Starter Data JPA
  - Spring Boot Starter Validation
  - PostgreSQL driver
  - Flyway migration
  - H2 database (testing and local dev)
  - Lombok
  - JUnit 5 and Testcontainers
- Comprehensive README.md with:
  - Architecture flow diagrams
  - Tech stack documentation
  - Database schema (3 tables: stores, products, price_records)
  - Getting started guide
  - Configuration profiles (local vs production)
- Documentation structure in `docs/`:
  - `API.md` - API documentation
  - `ROADMAP.md` - 16-week development roadmap
  - `ARCHITECTURE.md` - System design
  - `DATABASE.md` - Schema and migrations
  - `DEPLOYMENT.md` - Docker/K8s guides
  - `TESTING.md` - Test strategy
- `.gitignore` with comprehensive exclusions

[Unreleased]: https://github.com/RizkiRachman/goods-price-comparison-service/compare/v0.1.0...HEAD
[0.1.0]: https://github.com/RizkiRachman/goods-price-comparison-service/releases/tag/v0.1.0
