# Coding Standards & Rules

## 🎯 Purpose

Define strict coding standards to ensure consistency, readability, and maintainability across the entire codebase.

---

## 📝 General Rules

### 1. File Organization

```
src/main/java/com/example/goodsprice/
├── config/              # Configuration classes
├── controller/          # REST controllers
├── dto/                # Data transfer objects
├── exception/          # Custom exceptions
├── model/              # Entity/domain classes
├── repository/         # Database repositories
├── service/            # Business logic
│   ├── impl/          # Service implementations
│   └── mapper/        # Object mappers
└── util/              # Utility classes

src/test/java/com/example/goodsprice/
├── unit/               # Unit tests
├── integration/        # Integration tests
├── e2e/               # End-to-end tests
└── fixtures/          # Test data
```

### 2. Naming Conventions

**Classes:**
- PascalCase
- Nouns (e.g., `PriceService`, `ReceiptController`)
- Test classes: `ClassNameTest`

**Methods:**
- camelCase
- Verbs (e.g., `calculateTotal`, `findCheapestStore`)
- Test methods: `shouldExpectedBehaviorWhenCondition`

**Variables:**
- camelCase
- Meaningful names (e.g., `totalPrice`, not `tp`)
- Constants: UPPER_SNAKE_CASE

**Packages:**
- com.example.goodsprice.{layer}
- All lowercase
- Max 3 levels deep

### 3. Code Formatting

**Indentation:**
- 4 spaces (no tabs)
- Continuation indent: 8 spaces

**Line Length:**
- Soft limit: 100 characters
- Hard limit: 120 characters
- Break after operators

**Braces:**
- Opening brace on same line
- Closing brace on new line
- Always use braces (even for single line)

```java
// ✅ Good
if (price > 0) {
    return calculateDiscount(price);
}

// ❌ Bad
if (price > 0)
    return calculateDiscount(price);
```

---

## 🧱 Class Design Rules

### 1. Class Size

**Maximums:**
- Lines of code: 500
- Methods: 20
- Fields: 15

**If exceeded:**
- Split into smaller classes
- Use composition over inheritance
- Extract private methods

### 2. Method Design

**Maximums:**
- Lines of code: 50
- Parameters: 5
- Return statements: 3
- Cyclomatic complexity: 10

**Method Structure:**
```java
public ReturnType methodName(ParamType param) {
    // 1. Validate inputs
    validateInput(param);
    
    // 2. Get dependencies
    Dependency dep = dependencyService.find(param);
    
    // 3. Process
    Result result = processor.process(dep);
    
    // 4. Return
    return result;
}
```

### 3. Field Declaration

**Order:**
1. Static constants
2. Instance variables (private final)
3. Constructors
4. Public methods
5. Private methods

**Access Modifiers:**
```java
// ✅ Good
private final PriceRepository priceRepository;
private static final Logger log = LoggerFactory.getLogger(Class.class);

// ❌ Bad
public PriceRepository priceRepository;  // Never public!
```

---

## 🔒 Access Control

### 1. Encapsulation

**Fields:**
- Always private
- Use getters/setters if needed
- Prefer immutable (final)

**Methods:**
- Private: Implementation details
- Protected: Extension points only
- Public: API surface
- Package-private: Internal sharing

### 2. Dependency Injection

**Constructor injection only:**
```java
// ✅ Good
@Service
public class PriceService {
    private final PriceRepository repository;
    private final StoreService storeService;
    
    public PriceService(PriceRepository repository, StoreService storeService) {
        this.repository = repository;
        this.storeService = storeService;
    }
}

// ❌ Bad
@Service
public class PriceService {
    @Autowired
    private PriceRepository repository;  // Field injection
}
```

---

## 📚 Documentation Rules

### 1. JavaDoc Requirements

**Every public element MUST have JavaDoc:**

```java
/**
 * Calculates the total price for a shopping cart.
 *
 * <p>This method applies any applicable discounts and promotions
 * to calculate the final price the customer needs to pay.</p>
 *
 * @param items the list of items in the cart (must not be null or empty)
 * @param storeId the ID of the store where purchase is made
 * @return the total price after discounts
 * @throws IllegalArgumentException if items is null or empty
 * @throws StoreNotFoundException if store doesn't exist
 * @since 1.0.0
 */
public BigDecimal calculateTotal(List<CartItem> items, Long storeId) {
    // implementation
}
```

**Required Tags:**
- `@param` - All parameters
- `@return` - Return value (except void)
- `@throws` - All checked exceptions
- `@since` - Version introduced

### 2. Code Comments

**When to comment:**
- Complex algorithms
- Business rules
- Workarounds
- TODOs (with issue number)

