# Developer Guide

Complete guide for developers working on the Goods Price Comparison Service.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Development Setup](#development-setup)
- [Understanding the Architecture](#understanding-the-architecture)
- [Project Structure](#project-structure)
- [Development Workflow](#development-workflow)
- [Database Management](#database-management)
- [Testing](#testing)
- [Quality Gates](#quality-gates)
- [Adding New Features](#adding-new-features)
- [Configuration](#configuration)
- [Troubleshooting](#troubleshooting)

## Prerequisites

- **Java 17** or higher
- **Maven 3.8+**
- **Docker** (for PostgreSQL option)
- **Git**

## Development Setup

### Option 1: Quick Start (H2 Database)

Fastest setup for development - no external database needed.

```bash
# Clone the repository
git clone https://github.com/RizkiRachman/goods-price-comparison-service.git
cd goods-price-comparison-service

# Run quality checks
mvn spotless:apply
mvn verify

# Start with H2 in-memory database
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Access points:**
- Application: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:goodspricedb`)
- Swagger UI: http://localhost:8080/swagger-ui.html

### Option 2: PostgreSQL Setup (Production-like)

For testing with real database:

```bash
# 1. Start PostgreSQL via Docker
docker run -d --name goodsprice-db \
  -e POSTGRES_DB=goodsprice \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 postgres:16

# 2. Run migrations
mvn flyway:migrate -Pflyway \
  -Dflyway.url=jdbc:postgresql://localhost:5432/goodsprice \
  -Dflyway.user=postgres \
  -Dflyway.password=password

# 3. Set environment and run
export DATABASE_HOST=localhost
export DATABASE_PORT=5432
export DATABASE_NAME=goodsprice
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=password

mvn spring-boot:run
```

## Understanding the Architecture

This project uses a **three-layer hybrid architecture**:

### Layer 1: Microservice Boundaries

Eight bounded contexts (currently packages, designed for future services):
- `receipt` - Receipt upload and processing
- `price` - Price records and comparisons
- `product` - Product catalog
- `store` - Store directory
- `llm` - AI/OCR integration
- `shopping` - Shopping optimization
- `alert` - Price alerts
- `system` - System utilities

### Layer 2: Hexagonal Architecture

Each service follows ports & adapters pattern:

```
service/
├── application/           # Pure Java, no framework dependencies
│   ├── domain/model/      # Business entities
│   ├── domain/service/    # Business logic
│   ├── port/in/           # Driving ports (what the service CAN do)
│   └── port/out/          # Driven ports (what the service NEEDS)
└── infrastructure/        # Framework code
    ├── adapter/web/       # REST controllers
    ├── adapter/persistence/  # Database access
    └── handler/event/     # Event consumers
```

### Layer 3: Event-Driven Communication

Services communicate via events:
- Spring `ApplicationEvent` (current - in-process)
- Future: Kafka/RabbitMQ for distributed deployment

See [Architecture Overview](ARCHITECTURE_HYBRID.md) for full details.

## Project Structure

```
src/main/java/com/example/goodsprice/
├── common/                    # Shared utilities
│   ├── constant/             # AppConstants, ErrorCodes
│   ├── exception/            # Custom exceptions
│   └── util/                 # ObjectUtils, NumberUtils, JsonUtils
│
├── receipt/                   # Receipt service
│   ├── application/
│   │   ├── domain/model/     # Receipt, ReceiptItem, ReceiptStatus
│   │   ├── domain/service/   # ReceiptService
│   │   ├── port/in/          # ReceiptInPort
│   │   └── port/out/         # ReceiptRepositoryPort, ReceiptEventOutPort
│   └── infrastructure/
│       ├── adapter/web/      # ReceiptController, ReceiptWebAdapter
│       ├── adapter/persistence/  # ReceiptEntity, ReceiptRepositoryAdapter
│       └── handler/event/    # ReceiptProcessEventListener
│
├── price/                     # Price service (similar structure)
├── product/                   # Product service
├── store/                     # Store service
├── llm/                       # LLM integration
├── shopping/                  # Shopping optimization
└── alert/                     # Alert service
```

## Development Workflow

### 1. Making Changes

```bash
# Create feature branch
git checkout -b feature/your-feature-name

# Make your changes
# ... edit files ...

# Format code
mvn spotless:apply

# Run tests
mvn test

# Full verification
mvn verify
```

### 2. Code Style

- **Google Java Style** (enforced by Spotless)
- Use `mvn spotless:apply` to auto-format
- Follow existing patterns in the codebase

### 3. Writing Code

**Domain models:**
```java
@Builder
@Getter
@Setter
public class Product {
    private Long id;
    private String name;
    private String category;
}
```

**Ports return nullable (not Optional):**
```java
// Good
Product findById(Long id);  // returns null if not found

// Avoid
Optional<Product> findById(Long id);
```

**Use utilities:**
```java
ObjectUtils.getOrNull(obj, Parent::getChild)
ValidationUtils.requireNonNull(value, "fieldName")
NumberUtils.toDouble(obj)
```

## Database Management

### Migration Structure

```
db/migration/
├── tables/          # V1-V5: CREATE TABLE
├── alter/           # V6+: ALTER TABLE
└── data/            # Reference data
```

### Creating Migrations

```bash
# 1. Check latest version
ls db/migration/tables/ db/migration/alter/

# 2. Create new file
touch db/migration/alter/V6__add_column_to_products.sql

# 3. Write PostgreSQL-compatible SQL
# 4. Test: mvn flyway:migrate -Pflyway
```

### Naming Convention

- `V{version}__{description}.sql` (note: double underscore)
- Example: `V6__add_unit_column_to_products.sql`

## Testing

### Test Types

| Type | Location | Purpose |
|------|----------|---------|
| Unit | `application/domain/service/*Test` | Test business logic with mocked ports |
| Integration | `infrastructure/adapter/persistence/*Test` | Test database layer |
| Controller | `infrastructure/adapter/web/*Test` | Test REST endpoints |
| Architecture | `architecture/HexagonalArchitectureTest` | Enforce architecture rules |

### Running Tests

```bash
# Unit tests only
mvn test

# All tests including integration
mvn verify -Pintegration-tests

# Single test
mvn test -Dtest=ReceiptServiceTest
```

### Test Configuration

Tests use `@ActiveProfiles("test")` with:
- H2 in-memory database
- Flyway disabled
- LLM provider set to `local` (no API key needed)

## Quality Gates

Every commit must pass:

| Check | Command | What It Finds |
|-------|---------|---------------|
| Tests | `mvn test` | Broken logic |
| ArchUnit | (part of test) | Architecture violations |
| Spotless | `mvn spotless:check` | Formatting issues |
| SpotBugs | `mvn spotbugs:check` | Null bugs, encoding issues |
| PMD CPD | `mvn pmd:cpd-check` | Code duplication (>100 tokens) |

```bash
# Run all checks
mvn verify

# Fix formatting
mvn spotless:apply

# Extra conventions check
./scripts/check-conventions.sh
```

## Adding New Features

### Step-by-Step Guide

1. **Create ports first** (`application/port/in/` and `application/port/out/`)
2. **Create domain model** (`application/domain/model/`)
3. **Implement domain service** (`application/domain/service/`)
4. **Create mapper** (domain ↔ entity, domain ↔ DTO)
5. **Create adapter** (web, persistence, or event)

### Example: Adding a New Endpoint

```java
// 1. Port (application/port/in/ProductInPort.java)
public interface ProductInPort {
    Product createProduct(CreateProductRequest request);
    Product getProduct(Long id);
}

// 2. Domain Service (application/domain/service/ProductService.java)
@Service
@RequiredArgsConstructor
public class ProductService implements ProductInPort {
    private final ProductRepositoryPort repositoryPort;
    
    @Override
    public Product createProduct(CreateProductRequest request) {
        // Business logic here
        Product product = Product.builder()
            .name(request.getName())
            .build();
        return repositoryPort.save(product);
    }
}

// 3. Web Adapter (infrastructure/adapter/web/ProductController.java)
@RestController
@RequiredArgsConstructor
public class ProductController implements ProductsApi {
    private final ProductInPort productInPort;
    private final ProductDtoMapper mapper;
    
    @Override
    public ResponseEntity<ProductDto> createProduct(CreateProductRequest request) {
        Product product = productInPort.createProduct(request);
        return ResponseEntity.ok(mapper.toDto(product));
    }
}
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_HOST` | PostgreSQL host | `localhost` |
| `DATABASE_PORT` | PostgreSQL port | `5432` |
| `DATABASE_NAME` | Database name | `goodsprice` |
| `DATABASE_USERNAME` | Database user | `postgres` |
| `DATABASE_PASSWORD` | Database password | *(empty)* |
| `LLM_PROVIDER` | AI provider | `local` |
| `GEMINI_API_KEY` | Google Gemini key | *(none)* |
| `GROQ_API_KEY` | Groq AI key | *(none)* |

### Profiles

- `local` - H2 database, auto schema update
- `default` - PostgreSQL, Flyway migrations
- `test` - H2, disabled Flyway, mocked LLM

## Troubleshooting

### Common Issues

**Build fails with formatting errors:**
```bash
mvn spotless:apply
```

**Database connection errors:**
- Check PostgreSQL is running: `docker ps`
- Verify credentials in environment variables
- Run migrations: `mvn flyway:migrate -Pflyway`

**ArchUnit test failures:**
- Check for `application/` importing `infrastructure/`
- Ensure domain models don't have JPA annotations
- Verify ports don't return `Optional<T>`

**LLM processing fails:**
- Check `LLM_PROVIDER` is set correctly
- For `local`: ensure Ollama is running
- For `gemini`/`groq`: verify API keys

### Release Process

To ship a release, load the release-plan skill:

```bash
# In opencode session
/skill release-plan
```

The workflow covers:
1. **Planning** — impact analysis, architecture alignment
2. **Development** — port → service → mapper → adapter → tests
3. **Quality Gates** — `spotless:apply` → `test` → `verify` → `check-conventions.sh`
4. **Documentation** — CHANGELOG, README, docs/, AGENTS.md
5. **Pull Request** — template with quality checklist
6. **Release Review** — pre-merge checks, post-merge tagging

See `.opencode/skills/release-plan/SKILL.md` for the complete checklist.

### Getting Help

- Check [Architecture Overview](ARCHITECTURE_HYBRID.md) for design decisions
- Check [Database ERD](ERD.md) for schema information
- Review existing services as examples
- See [Contributing](../CONTRIBUTING.md) for PR process
