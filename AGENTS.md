# Goods Price Comparison Service — Agent Guide

Java 17 + Spring Boot 3.4. Hexagonal architecture per service, event-driven between services.

## Boundaries

- **This project only.** Never read, write, or access files outside this repository.
- Never browse, clone, inspect, or modify other projects, directories, or repositories without explicit user permission.
- Never push directly to any branch. Always create a pull request.
- Never force push, rewrite git history, or delete tags.
- Never delete files or directories without user confirmation.
- Never modify CI/CD workflows, git config, or GitHub settings without asking.
- Never commit secrets, credentials, tokens, or `.env` files.
- If a task references something outside this repo, stop and ask.

## Build & Verify

```bash
mvn verify                  # Gate: tests + ArchUnit + Spotless + SpotBugs + PMD CPD
mvn test                    # Tests + ArchUnit only
mvn spotless:apply          # Fix formatting before verify (Google Java Style)
scripts/check-conventions.sh # Extra: getOrNull, method refs, no JPA in domain
mvn spring-boot:run         # Start on port 8080
```

- `mvn verify` **fails** if SpotBugs finds bugs (`failOnError: true`). Suppressions in `config/spotbugs/exclude.xml`.
- PMD CPD min 100 tokens. Fix dupes before verify passes.
- JaCoCo coverage check (90%) is commented out in pom.xml.
- No CI workflows configured yet.

## Architecture

Full reference: [`docs/ARCHITECTURE_HYBRID.md`](docs/ARCHITECTURE_HYBRID.md) — three-layer hybrid: microservices boundaries, hexagonal internals, event-driven communication.

**Current project**: single-module Maven (`pom.xml` at root). Services are packages under `com.example.goodsprice`, not separate modules as described in the doc. The doc describes both current state and future distributed target.

### Layer 1 — Microservice Boundaries
Eight services under `com.example.goodsprice`: `receipt`, `price`, `product`, `store`, `llm`, `shopping`, `alert`, `system`. Each owns its tables. No service queries another service's tables directly.

### Layer 2 — Hexagonal (per service)
```
Each service: application/ (pure Java, no Spring/JPA) + infrastructure/ (adapters)
Ports: *InPort (driving), *RepositoryPort (driven), *EventOutPort (events)
```

### Layer 3 — Event-Driven
Services communicate via Spring `ApplicationEvent` + `@Async` + `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`. Events are fired after transaction commit only. The event adapter pattern (`*EventOutPort` → `ApplicationEventPublisher`) makes future Kafka migration transparent.

## Non-Negotiable Rules (Enforced by ArchUnit)

- `application/` MUST NOT import `infrastructure/`
- Domain models: `@Builder @Getter @Setter`, zero JPA annotations
- Ports return **nullable**, never `Optional<T>`. Unwrap Spring Data Optional at adapter boundary.
- JPA entities: NO `@ManyToOne`, `@OneToMany`, `@OneToOne`, `@ManyToMany`, `@JoinColumn`. FK columns are primitives: `Long storeId`, `UUID receiptId`.
- UUID `@Id`: `@GeneratedValue(strategy = GenerationType.UUID)`. Long `@Id`: `IDENTITY`.

## Null Handling

Use project utilities in `common/util/`:

```java
Objects.isNull(x)                    // x == null
ObjectUtils.getOrNull(obj, X::getY) // obj != null ? obj.getY() : null
ObjectUtils.getOrDefault(obj, X::getY, fallback)
ValidationUtils.requireNonNull(x, "name")
NumberUtils.toDouble(obj)           // safe instanceof + parse
JsonUtils.extractItems(json)        // parse "items" array from LLM response
HashUtils.sha256(bytes)             // Base64-encoded SHA-256
```

## Mapping Convention

Domain → API DTO goes in `*/web/mapper/*DtoMapper.java`.
Domain ↔ Entity goes in `*/persistence/*Mapper.java`.
Adapters orchestrate; mappers do the conversion. Never map in adapters.

## Data Shapes

| Shape | Package | Annotations |
|-------|---------|-------------|
| API DTO | `infrastructure/adapter/web/dto/` | `record` |
| Domain model | `application/domain/model/` | `@Builder @Getter @Setter` |
| JPA entity | `infrastructure/adapter/persistence/entity/` | `@Entity` |

## Writing Order

1. Port interface (`*InPort`, `*RepositoryPort`)
2. Domain service (implements `*InPort`)
3. Mapper (domain ↔ DTO, domain ↔ entity)
4. Adapter (orchestration via ports + mappers)

## Events

Domain services publish via `*EventOutPort` → infrastructure `*EventAdapter` translates to Spring `ApplicationEvent`. Handlers use `@Async @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)`. Never fire events outside a transaction.

## External Dependency

`goods-price-comparison-api:1.3.0` from GitHub Packages (`maven.pkg.github.com/RizkiRachman`). Requires `~/.m2/settings.xml` with GitHub token. This is the API spec — controllers implement generated interfaces (`PricesApi`, `ReceiptsApi`, etc.).

## Test Quirks

- Profile: `@ActiveProfiles("test")`. Uses H2 in-memory with `ddl-auto=update`, Flyway disabled.
- LLM provider defaults to `local` in tests (no API key needed).
- ArchUnit tests in `architecture/HexagonalArchitectureTest.java` — 7 rules.
- No integration tests currently run (failsafe plugin in `integration-tests` profile only).

## Skills

Reusable expertise loaded from `.opencode/skills/`. Run `/skill <name>` in a session to load:
- `java-developer` — Spring Boot 3.4, hexagonal architecture, GoF patterns, clean code, quality gates
- `agent-development` — broader Spring Boot + quality gates
- `software-developer` — general development best practices, SOLID, code quality
- `qa-expert` — test strategy, coverage analysis, regression prevention
- `system-analyst` — architecture evaluation, impact analysis, technical documentation
- `business-analyst` — requirements gathering, process modeling, prioritization
- `devops-expert` — CI/CD, IaC, containerization, monitoring, deployment
- `security-expert` — threat modeling, secure code review, vulnerability assessment
- `front-end-expert` — React, state management, a11y, performance, testing

## Agents

Custom sub-agents in `.opencode/agents/`. Invoke with `@<name>` in a session:
- `@software-developer` — focused coding agent with auto-verify
- `@development` — five-lens orchestrator: developer + QA + system analyst + business analyst + DevOps
- `@security-performance` — read-only auditor: QA + DevOps + security, produces structured report

## Constants

`common/constant/` has `AppConstants`, `ErrorCodes`, `ErrorMessageConstants`, `EntityConstants`, `ParamConstants`, `SortConstants`. Use these instead of magic strings. LLM-related constants in `llm/infrastructure/config/LlmConstants.java`.

Available skills expose detailed coding standards. Load `/skill java-developer` for the full reference.