**When NOT to comment:**
- Obvious code
- What code does (show, don't tell)
- Outdated comments

```java
// ✅ Good - explains WHY
// Using BigDecimal to avoid floating-point precision issues
// with monetary calculations
BigDecimal price = new BigDecimal("19.99");

// ❌ Bad - states the obvious
// Set price to 19.99
BigDecimal price = new BigDecimal("19.99");
```

---

## 🧪 Testing Rules

### 1. Test Structure

**Given-When-Then format:**
```java
@Test
@DisplayName("Should calculate total price with discount when quantity exceeds threshold")
void shouldCalculateTotalPriceWithDiscount_WhenQuantityExceedsThreshold() {
    // Given
    int quantity = 10;
    BigDecimal unitPrice = new BigDecimal("100.00");
    
    // When
    BigDecimal total = calculator.calculate(quantity, unitPrice);
    
    // Then
    assertEquals(new BigDecimal("900.00"), total);  // 10% discount applied
}
```

### 2. Test Independence

**Rules:**
- No shared state between tests
- Each test creates its own data
- Clean up after test (use @AfterEach)
- No test execution order dependency

### 3. Mocking Rules

**What to mock:**
- External services (OCR, APIs)
- Database (use @DataJpaTest or Testcontainers)
- Slow operations

**What NOT to mock:**
- Value objects (DTOs, entities)
- Utility classes
- Simple calculations

```java
// ✅ Good - mock external service
@Mock
private OcrService ocrService;

// ❌ Bad - don't mock simple objects
@Mock
private PriceDto priceDto;
```

---

## 🎯 Specific Rules by Layer

### 1. Controllers

**Rules:**
- Stateless (no instance variables)
- Validate all inputs
- Return ResponseEntity
- Use consistent error format

```java
@RestController
@RequestMapping("/api/prices")
public class PriceController {
    
    private final PriceService priceService;
    
    @GetMapping("/search")
    public ResponseEntity<PriceSearchResponse> searchPrices(
            @Valid @RequestBody PriceSearchRequest request) {
        
        List<PriceDto> prices = priceService.search(request);
        return ResponseEntity.ok(new PriceSearchResponse(prices));
    }
}
```

### 2. Services

**Rules:**
- Annotate with @Transactional when needed
- Business logic only (no HTTP, no DB details)
- Throw domain exceptions
- Use final variables

### 3. Repositories

**Rules:**
- Extend JpaRepository or use @Repository
- Method names follow Spring Data conventions
- Use @Query for complex queries
- Always return Optional for single results

```java
// ✅ Good
public interface PriceRepository extends JpaRepository<PriceRecord, Long> {
    Optional<PriceRecord> findByProductIdAndStoreId(Long productId, Long storeId);
    
    @Query("SELECT p FROM PriceRecord p WHERE p.product.name = :name ORDER BY p.price ASC")
    List<PriceRecord> findCheapestByProductName(@Param("name") String name);
}
```

### 4. DTOs

**Rules:**
- Use records (Java 17+) for immutable DTOs
- Validate with Bean Validation
- No business logic
- Map from/to entities in mapper layer

```java
// ✅ Good - immutable record
public record PriceSearchRequest(
    @NotBlank String productName,
    @NotNull @Positive Long storeId,
    @Min(0) BigDecimal maxPrice
) {}
```

---

## 🚨 Forbidden Patterns

### Code Smells

```java
// ❌ String concatenation in loop
String result = "";
for (String s : list) {
    result += s;  // Use StringBuilder
}

// ❌ Catch and ignore
try {
    process();
} catch (Exception e) {
    // DON'T DO THIS!
}

// ❌ Return null
public List<Price> findPrices() {
    if (noResults) {
        return null;  // Return empty list instead
    }
}

// ❌ Magic numbers
if (status == 3) {  // What is 3?
    // ...
}

// ❌ God object
public class OrderService {
    public void processOrder() { /* 200 lines */ }
    public void sendEmail() { /* 100 lines */ }
    public void updateInventory() { /* 150 lines */ }
    // Split these into separate services!
}
```

---

## 📊 Quality Gates

### Before Commit

```bash
# Run these checks
mvn clean compile                    # Must pass
mvn test                            # 100% pass rate
mvn checkstyle:check                # 0 violations
mvn spotbugs:check                  # 0 high priority
mvn jacoco:report                   # 90%+ coverage
```

### IDE Settings

**Enable:**
- Auto-format on save
- Organize imports
- Remove unused imports
- Add final modifier

**Recommended Plugins:**
- SonarLint
- Checkstyle
- Save Actions

---

## 🔍 Code Review Checklist

**Reviewer must check:**

- [ ] Code follows style guide
- [ ] All public methods have JavaDoc
- [ ] No code smells (SonarQube)
- [ ] Tests cover all new code (100%)
- [ ] No breaking changes
- [ ] Performance is acceptable
- [ ] Security best practices followed
- [ ] Logging is appropriate (no sensitive data)

---

*Last updated: [AUTO-UPDATED BY CI]*