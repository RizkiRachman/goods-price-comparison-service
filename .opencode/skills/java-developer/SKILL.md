---
name: java-developer
description: Spring Boot hexagonal architecture, clean code, GoF design patterns, anti-patterns, modern Java 17, and quality gates (ArchUnit, SpotBugs, PMD CPD, Spotless)
license: MIT
compatibility: opencode
metadata:
  framework: spring-boot
  java-version: "21"
---

# Java Developer — Spring Boot + Clean Code + Design Patterns

## Architecture: Microservices → Hexagonal → Event-Driven

```
Each service: application/ (pure Java, no Spring/JPA) + infrastructure/ (adapters)
Services communicate via Spring ApplicationEvent + @Async @TransactionalEventListener(AFTER_COMMIT)
```

### Package Structure Per Service

```
service-name/
├── application/
│   ├── domain/model/        ← POJOs @Builder @Getter @Setter, zero annotations
│   ├── domain/service/      ← @Service implements *InPort
│   ├── port/in/             ← Driving port interfaces (XxxInPort)
│   ├── port/out/            ← Driven ports (XxxRepositoryPort, XxxEventOutPort)
│   └── exception/           ← Domain exceptions
└── infrastructure/
    ├── adapter/web/         ← @RestController + WebAdapter + *DtoMapper
    ├── adapter/persistence/ ← @Entity + RepositoryAdapter + JpaRepository + *Mapper
    ├── adapter/event/       ← Implements XxxEventOutPort (Spring events)
    ├── adapter/llm/         ← LLM provider adapters (if applicable)
    └── handler/event/       ← @Async @TransactionalEventListener (AFTER_COMMIT)
```

---

## Data Shapes

| Shape | Java Type | Package | Purpose |
|-------|-----------|---------|---------|
| API DTO | `record` | `infrastructure/adapter/web/dto/` | Request/response |
| Domain Model | `class` with `@Builder` | `application/domain/model/` | Business logic with behavior |
| Entity | `class` with JPA annotations | `infrastructure/adapter/persistence/entity/` | Database mapping |

---

## JPA Entity Rules
- NO `@ManyToOne`, `@OneToMany`, `@OneToOne`, `@ManyToMany`, `@JoinColumn`
- FK columns stored as primitives only: `private UUID receiptId;`
- UUID `@Id` always: `@GeneratedValue(strategy = GenerationType.UUID)`
- Long `@Id` with identity: `@GeneratedValue(strategy = GenerationType.IDENTITY)`

## Port Rules
- Driving ports: `XxxInPort` — consumed by web adapters, implemented by domain services
- Driven ports: `XxxRepositoryPort` — consumed by domain services, implemented by infrastructure
- Event ports: `XxxEventOutPort` — for publishing domain events
- All ports return **nullable** (no Optional). Spring Data Optional unwrapped at adapter boundary.

---

## Null Handling (No Optional)

```java
// ✅ GOOD — nullable return
public Receipt findById(UUID id);

// ✅ Always use utility methods:
Objects.isNull(x)                    // instead of x == null
Objects.nonNull(x)                   // instead of x != null
StringUtils.isBlank(x)               // instead of x == null || x.isEmpty()
ObjectUtils.defaultIfNull(x, fallback)
ObjectUtils.getOrNull(x, X::getY)    // obj != null ? obj.getY() : null
ObjectUtils.getOrDefault(x, X::getY, fallback)
ValidationUtils.requireNonNull(x, "name")
NumberUtils.toDouble(x)              // safe instanceof + parse
JsonUtils.extractItems(json)         // parse "items" array from LLM response
HashUtils.sha256(bytes)              // Base64-encoded SHA-256
```

---

## Mapper Pattern

Every conversion between shapes goes in a dedicated `*Mapper` / `*DtoMapper` class — never in the adapter.

```
infrastructure/adapter/
├── web/
│   ├── *Controller.java       ← @RestController, thin — delegates to adapter
│   ├── *WebAdapter.java       ← @Component, orchestration — calls ports, delegates mapping
│   ├── mapper/
│   │   └── *DtoMapper.java    ← @Component, pure mapping logic
│   └── dto/                   ← API DTO records
└── persistence/
    ├── *Mapper.java           ← domain ↔ entity
    └── *RepositoryAdapter.java
```

