# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- **Hexagonal architecture restructuring**: Each service (`receipt`, `price`, `product`, `store`, `llm`, `shopping`, `alert`, `system`) now follows ports-and-adapters pattern with strict layer separation
  - `application/` — pure Java domain services, ports, and domain models (no Spring/JPA)
  - `infrastructure/adapter/` — web controllers, persistence JPA, event adapters
  - `infrastructure/handler/` — async event handlers via `@Async` + `@TransactionalEventListener(AFTER_COMMIT)`
- **Common utilities**: `common/` package with `ObjectUtils`, `JsonUtils`, `NumberUtils`, `HashUtils`, `ValidationUtils`, `PaginationUtils`, `SortingUtils`, `ProductNameUtils`
- **Common constants**: `common/constant/` with `AppConstants`, `ErrorCodes`, `ErrorMessageConstants`, `EntityConstants`, `ParamConstants`, `SortConstants`
- **Common DTOs**: `PageRequest`, `PageResponse` for paginated APIs
- **Global exception handler**: `GlobalExceptionHandler` with structured error responses for domain exceptions
- **Jackson configuration**: `JacksonConfiguration` with `JsonNullableModule` and `JavaTimeModule` for OpenAPI compatibility
- **Price summary batch service**: `PriceSummaryBatchService` with `@Scheduled` batch job for computing product price aggregates (avg, min, max)
- **Price summary scheduler**: `PriceSummaryBatchScheduler` with configurable cron expression
- **Product price query service**: `ProductPriceQueryService` for efficient product+price queries with pagination
- **Product comparators**: `ProductComparators` for flexible product sorting
- **Store lookup service**: `StoreLookupService` for cross-service store resolution
- **LLM provider configuration**: `LlmProviderConfiguration` with dynamic provider selection (`local`, `gemini`, `groq`)
- **Groq LLM provider**: `GroqLlmProvider` for Groq API integration
- **LLM constants**: `LlmConstants` with JSON keys, provider names, and MIME types
- **Receipt event system**: `ReceiptUploadedEvent`, `ReceiptProcessedEvent`, `ReceiptApprovedEvent` with corresponding async handlers
- **Receipt approve processor executor**: Second thread pool (`receiptApproveProcessorExecutor`) for receipt approval processing
- **ArchUnit tests**: `HexagonalArchitectureTest` with 7 rules enforcing hexagonal layer boundaries, port contracts, and JPA-free domains
- **Price tests**: `PriceSummaryBatchServiceTest`, `PriceSummaryMapperTest`, `PriceSummaryRepositoryAdapterTest`
- **Product DTO mapper test**: `ProductDtoMapperTest`
- **Product name utils test**: `ProductNameUtilsTest`
- **Release plan skill**: `.opencode/skills/release-plan/SKILL.md` — end-to-end release workflow
- **PR template**: `.github/PULL_REQUEST_TEMPLATE.md` with structured summary, type of change, and quality checklist
- **Knowledge skills**: `.opencode/skills/` — `java-developer`, `qa-expert`, `system-analyst`, `business-analyst`, `devops-expert`, `security-expert`, `front-end-expert`, `software-developer`, `token-optimize`
- **Custom agents**: `.opencode/agents/` — `development` (five-lens orchestrator), `security-performance` (read-only auditor)
- **Documentation**: `docs/ARCHITECTURE_HYBRID.md`, `docs/DEVELOPER_GUIDE.md`, `docs/ERD.md`, `docs/USER_GUIDE.md`, `docs/PROMPT_DRAFT.md`
- **Planning docs**: `planning/PRODUCT_PRICE_AGGREGATION_MASTER_PLAN.md` and supporting strategy documents
- **Migration scripts**: `V8__create_product_price_summaries_table.sql`, `V6`-`V9` alter scripts
- **Convention checker script**: `scripts/check-conventions.sh` with 7 convention checks
- **SpotBugs exclusion config**: `config/spotbugs/exclude.xml`

### Changed

- **Package restructuring**: Moved from flat `module/` package structure to full hexagonal architecture per service
  - Old `module/receipt/` → `receipt/application/` + `receipt/infrastructure/`
  - Old `module/price/` → `price/application/` + `price/infrastructure/`
  - Old `module/product/` → `product/application/` + `product/infrastructure/`
  - Old `module/store/` → `store/application/` + `store/infrastructure/`
  - Old `module/llm/` → `llm/application/` + `llm/infrastructure/`
  - Old `controller/` controllers → each service's `infrastructure/adapter/web/`
- **Application.java**: Updated `LlmProperties` import from `module.llm.config` to `llm.infrastructure.config`
- **API spec version**: Updated from `1.2.3` to `1.4.4` for new DTOs (`ProductDetail`, `ListProducts200Response`, `EntityStatus`, etc.)
- **AsyncConfiguration**: Split into `receiptProcessorExecutor` (core=3, max=10) and `receiptApproveProcessorExecutor` (core=2, max=5)
- **`.gitignore` expanded**: Consolidated AI agent ignores under organized sections

### Removed

- Removed `controller/` package — controllers now live in respective service packages
- Removed `module/llm/` — replaced by `llm/` hexagonal service
- Removed `module/price/` — replaced by `price/` hexagonal service
- Removed `module/product/` — replaced by `product/` hexagonal service
- Removed `module/receipt/` — replaced by `receipt/` hexagonal service
- Removed `module/store/` — replaced by `store/` hexagonal service
- Removed old test files at `module/llm/` and `controller/` — replaced by service-specific tests

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
