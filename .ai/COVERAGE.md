# Code Coverage Requirements & Guide

## 🎯 Coverage Standards

### Strict Enforcement Matrix

| Code Type | Minimum | Target | Hard Gate |
|-----------|---------|--------|-----------|
| **Existing Code** | 90% | 95% | ✅ Yes |
| **New Code** | **100%** | **100%** | ✅ Yes |
| **Modified Code** | **100%** | **100%** | ✅ Yes |
| **Utilities** | 95% | 100% | ✅ Yes |
| **Controllers** | 90% | 95% | ✅ Yes |
| **Services** | 95% | 100% | ✅ Yes |
| **Repositories** | 90% | 95% | ✅ Yes |
| **DTOs** | 80% | 90% | ✅ Yes |

---

## 📊 Coverage Types

### 1. Line Coverage
**What it measures:** Percentage of lines executed
**Requirement:** 90%+ existing, 100% new

```bash
# Generate report
mvn jacoco:report

# Check line coverage
grep -A 5 "Line coverage" target/site/jacoco/index.html
```

### 2. Branch Coverage
**What it measures:** Percentage of branches (if/else, switch) executed
**Requirement:** 85%+ existing, 95% new

```bash
# Check branch coverage
grep -A 5 "Branch coverage" target/site/jacoco/index.html
```

### 3. Mutation Coverage (PIT)
**What it measures:** Test strength against code mutations
**Requirement:** 80%+

```bash
# Run mutation testing
mvn org.pitest:pitest-maven:mutationCoverage

# Check report
cat target/pit-reports/*/mutations.xml
```

---

## 🧪 How to Achieve 100% Coverage

### Strategy 1: Happy Path

Test the normal, expected flow:

```java
@Test
@DisplayName("Should calculate price when all inputs valid")
void shouldCalculatePrice_WhenAllInputsValid() {
    // Given - valid inputs
    PriceRequest request = new PriceRequest("Milk", 1L);
    
    // When
    PriceResponse response = service.calculate(request);
    
    // Then - expected result
    assertNotNull(response);
    assertEquals(new BigDecimal("5600"), response.getPrice());
}
```

### Strategy 2: Edge Cases

Test boundary conditions:

```java
@Test
@DisplayName("Should handle zero quantity")
void shouldHandleZeroQuantity() {
    // Edge case: quantity = 0
    assertThrows(IllegalArgumentException.class, () -> {
        calculator.calculate(0, new BigDecimal("100"));
    });
}

@Test
@DisplayName("Should handle maximum quantity")
void shouldHandleMaxQuantity() {
    // Edge case: very large quantity
    int maxQuantity = Integer.MAX_VALUE;
    // ...
}

@Test
@DisplayName("Should handle null inputs")
void shouldHandleNullInputs() {
    // Edge case: null values
    assertThrows(NullPointerException.class, () -> {
        calculator.calculate(null, new BigDecimal("100"));
    });
}
```

### Strategy 3: Exception Paths

Test all exception scenarios:

```java
@Test
@DisplayName("Should throw exception when product not found")
void shouldThrowException_WhenProductNotFound() {
    when(repository.findById(any())).thenReturn(Optional.empty());
    
    assertThrows(ProductNotFoundException.class, () -> {
        service.findPrice(999L);
    });
}

@Test
@DisplayName("Should throw exception when database fails")
void shouldThrowException_WhenDatabaseFails() {
    when(repository.findById(any()))
        .thenThrow(new DataAccessException("DB Error"));
    
    assertThrows(ServiceException.class, () -> {
        service.findPrice(1L);
    });
}
```

### Strategy 4: Branch Coverage

Test all if/else branches:

```java
// Method to test:
public boolean isPromoPrice(BigDecimal price, LocalDate date) {
    if (price == null || date == null) {
        return false;  // Branch 1
    }
    if (date.isAfter(promoEndDate)) {
        return false;  // Branch 2
    }
    if (price.compareTo(regularPrice) < 0) {
        return true;   // Branch 3
    }
    return false;      // Branch 4
}

// Tests for each branch:
@Test
void shouldReturnFalse_WhenPriceIsNull() { /* Branch 1 */ }

@Test
void shouldReturnFalse_WhenDateIsNull() { /* Branch 1 */ }

@Test
void shouldReturnFalse_WhenDateAfterPromo() { /* Branch 2 */ }

@Test
void shouldReturnTrue_WhenPriceLower() { /* Branch 3 */ }

@Test
void shouldReturnFalse_WhenPriceNotLower() { /* Branch 4 */ }
```

---

