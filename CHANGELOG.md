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
- CHANGELOG.md and CONTRIBUTING.md for project documentation
- Strict merge rules in AI documentation (explicit permission required)
- Added `goods-price-comparison-api` as Maven dependency for OpenAPI generated DTOs
- Created API controller skeletons implementing OpenAPI interfaces:
  - SystemController, ReceiptController, PriceController
  - ShoppingController, ProductController, AlertController
- Added comprehensive unit tests for all controllers (100% coverage)

### Changed
- Reorganized `.ai/` directory structure to match `goods-price-comparison-api` layout
- Simplified commit message guidelines to be meaningful and natural
- Updated Spring Boot from `3.3.0` to `3.4.4` (latest stable supporting Java 17)
- Updated PR workflow documentation to include application startup verification

### Fixed
- Fixed test configuration (`application-test.properties`) - removed invalid `spring.profiles.active`
- Fixed H2 database dependency scope (changed from `test` to `runtime`)
- Changed default database from PostgreSQL to H2 (no profile required to run)
- Reorganized configuration structure: parent `application.properties` imports modular configs from `config/` folder
- Cleaned up unused property files and simplified configuration structure

### Added
- LLM Provider Interface (`LLMProvider`) for agnostic LLM integration
- LLM Service (`LLMService`) for receipt data extraction
- Local LLM Provider implementation (`LocalLLMProvider`) for Ollama
- Configuration Properties (`LlmProperties`) with type-safe property binding
- LLM Configuration class (`LLMConfiguration`) for provider bean creation
- Modular configuration files in `config/` folder:
  - `database.properties` - H2 database configuration
  - `llm.properties` - LLM provider settings
  - `features.properties` - Feature flags
  - `logging.properties` - Logging configuration
  - `service.properties` - Service settings
- Documentation: `docs/CONFIGURATION.md` for configuration guide
- Unit tests for LLM configuration and service (4 new tests)
- Google Gemini LLM Provider (`GeminiLLMProvider`)
  - Uses official Google GenAI SDK (`com.google.genai:google-genai:1.0.0`)
  - Vision API support for receipt OCR
  - Free tier: 60 requests/minute
  - Default provider for reliable image processing
  - Supports gemini-1.5-flash-latest model
- Receipt Processing Service (`ReceiptService`)
  - Direct integration with Gemini LLM for receipt OCR
  - Image to base64 conversion
  - Structured data extraction (store, date, items, prices)
  - In-memory storage for processed receipts
  - Updated ReceiptController to use new service
- LLM Provider Type Configuration
  - Added `type` field to provider config (local vs cloud)
  - Provider type loaded from properties (not hardcoded)
  - Removed default value from LlmProperties.provider field
  - Properties file is now source of truth for provider selection
  - Fixed `core.llm.provider` to `llm.provider` in config
  - Updated tests to handle missing API keys gracefully
  - Added `isLocal()` and `isCloud()` helper methods

### Removed
- Deleted `application-local.properties` (no longer needed)
- Deleted `INTELLIJ_FIX.md` (resolved)
- Deleted example and test files for property references

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