Naming: `*DtoMapper` (domain → API DTO), `*Mapper` (domain ↔ entity). Methods named `toXxx`.

---

## Event-Driven Communication
- Domain service publishes via `XxxEventOutPort`
- Infrastructure adapter translates to Spring `ApplicationEvent`
- Handlers use `@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)` + `@Async`
- Never fire events outside a transaction boundary

---

## Modern Java 17 (Non-Negotiable)

```java
String.formatted()              // NEVER String.format()
.toList()                       // NEVER .collect(Collectors.toList())
.map(this::method)              // method reference over lambda
instanceof Foo f                // pattern matching, no explicit cast
case X ->                       // switch expressions, no break
```

---

## GoF Design Patterns Applied

### In This Codebase
| Pattern | Usage |
|---------|-------|
| **Adapter** | infrastructure/adapter/{web,persistence,event}/ |
| **Facade** | application/domain/service/ |
| **Strategy** | Port interfaces with multiple implementations |
| **Observer** | ApplicationEventPublisher + @EventListener |
| **State** | Domain model state-transition methods (markAsXxx) |
| **Builder** | Lombok @Builder on domain models |
| **Factory Method** | Provider factories (LlmProviderFactory) |

### Full GoF Reference

#### Creational
| Pattern | When | Signal |
|---------|------|--------|
| **Factory Method** | Class can't anticipate objects it creates | `switch` on type to create |
| **Abstract Factory** | Families of related products | Objects that must be used together |
| **Builder** | Complex objects with optional parts | Telescoping constructors |
| **Singleton** | Exactly one global instance | Shared resource (logger, pool) |
| **Prototype** | Copy without coupling to class | Expensive creation |

#### Structural
| Pattern | When | Signal |
|---------|------|--------|
| **Adapter** | Interface mismatch | "API doesn't match what we need" |
| **Decorator** | Add responsibilities dynamically | Many combinations of behaviors |
| **Facade** | Simplify complex subsystem | "Need one simple method from this mess" |
| **Composite** | Uniform treatment of individual/composite | Tree structures |
| **Proxy** | Control access to another object | Lazy loading, access control, logging |
| **Bridge** | Decouple abstraction from implementation | Two orthogonal dimensions of variation |

#### Behavioral
| Pattern | When | Signal |
|---------|------|--------|
| **Strategy** | Multiple algorithms for same task | `if/else` or `switch` on type |
| **Observer** | One-to-many notification | Event listeners, pub/sub |
| **Command** | Parameterize operations, support undo | Queues, transactional behavior |
| **Template Method** | Skeleton with overridable steps | "Same steps but one varies" |
| **State** | Behavior changes with internal state | Complex `if/else` on state variable |
| **Chain of Responsibility** | Multiple handlers, one processes | Request pipeline |
| **Mediator** | Reduce chaotic coupling | Many objects all talking to each other |
| **Iterator** | Uniform traversal | "How do I walk this structure?" |
| **Visitor** | Operations on stable object structure | "Add operation without changing classes" |
| **Memento** | Capture/restore state | Undo/redo |

---

## Anti-Patterns to Avoid

### Code Level
| Anti-Pattern | Looks like | Fix |
|-------------|-----------|-----|
| **God Class** | >500 lines, >5 deps | Split by responsibility |
| **Shotgun Surgery** | One change touches many files | Consolidate |
| **Feature Envy** | Uses another class's data more than own | Move the method |
| **Primitive Obsession** | Primitives for domain concepts | Extract value objects |
| **Data Clump** | Same fields appear together repeatedly | Extract class |
| **Switch Statement** | `switch` on type for behavior | Polymorphism or Strategy |
| **Speculative Generality** | "We might need this someday" | YAGNI — delete it |
| **Message Chain** | `a.getB().getC().doSomething()` | Hide chain or extract |
| **Middle Man** | Class that only delegates | Remove middleman |

### Architecture Level
| Anti-Pattern | Looks like | Fix |
|-------------|-----------|-----|
| **Big Ball of Mud** | No clear structure, everything depends | Define boundaries, enforce layering |
| **Circular Dependency** | A depends on B, B depends on A | Extract shared concept |
| **Leaky Abstraction** | Implementation detail visible through abstraction | Tighten the abstraction |
| **Golden Hammer** | Using familiar tool for every problem | Choose right tool |
| **Premature Optimization** | Optimizing before measuring | Profile first, optimize second |

