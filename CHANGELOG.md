# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- **Rate limiting**: Token-bucket rate limiter using Bucket4j with per-client-IP enforcement, configurable limits, and `@RateLimit` annotation for per-endpoint overrides
  - `config/ratelimit/` — `RateLimitInterceptor`, `RateLimitBucketStore`, `RateLimitProperties`, `RateLimitExceededException`, `RateLimit` annotation
  - `config/RateLimitConfiguration.java` — WebMvcConfigurer registering the interceptor
  - `common/constant/HttpHeaderConstants.java` — `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `Retry-After` headers
  - `GlobalExceptionHandler` enhanced with `handleRateLimitExceeded()` returning `429 Too Many Requests` with rate limit headers
  - `config/rate-limiter.properties` — default 60 requests per 60-second window, per-endpoint overrides supported
  - `ErrorCodes.RATE_LIMIT_EXCEEDED` added
  - `bucket4j-core:8.10.1` dependency added to `pom.xml`

### Added
- **Batch query support**: `PriceInPort.findCheapestByProducts()` and `ProductInPort.findAllByNames()` to avoid N+1 query problems
- **Shopping optimizer extraction**: `ShoppingOptimizer` extracted from `ShoppingService` with `ShoppingContext` for clean pipeline-based route optimization
- **Code quality tooling**: Spotless (Google Java Format), Checkstyle, PMD, SpotBugs with FindSecBugs plugins wired into `verify` lifecycle
- **SAST security scan**: OWASP Dependency-Check profile (`mvn verify -P security-check`) for CVE detection in dependencies (fails on CVSS >= 7)
- **Pipeline utility**: `common/util/Pipeline.java` for functional pipeline pattern
- **Serial version UIDs**: Added `serialVersionUID` to `PriceNotFoundException`, `ProductNotFoundException`, `DuplicateReceiptException`, `ReceiptNotFoundException`, and `StoreNotFoundException` for serialization safety
- **Checkstyle `@SuppressWarnings` support**: Added `SuppressWarningsFilter` and `SuppressWarningsHolder` to Checkstyle config for per-method suppression

### Changed
- **API spec version**: Updated `goods-price-comparison-api` from `1.5.1` to `1.6.0`
- **Thread safety**: Replaced `HashMap` with `ConcurrentHashMap` in `JobRegistry` and `ShoppingOptimizer` for safe concurrent access
- **LlmService**: Added missing `@Override` annotations on `getCurrentProvider()` and `isAvailable()`; simplified hex encoding with `String.format("%02x", b)`; extracted `HEX_PAD_LENGTH` constant
- **LenientOffsetDateTimeDeserializer**: Added `@Slf4j` logging instead of empty catch block; preserved cause exception in thrown `IOException` for better debugging
- **Wildcard imports**: Replaced star imports with explicit imports in `ProductController`, `ProductWebAdapter`, `HexagonalArchitectureTest`, and all affected test files (Checkstyle compliance)
- **ProductDtoMapperTest**: Added `@SuppressWarnings("checkstyle:MethodName")` annotation
- **SpotBugs exclusion**: Broadened `EI_EXPOSE_REP` exclusion from shopping-only domain models to all domain models (consistent policy)
- **Price calculation job**: `product-prices-calculate` job registered via `ProductPricesCalculateJob` for on-demand price summary recalculation
- **Receipt correction event**: `ReceiptCorrectedEvent` fired after correction submission with two independent async handlers:
  - `ReceiptCorrectedEventHandler`: updates products and prices from corrected receipt items
  - `ReceiptCorrectedPriceCalcHandler`: triggers price summary recalculation
- **Receipt correction flow**: Full correction pipeline with `ReceiptCorrectionInPort`, `ReceiptCorrectionService`, `ReceiptCorrectionWebAdapter`, and `ReceiptDtoMapper.toResultResponse()`
- **Receipt correction API models**: Request changed from `ReceiptCorrectionRequest` → `ReceiptCorrectRequest`, response changed from `ReceiptCorrectionResponse` → `ReceiptResultResponse` (aligns with API v1.5.0)
- **Receipt correction response**: Now returns full receipt data (store name, location, date, items, total amount) via `ReceiptCorrectionWebAdapter` instead of a skeleton response
- **ReceiptDtoMapper**: Extracted `toResultResponse()` mapper method, refactored `ReceiptWebAdapter.getResult()` to use it (DRY)

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
- **Sumopod LLM provider**: `SumopodLlmProvider` as OpenAI-compatible provider with configurable base URL, model, timeout, and API key; `SUMOPOD` added to `LlmProviderType` enum; provider registration in `LlmProviderConfiguration`
- **Groq constants generalized**: `GROQ_*` constants renamed to `GENERAL_*` for multi-provider reuse; `GroqLlmProvider` updated to use generalized constants
- **Provider enable/disable flags**: Per-provider `enabled` property in `llm.properties` (e.g., `llm.sumopod.enabled=true`)
- **Receipt event system**: `ReceiptUploadedEvent`, `ReceiptProcessedEvent`, `ReceiptApprovedEvent` with corresponding async handlers
- **Receipt approve processor executor**: Second thread pool (`receiptApproveProcessorExecutor`) for receipt approval processing
- **ArchUnit tests**: `HexagonalArchitectureTest` with 7 rules enforcing hexagonal layer boundaries, port contracts, and JPA-free domains
- **Price tests**: `PriceSummaryBatchServiceTest`, `PriceSummaryMapperTest`, `PriceSummaryRepositoryAdapterTest`
- **Product DTO mapper test**: `ProductDtoMapperTest`
- **Product name utils test**: `ProductNameUtilsTest`
- **Release plan skill**: `.opencode/skills/release-plan/SKILL.md` — end-to-end release workflow
- **PR template**: `.github/PULL_REQUEST_TEMPLATE.md` with structured summary, type of change, and quality checklist
- **Unit constants**: `UnitConstants.isWeight()` centralizes weight/volume unit detection across price and shopping modules
- **Batch price query by product IDs**: `PriceInPort.findAllByProductIds()` + `PriceRepositoryPort.findAllByProductIds()` + `JpaPriceRepository.findAllByProductIds()` for multi-product price lookups
- **Shopping optimizer tests**: `ShoppingOptimizerTest` with 11 test cases covering null inputs, weight-based pricing, non-weight pricing, null/zero price exclusion, mixed products, same-store grouping, missing store, and missing product scenarios
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
- **Shopping optimizer unit-aware pricing**: `ShoppingOptimizer` now selects best price using `unitPrice` for weight-based products and `price` for non-weight products via `UnitConstants.isWeight()`
- **Shopping optimizer stream refactor**: Converted all imperative loops to Java 17 streams; extracted `findBestPrice()`, `isValidPriceValue()`, and `effectivePriceValue()` helper methods for testability
- **Price summary batch service**: Replaced inline weight-unit set with `UnitConstants.isWeight()`; removed redundant Javadoc comments

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
