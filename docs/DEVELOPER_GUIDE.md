<a id="readme-top"></a>

[![Java 21][java-shield]][java-url]
[![Spring Boot][spring-shield]][spring-url]
[![Maven][maven-shield]][maven-url]
[![PostgreSQL][postgres-shield]][postgres-url]
[![Docker][docker-shield]][docker-url]

<br />

<div align="center">
  <h3 align="center">Goods Price Comparison Service — Developer Guide</h3>
  <p align="center">
    Everything you need to develop, test, and contribute to the project.
    <br />
    <a href="ARCHITECTURE_HYBRID.md"><strong>Read the Architecture Docs »</strong></a>
  </p>
</div>

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#project-structure">Project Structure</a></li>
    <li><a href="#development-workflow">Development Workflow</a></li>
    <li><a href="#writing-conventions">Writing Conventions</a></li>
    <li><a href="#database">Database</a></li>
    <li><a href="#testing">Testing</a></li>
    <li><a href="#quality-gates">Quality Gates</a></li>
    <li><a href="#adding-new-features">Adding New Features</a></li>
    <li><a href="#configuration">Configuration</a></li>
    <li><a href="#troubleshooting">Troubleshooting</a></li>
  </ol>
</details>

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker (for PostgreSQL option)
- Git

### Quick Start (H2)

```bash
git clone https://github.com/RizkiRachman/goods-price-comparison-service.git
cd goods-price-comparison-service

mvn spotless:apply
mvn verify
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

| Access | URL |
|--------|-----|
| Application | http://localhost:8080 |
| H2 Console | http://localhost:8080/h2-console |
| Swagger UI | http://localhost:8080/swagger-ui.html |

H2 JDBC URL: `jdbc:h2:mem:goodspricedb`

### PostgreSQL Setup

```bash
docker run -d --name goodsprice-db \
  -e POSTGRES_DB=goodsprice \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password \
  -p 5432:5432 postgres:16

mvn flyway:migrate -Pflyway \
  -Dflyway.url=jdbc:postgresql://localhost:5432/goodsprice \
  -Dflyway.user=postgres \
  -Dflyway.password=password

export DATABASE_HOST=localhost DATABASE_PORT=5432
export DATABASE_NAME=goodsprice DATABASE_USERNAME=postgres DATABASE_PASSWORD=password
mvn spring-boot:run
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Project Structure

```
src/main/java/com/example/goodsprice/
├── common/                    # Shared utilities, constants, events
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
├── price/                     # Price service
├── product/                   # Product service
├── store/                     # Store service
├── llm/                       # LLM integration
├── shopping/                  # Shopping optimization
└── alert/                     # Alert service
```

Each service follows the hexagonal pattern: **ports → domain → adapters**. See the [Architecture Overview](ARCHITECTURE_HYBRID.md) for design rationale.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Development Workflow

### Branching

```bash
git checkout -b feature/YYYYMMDD-short-description
```

- **ALWAYS** create a new feature branch before starting work. Never commit directly to `main`.
- **Branch naming:** `feature/YYYYMMDD-<short-description>` for features, `bugfix/YYYYMMDD-<short-description>` for bug fixes.
- Push the new branch to the remote before starting significant work.

### Committing

```bash
type(scope): brief description
```

Types: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `perf`, `style`.