### JPA-Specific Anti-Patterns
- `@ManyToOne`/`@OneToMany` — lazy loading N+1, cascading surprises
- Leaky abstraction: JPA annotations in domain model
- Fetching entire tables when a count or paginated query would do

---

## Design Principles

### SOLID
- **S**ingle Responsibility — one reason to change per class
- **O**pen/Closed — open for extension, closed for modification
- **L**iskov Substitution — subtypes substitutable for base types
- **I**nterface Segregation — small focused interfaces
- **D**ependency Inversion — depend on abstractions, not concretions

### Other Key Principles
- **DRY**: Don't Repeat Yourself — one representation per piece of knowledge
- **YAGNI**: You Ain't Gonna Need It — build only what's needed now
- **KISS**: Keep It Simple, Stupid — simplest solution is usually best
- **Law of Demeter**: Talk only to immediate friends (one dot)
- **Tell, Don't Ask**: Tell objects what to do, don't ask their state
- **Composition over Inheritance**: Favor composition for flexibility

---

## Magic Strings → Enums or Constants

```java
// ❌ BAD — magic strings duplicated
switch (provider.toLowerCase()) {
  case "openai" -> openai;
  case "gemini" -> gemini;
}

// ✅ GOOD — single source of truth
public enum LlmProviderType {
  LOCAL, OPENAI, GEMINI, GROQ;
  public static LlmProviderType fromValue(String value) {
    return switch (value.toLowerCase(Locale.ROOT)) {
      case "openai" -> OPENAI;
      case "gemini" -> GEMINI;
      default -> LOCAL;
    };
  }
}
```

For non-enum strings (JSON keys, API fields, provider names), use a `*Constants` class:
- JSON map keys → `KEY_*` constants
- API field names → `GROQ_*`, `GEMINI_*` prefixes
- Provider names → `PROVIDER_*` constants

---

## Exception Handling
- Custom exceptions in `application/exception/`: extend `RuntimeException`
- Messages from `ErrorMessageConstants` constants
- `@RestControllerAdvice` with `GlobalExceptionHandler` for all controllers
- Use `ErrorCodes` constants for error codes

---

## Writing Code — Order of Operations

1. **Start with the port interface** — `*InPort`, `*RepositoryPort`
2. **Domain service** implements the port, uses only domain models
3. **Mapper** converts between shapes (never field-by-field in adapters)
4. **Adapter** handles orchestration (calls ports, delegates to mapper)
5. **Constants** — extract magic strings to `*Constants` or enums
6. **Events** — domain service publishes via `*EventOutPort`, handler uses `@Async @TransactionalEventListener(AFTER_COMMIT)`

---

## Build Quality Gates

```bash
mvn test                    # Unit tests + ArchUnit (7 rules)
mvn verify                  # Full: tests + format + CPD + SpotBugs
mvn spotless:apply          # Auto-format (Google Java Style)
mvn spotbugs:gui            # Visual SpotBugs report
```

| Gate | Enforces | Fails build |
|------|----------|-------------|
| **ArchUnit** (7 rules) | Hexagonal layers, no Optional in ports, no JPA in domain | ✅ |
| **Spotless** | Google Java Format | ✅ |
| **SpotBugs** | Null safety, encoding, exposed internals | ✅ (`failOnError: true`) |
| **PMD CPD** | No duplicate code (>100 tokens) | ✅ |

### ArchUnit Rules
| Rule | Prevents |
|------|----------|
| `domainMustNotDependOnInfrastructure` | `application/` importing `infrastructure/` |
| `domainModelsMustNotHaveJpaAnnotations` | `@Entity`, `@Id`, `@Column` in domain POJOs |
| `portsMustNotReturnOptional` | `Optional<T>` in port return types |
| `entitiesMustNotUseJpaRelationshipAnnotations` | `@ManyToOne`, `@OneToMany`, etc. |
| `layeredArchitectureShouldRespectHexagonalBoundaries` | Reverse dependency direction |
| `domainServicesMustBeAnnotatedWithService` | Missing `@Service` |
| `repositoryAdaptersMustBeAnnotatedWithComponent` | Missing `@Component` |

### Page Format
```java
record PageRequest(int page, int size, String sortBy, String sortDirection) {}
record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages, boolean first, boolean last) {}
```