## 🛠️ Coverage Tools

### 1. JaCoCo (Primary)

**Configuration in pom.xml:**
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>INSTRUCTION</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.90</minimum>
                            </limit>
                            <limit>
                                <counter>BRANCH</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.85</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Check coverage:**
```bash
mvn clean test jacoco:report
open target/site/jacoco/index.html
```

### 2. IntelliJ IDEA Coverage

**Built-in coverage runner:**
1. Run tests with Coverage
2. View results in Coverage tool window
3. Green = covered, Red = not covered

### 3. VS Code Coverage

**Extension: Coverage Gutters**
- Shows coverage in editor
- Red = not covered
- Green = covered

---

## 📈 Coverage Improvement Workflow

### Step 1: Identify Gaps

```bash
# Generate report
mvn clean test jacoco:report

# Open report
cat target/site/jacoco/index.html | grep -E "class.*[0-9]+%" | sort -t'>' -k3 -n

# Find classes with low coverage
grep -r "class=\"el_class\"" target/site/jacoco/ | grep -v "100%"
```

### Step 2: Analyze Uncovered Code

```bash
# Open class coverage report
open target/site/jacoco/com.example.goodsprice.service/PriceService.html

# Red lines = not covered
# Yellow lines = partial coverage
# Green lines = fully covered
```

### Step 3: Write Missing Tests

**Example: Uncovered catch block**
```java
// Original code:
try {
    processReceipt(image);
} catch (OcrException e) {
    log.error("OCR failed", e);  // Not covered!
    throw new ServiceException("Failed to process receipt", e);
}

// Add test:
@Test
void shouldThrowServiceException_WhenOcrFails() {
    when(ocrService.process(any()))
        .thenThrow(new OcrException("Failed"));
    
    assertThrows(ServiceException.class, () -> {
        service.processReceipt(image);
    });
}
```

### Step 4: Verify Improvement

```bash
# Re-run tests
mvn clean test jacoco:report

# Check new coverage
grep "Total" target/site/jacoco/index.html
```

---

## 🎯 Coverage Checklist for New Code

For every new class/method:

- [ ] Constructor tested
- [ ] All public methods tested
- [ ] All private methods tested (via public)
- [ ] All branches covered (if/else, switch)
- [ ] All exceptions tested
- [ ] Edge cases covered (null, empty, max values)
- [ ] Happy path tested
- [ ] Error paths tested
- [ ] Line coverage = 100%
- [ ] Branch coverage >= 95%

---

## 🚨 Common Coverage Pitfalls

### 1. Lombok Generated Code

**Problem:** Getters/setters not covered
**Solution:** Exclude from coverage
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <configuration>
        <excludes>
            <exclude>**/*Dto.*</exclude>
            <exclude>**/model/**</exclude>
        </excludes>
    </configuration>
</plugin>
```

### 2. Configuration Classes

**Problem:** Spring config not tested
**Solution:** Test with @SpringBootTest or exclude
```java
@SpringBootTest
class AppConfigTest {
    @Test
    void contextLoads() {
        // Verifies config loads
    }
}
```

### 3. Static Utilities

**Problem:** Private constructor not covered
**Solution:** Test or exclude
```java
@Test
void constructorShouldBePrivate() {
    assertThrows(InvocationTargetException.class, () -> {
        Constructor<PriceUtils> constructor = PriceUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    });
}
```

---

## 📊 Coverage Reports

### JaCoCo Report Location

```
target/site/jacoco/
├── index.html              # Overview
├── com.example.goodsprice/
│   ├── index.html          # Package summary
│   ├── PriceService.html   # Class details
│   └── ...
└── jacoco.xml              # XML for CI
```

### Reading the Report

- **Green**: Fully covered
- **Yellow**: Partially covered (branches)
- **Red**: Not covered

### CI Integration

**GitHub Actions:**
```yaml
- name: Upload coverage
  uses: codecov/codecov-action@v3
  with:
    file: ./target/site/jacoco/jacoco.xml
    fail_ci_if_error: true
```

---

## ✅ Coverage Verification Commands

```bash
# Full check
mvn clean verify

# Quick coverage check
mvn test jacoco:report && \
  cat target/site/jacoco/index.html | grep "Total.*[0-9]*%"

# Check specific class
cat target/site/jacoco/com.example/PriceService.html | grep -o "[0-9]*%" | head -1

# Enforce coverage (fails if below threshold)
mvn jacoco:check
```

---

*Remember: 100% coverage doesn't mean bug-free, but it means all code is tested.*

*Aim for 100% coverage on new code. It's achievable and worth it.*