Changelog is auto-generated from conventional commits via [changelogen](https://github.com/unjs/changelogen).

### Code Style

- **Google Java Style** (enforced by Spotless)
- Run `mvn spotless:apply` to auto-format before committing
- Follow existing patterns in the codebase

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Writing Conventions

### Domain Models

```java
@Builder @Getter @Setter          // No JPA annotations
public class Product {
    private Long id;
    private String name;
    private String category;
}
```

### Ports

Return **nullable**, never `Optional<T>`:

```java
// Good
Product findById(Long id);

// Avoid
Optional<Product> findById(Long id);
```

### Null Handling

Use project utilities instead of raw checks:

```java
ObjectUtils.getOrNull(obj, Parent::getChild)
ValidationUtils.requireNonNull(value, "fieldName")
NumberUtils.toDouble(obj)
StringUtils.isBlank(str)
```

### Modern Java 17

```java
String.formatted()          // over String.format()
fooList.toList()            // over .collect(Collectors.toList())
Foo::bar                    // over x -> x.bar()
instanceof Foo f            // pattern matching
case X ->                   // switch expressions
var                         // where type is obvious
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Database

### Migration Structure

```
db/migration/
├── tables/          # V1-V5: CREATE TABLE
├── alter/           # V6+: ALTER TABLE
└── data/            # Reference data
```

### Creating Migrations

```bash
ls db/migration/tables/ db/migration/alter/
touch db/migration/alter/V6__add_column_to_products.sql
mvn flyway:migrate -Pflyway
```

**Naming:** `V{version}__{description}.sql` (double underscore). Example: `V6__add_unit_column_to_products.sql`.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Testing

### Test Types

| Type | Location | What It Tests |
|------|----------|---------------|
| Unit | `application/domain/service/*Test` | Business logic with mocked ports |
| Integration | `infrastructure/adapter/persistence/*Test` | Database layer |
| Controller | `infrastructure/adapter/web/*Test` | REST endpoints |
| Architecture | `architecture/HexagonalArchitectureTest` | ArchUnit rules |

### Commands

```bash
mvn test                           # Unit tests only
mvn verify                         # Full verification
mvn test -Dtest=ReceiptServiceTest # Single test
```

Tests use `@ActiveProfiles("test")` — H2 in-memory, Flyway disabled, LLM set to `local`.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Quality Gates

Run these in order. Each must pass before the next.

| Gate | Command | What It Catches |
|------|---------|-----------------|
| Formatting | `mvn spotless:apply` | Google Java Style violations |
| Architecture | `mvn test` (ArchUnit) | Package dependency violations |
| Static Analysis | `mvn verify` | SpotBugs bugs, PMD CPD duplicates |
| Coverage | `mvn verify` (JaCoCo) | ≥90% INSTRUCTION / ≥80% BRANCH |
| Conventions | `./scripts/check-conventions.sh` | getOrNull usage, method refs, no JPA in domain |
| Full Test | `mvn test && mvn verify` | All tests pass, 0 failures |
| Security | `mvn verify -P security-check` | OWASP dependency vulnerabilities |
| Smoke Tests | `npx newman run "postman/Goods Price Comparison Service.postman_collection.json"` | API endpoint integration (requires app running on localhost:8080) |

```bash
mvn spotless:apply              # Quick fix
mvn verify                      # Standard quality
mvn verify -P security-check    # Full + security
# Smoke tests (start app first):
mvn spring-boot:run -Dspring-boot.run.profiles=local
npx newman run "postman/Goods Price Comparison Service.postman_collection.json"
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Adding New Features

Follow this order to maintain hexagonal integrity:

### 1. Port Interface

```java
// application/port/in/ProductInPort.java
public interface ProductInPort {
    Product createProduct(CreateProductRequest request);
    Product getProduct(Long id);
}
```

### 2. Domain Service

```java
// application/domain/service/ProductService.java
@Service
@RequiredArgsConstructor
public class ProductService implements ProductInPort {
    private final ProductRepositoryPort repositoryPort;

    @Override
    public Product createProduct(CreateProductRequest request) {
        return repositoryPort.save(
            Product.builder()
                .name(request.getName())
                .category(request.getCategory())
                .build()
        );
    }
}
```

### 3. Web Adapter

```java
// infrastructure/adapter/web/ProductController.java
@RestController
@RequiredArgsConstructor
public class ProductController implements ProductsApi {
    private final ProductInPort productInPort;
    private final ProductDtoMapper mapper;

    @Override
    public ResponseEntity<ProductDto> createProduct(CreateProductRequest request) {
        return ResponseEntity.ok(
            mapper.toDto(productInPort.createProduct(request))
        );
    }
}
```

### 4. Persistence Adapter

```java
// infrastructure/adapter/persistence/entity/ProductEntity.java
@Entity @Table(name = "products")
public class ProductEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String category;
}

// infrastructure/adapter/persistence/ProductRepositoryAdapter.java
@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepositoryPort {
    private final JpaProductRepository jpaRepository;
    private final ProductMapper mapper;

    @Override
    public Product save(Product product) {
        return mapper.toDomain(
            jpaRepository.save(mapper.toEntity(product))
        );
    }
}
```

### Writing Order

```
1. Port interface (*InPort, *RepositoryPort)
2. Domain service (implements *InPort)
3. Mapper (domain ↔ DTO, domain ↔ entity)
4. Adapter (orchestration via ports + mappers)
5. Constants (AppConstants, ErrorCodes, ErrorMessageConstants)
6. Events (*EventOutPort, *EventAdapter, handler)
7. Tests (unit + ArchUnit compliance)
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Configuration

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

### Spring Profiles

| Profile | Database | Flyway | Use Case |
|---------|----------|--------|----------|
| `local` | H2 | Disabled | Local development |
| `default` | PostgreSQL | Enabled | Production |
| `test` | H2 | Disabled | Tests |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Troubleshooting

**Formatting errors:**
```bash
mvn spotless:apply
```

**Database connection errors:**
```bash
docker ps                              # Check PostgreSQL is running
mvn flyway:migrate -Pflyway            # Run migrations
```

**ArchUnit failures:**
- `application/` importing `infrastructure/`
- Domain models with JPA annotations
- Ports returning `Optional<T>`

**LLM errors:**
- Verify `LLM_PROVIDER` setting
- `local` requires Ollama running
- `gemini`/`groq` require valid API keys

**Need more help?** See [ARCHITECTURE_HYBRID.md](ARCHITECTURE_HYBRID.md), [ERD.md](ERD.md), or [CONTRIBUTING.md](../CONTRIBUTING.md).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

[java-shield]: https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[java-url]: https://www.oracle.com/java/
[spring-shield]: https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[spring-url]: https://spring.io/projects/spring-boot
[maven-shield]: https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white
[maven-url]: https://maven.apache.org/
[postgres-shield]: https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white
[postgres-url]: https://www.postgresql.org/
[docker-shield]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[docker-url]: https://www.docker.com/