### Constants
- `AppConstants` — string/numeric/date constants
- `ErrorCodes` — error code identifiers
- `ErrorMessageConstants` — message templates
- `EntityConstants` — field names
- `ParamConstants` — request param names
- `SortConstants` — sort direction values

### Naming Convention for Conflicting Models

When API models (from external JAR) conflict with domain models:

**Priority:** API Model > Domain Model > Entity

**Naming Pattern:**
```
API Model (external JAR):     Product          (keep simple name)
Domain Model (internal):      ProductDomain    (add "Domain" suffix)
Entity (database):            ProductEntity    (add "Entity" suffix)
```

**Example:**
```java
// API Model - from goods-price-comparison-api JAR
import com.example.goodsprice.api.model.Product;

// Domain Model - internal business logic
import com.example.goodsprice.product.application.domain.model.ProductDomain;

// Entity - database mapping
import com.example.goodsprice.product.infrastructure.adapter.persistence.entity.ProductEntity;

// Usage in mapper
@Component
public class ProductDtoMapper {
    public Product toApiProduct(ProductDomain domain) {
        // Map domain to API model
    }
    
    public ProductDomain toDomain(ProductEntity entity) {
        // Map entity to domain
    }
}
```

**Rules:**
- Never use fully qualified names inline
- Always add proper imports at the top
- When conflict exists, domain model gets "Domain" suffix
- Entity always has "Entity" suffix (existing convention)

### Avoid Redundant Variable Assignments

**❌ BAD — Redundant intermediate variable**
```java
var all = stream.toList();
var comparator = resolveComparator(sortByValue);
if (Objects.nonNull(comparator)) {
  var sorted =
      "desc".equalsIgnoreCase(sortDirValue)
          ? all.stream().sorted(comparator.reversed()).toList()
          : all.stream().sorted(comparator).toList();
  all = sorted;  // Redundant assignment
}
return all;
```

**✅ GOOD — Direct assignment or return**
```java
var all = stream.toList();
var comparator = resolveComparator(sortByValue);
if (Objects.nonNull(comparator)) {
  all = "desc".equalsIgnoreCase(sortDirValue)
      ? all.stream().sorted(comparator.reversed()).toList()
      : all.stream().sorted(comparator).toList();
}
return all;
```

**✅ BETTER — Return directly without reassignment**
```java
var result = stream.toList();
var comparator = resolveComparator(sortByValue);
if (Objects.nonNull(comparator)) {
  return "desc".equalsIgnoreCase(sortDirValue)
      ? result.stream().sorted(comparator.reversed()).toList()
      : result.stream().sorted(comparator).toList();
}
return result;
```

**Rule:** Avoid creating a variable just to immediately assign it to another variable. Either assign directly or return immediately.

### No Unused Code (YAGNI Principle)

**Do not implement:**
- Functions that are never called
- Variables that are never used
- Parameters that are never referenced
- Dead code or commented-out code

**Exceptions:**
- ✅ **Constants** — defined for future use or clarity
- ✅ **Utility methods** — part of a utility class designed for reuse
- ✅ **Abstract methods** — required by interface/abstract class
- ✅ **Overridden methods** — parent class requires implementation

**❌ BAD — Unused function**
```java
// Never called anywhere
private void validateProductName(String name) {
    if (StringUtils.isBlank(name)) {
        throw new ValidationException("Name required");
    }
}
```

**❌ BAD — Unused variable**
```java
public Product createProduct(CreateRequest request) {
    var timestamp = Instant.now();  // Never used
    return productRepository.save(request);
}
```

**❌ BAD — Unused parameter**
```java
public List<Product> findProducts(String category, String sortBy, boolean includeDeleted) {
    // includeDeleted is never used in the method
    return repository.findByCategory(category);
}
```

**✅ GOOD — Clean implementation**
```java
public Product createProduct(CreateRequest request) {
    return productRepository.save(request);
}

public List<Product> findProducts(String category) {
    return repository.findByCategory(category);
}
```

**Rule:** If it's not used, don't write it. If it becomes needed later, add it then (YAGNI — You Ain't Gonna Need It).

---

## Token Optimization

When working with Java code and Spring Boot applications, optimize context usage:

```bash
/skill token-optimize
```

**Key practices:**
- Search with `grep` before reading large Java files
- Use `limit` and `offset` when reading service implementations
- Focus on relevant packages and classes
- Reuse context when working on related features
