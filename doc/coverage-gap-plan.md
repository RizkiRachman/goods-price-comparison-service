# Coverage Gap Closure Plan

## Goal
Close all 18 identified coverage gaps across the project to meet JaCoCo thresholds (line ≥ 90%, branch ≥ 85%) — test-only changes.

## Acceptance Criteria
- [ ] P1 packages (instruction < 90%) reach ≥ 90% instruction coverage
- [ ] P2 packages (branch < 85%) reach ≥ 85% branch coverage
- [ ] All existing 1,076 tests continue to pass
- [ ] `mvn test` passes with no failures
- [ ] `mvn spotless:apply` formatting clean
- [ ] `gitnexus_detect_changes()` shows only expected test files changed

## Scope
### Files to Modify (test files only)
- `src/test/java/.../activity/infrastructure/aspect/ActivityLogAspectTest.java`
- `src/test/java/.../llm/infrastructure/adapter/provider/GeminiLlmProviderTest.java`
- `src/test/java/.../llm/infrastructure/config/LlmProviderConfigurationTest.java`
- `src/test/java/.../activity/infrastructure/adapter/persistence/ActivityLogRepositoryAdapterTest.java`
- `src/test/java/.../product/infrastructure/adapter/persistence/ProductRepositoryAdapterTest.java`
- `src/test/java/.../price/infrastructure/adapter/web/PriceWebAdapterTest.java`
- `src/test/java/.../config/ratelimit/RateLimitInterceptorTest.java`
- `src/test/java/.../receipt/application/domain/service/ReceiptServiceTest.java`
- `src/test/java/.../common/service/CommonServiceTest.java` (or equivalent)
- `src/test/java/.../receipt/infrastructure/handler/event/*Test.java`
- `src/test/java/.../feedbackquestion/infrastructure/adapter/persistence/*Test.java`
- `src/test/java/.../unit/infrastructure/adapter/web/*Test.java`

### Files to Create
- `src/test/java/.../activity/infrastructure/adapter/web/mapper/ActivityLogDtoMapperTest.java`
- `src/test/java/.../product/infrastructure/adapter/web/mapper/ProductDtoMapperTest.java`
- `src/test/java/.../alert/infrastructure/adapter/web/mapper/AlertDtoMapperTest.java`
- `src/test/java/.../shopping/infrastructure/adapter/web/mapper/ShoppingDtoMapperTest.java`
- `src/test/java/.../feedbackquestion/infrastructure/adapter/web/mapper/FeedbackQuestionDtoMapperTest.java`

### Out of Scope
- Production code changes (unless required for testability, which is not expected)
- Integration or E2E tests
- Performance tests
- Test infrastructure setup

## Risk Assessment
| Risk | L | I | Score | Mitigation |
|------|---|---|-------|------------|
| Test changes break existing tests | 2 | 3 | 6 | Run `mvn test` after each task |
| New tests introduce formatting violations | 1 | 2 | 2 | Run `mvn spotless:apply` after each task |
| Accidental production code modification | 1 | 4 | 4 | All changes restricted to `src/test/java/` |
| Parallel execution conflicts on same file | 2 | 2 | 4 | Serialize tasks modifying the same file; parallelize disjoint files |

## Rollback Strategy
- All changes are test-only → `git checkout -- src/test/java/` reverts everything
- Task-level: `git checkout -- <test-file-path>` reverts individual file

---

## Task Breakdown

### Priority 1 — Instruction Coverage < 90%

---

### Task P1.1: ActivityLogAspectTest — Add missing entity types and branches [M]
**Files**: `src/test/java/.../activity/infrastructure/aspect/ActivityLogAspectTest.java`
**Predecessors**: none
**Estimated size**: 50 lines added

**Missing coverage analysis:**
- `resolveEntityType()`: Missing entity types CATEGORY, UNIT, ALERT, FEEDBACK_QUESTION, and special case `ReceiptCorrection` → RECEIPT
- `resolveEntityType()`: Missing class name ending with `$` character
- `resolveEntityType()`: Class name suffix stripped but resulting name not in ENTITY_TYPE_MAP → null
- `buildDescription()`: Not directly tested; indirect path only with entityId
- `resolveAction()`: Already well tested — no gaps
- `resolveEntityId()`: Already well tested — no gaps

**Tests to add:**

1. **`shouldResolveCategoryEntityType()`**
   - Given: `CategoryService` class
   - When: `resolveEntityType(CategoryService.class)` is called
   - Then: Returns `ActivityLogType.CATEGORY`

2. **`shouldResolveUnitEntityType()`**
   - Given: `UnitService` class
   - When: `resolveEntityType(UnitService.class)` is called
   - Then: Returns `ActivityLogType.UNIT`

3. **`shouldResolveAlertEntityType()`**
   - Given: `AlertService` class
   - When: `resolveEntityType(AlertService.class)` is called
   - Then: Returns `ActivityLogType.ALERT`

4. **`shouldResolveFeedbackQuestionEntityType()`**
   - Given: `FeedbackQuestionService` class
   - When: `resolveEntityType(FeedbackQuestionService.class)` is called
   - Then: Returns `ActivityLogType.FEEDBACK_QUESTION`

5. **`shouldResolveReceiptCorrectionAsReceipt()`**
   - Given: `ReceiptCorrectionService` class
   - When: `resolveEntityType(ReceiptCorrectionService.class)` is called
   - Then: Returns `ActivityLogType.RECEIPT`

6. **`shouldReturnNullForUnknownEntityType()`**
   - Given: `UnknownTypeService` class (name not in ENTITY_TYPE_MAP)
   - When: `resolveEntityType(UnknownTypeService.class)` is called
   - Then: Returns null

7. **`shouldStripDollarSuffixFromClassName()`**
   - Given: A class whose simple name ends with `$` (use inner anonymous class)
   - When: `resolveEntityType(ReceiptService$1.class)` is called (or simulate)
   - Then: Receipt type resolved properly (dollar suffix stripped)
   - **Note**: Use `class ReceiptService$1 {}` as inner test class, or test via `ReceiptService` with `$$Enhancer` pattern already tested. For the `name.endsWith("$")` case specifically, create `class PriceService$ {}` as test support class.

**Edge cases covered by these tests:**
- All 9 entity types in ENTITY_TYPE_MAP now tested (was 4)
- Suffix edge cases: `$`, `$$Enhancer...`, unknown entity name after suffix removal
- All return null for Adapter/JPA/Aspect paths already tested

**Verification**: `mvn test -Dtest=ActivityLogAspectTest`
**Rollback**: `git checkout -- src/test/java/.../ActivityLogAspectTest.java`

---

### Task P1.2: GeminiLlmProviderTest — Add parseResponse branch coverage [S]
**Files**: `src/test/java/.../llm/infrastructure/adapter/provider/GeminiLlmProviderTest.java`
**Predecessors**: none
**Estimated size**: 60 lines added

**Missing coverage analysis:**
- `parseResponse(String text)` private method: null text, empty text, text with ` ```json...``` ` markers, text with plain ` ```...``` ` markers, text without any markers, text that fails JSON parsing
- Since `parseResponse` is private, test it indirectly via `extractReceiptData()` with a mock that succeeds, OR use reflection. But the better approach: test via the GenAI client mock that fails at JSON parse stage.

**Approach**: Since `parseResponse` is private and called from `extractReceiptData` after the GenAI response, and GenAI SDK is not mockable, we extract `parseResponse` into a package-private method or test via the existing catch block in `extractReceiptData`.

However, the simplest approach: Add a package-private helper method or use `@VisibleForTesting` to make `parseResponse` testable. This is a minor production code change:

**Production change** (in `GeminiLlmProvider.java`):
- Change `private Map<String, Object> parseResponse(String text)` to `@VisibleForTesting Map<String, Object> parseResponse(String text)`

**Tests to add:**

1. **`shouldReturnErrorWhenResponseIsNull()`**
   - Given: null text
   - When: `parseResponse(null)` is called
   - Then: Returns map with KEY_ERROR = "Empty response from Gemini"

2. **`shouldReturnErrorWhenResponseIsEmpty()`**
   - Given: empty string text
   - When: `parseResponse("")` is called
   - Then: Returns map with KEY_ERROR = "Empty response from Gemini"

3. **`shouldParseJsonBlockFromCodeFencedResponse()`**
   - Given: text with ` ```json\n{"key":"value"}\n``` `
   - When: `parseResponse(text)` is called
   - Then: Returns map with `{"key": "value"}`

4. **`shouldParseJsonBlockFromPlainCodeFencedResponse()`**
   - Given: text with ` ```\n{"key":"value"}\n``` ` (no json marker)
   - When: `parseResponse(text)` is called
   - Then: Returns map with `{"key": "value"}`

5. **`shouldReturnRawTextWhenJsonParsingFails()`**
   - Given: text that is a plain string (not JSON), like `"Hello"` or `"Sorry, I can't process this image"`
   - When: `parseResponse(text)` is called
   - Then: Returns map with KEY_RAW_TEXT = original text

**Verification**: `mvn test -Dtest=GeminiLlmProviderTest`
**Rollback**: `git checkout -- src/test/java/.../GeminiLlmProviderTest.java; git checkout -- src/main/java/.../GeminiLlmProvider.java`

---

### Task P1.3: LlmProviderConfigurationTest — Add switch expression branch coverage [S]
**Files**: `src/test/java/.../llm/infrastructure/config/LlmProviderConfigurationTest.java`
**Predecessors**: none
**Estimated size**: 40 lines added

**Missing coverage analysis:**
- `llmProvider()` switch: currently only tests LOCAL path (since test profile has `llm.provider=local`)
- Missing: GEMINI, GROQ, SUMOPOD provider selection and the default/unknown fallback

**Approach**: Since this is a `@SpringBootTest` with `@ActiveProfiles("test")`, the provider is hardcoded to local. We need to test the switch logic. The cleanest approach is a unit test for the switch logic by mocking `LlmProperties` and calling the method directly.

However, `llmProvider()` takes 5 bean parameters. Instead, refactor to extract the selection logic, or use `@SpringBootTest` with different profiles.

**Simplest approach**: Add a `@SpringBootTest` with `@ActiveProfiles("test")` but override the provider property. Or use `@TestPropertySource`:

1. **`shouldSelectGroqProvider()`** — Add a separate test class or method that overrides properties
   - Use `@SpringBootTest(properties = {"llm.provider=groq"})`
   - Then: Verify the returned provider is `GroqLlmProvider`

2. **`shouldSelectGeminiProvider()`** — Same with `llm.provider=gemini`
   - Then: Verify the returned provider is `GeminiLlmProvider`

3. **`shouldSelectSumopodProvider()`** — Same with `llm.provider=sumopod`

**Alternative (simpler)**: Extract the switch into a `@VisibleForTesting` static method `selectProvider(LlmProviderType type, ...)` and unit test it directly.

**Recommended**: Keep it simple — add 3 `@SpringBootTest` methods with property overrides. They share the same test class:

```java
@SpringBootTest(properties = {"llm.provider=groq"})
@ActiveProfiles("test")
static class GroqProviderTest {
  @Autowired @Qualifier("llmProvider") LlmProviderPort provider;
  @Test void shouldSelectGroqProvider() { assertTrue(provider instanceof GroqLlmProvider); }
}
```

**Verification**: `mvn test -Dtest=LlmProviderConfigurationTest`
**Rollback**: `git checkout -- src/test/java/.../LlmProviderConfigurationTest.java`

---

### Task P1.4: ProductRepositoryAdapterTest — Add missing method coverage [S]
**Files**: `src/test/java/.../product/infrastructure/adapter/persistence/ProductRepositoryAdapterTest.java`
**Predecessors**: none
**Estimated size**: 30 lines added

**Missing coverage analysis:**
- `findProductsNeedingSummaryUpdate(int limit)` — not tested
- `updateSummaryLastCalculated(Long productId, LocalDateTime timestamp)` — not tested
- `updateSummaryLastCalculated(List<Long> productIds, LocalDateTime timestamp)` — not tested; empty list early-return branch not tested
- `updateLastPriceUpdate(Long productId, LocalDateTime timestamp)` — not tested

**Tests to add:**

1. **`shouldFindProductsNeedingSummaryUpdate()`**
   - Given: Mock jpaRepository returns list of entities
   - When: `findProductsNeedingSummaryUpdate(10)` is called
   - Then: Returns domain list with correct mapping; `verify(jpaRepository).findProductsNeedingSummaryUpdate(10)`

2. **`shouldUpdateSummaryLastCalculatedForSingleProduct()`**
   - Given: A productId and timestamp
   - When: `updateSummaryLastCalculated(1L, timestamp)` is called
   - Then: `verify(jpaRepository).updateSummaryLastCalculated(1L, timestamp)`

3. **`shouldUpdateSummaryLastCalculatedForMultipleProducts()`**
   - Given: A list of productIds and timestamp
   - When: `updateSummaryLastCalculated(List.of(1L, 2L), timestamp)` is called
   - Then: `verify(jpaRepository).updateSummaryLastCalculated(List.of(1L, 2L), timestamp)`

4. **`shouldNotUpdateSummaryLastCalculatedForEmptyList()`**
   - Given: Empty product ID list and timestamp
   - When: `updateSummaryLastCalculated(List.of(), timestamp)` is called
   - Then: `verify(jpaRepository, never()).updateSummaryLastCalculated(any(), any())` — returns early

5. **`shouldUpdateLastPriceUpdate()`**
   - Given: A productId and timestamp
   - When: `updateLastPriceUpdate(1L, timestamp)` is called
   - Then: `verify(jpaRepository).updateLastPriceUpdate(1L, timestamp)`

**Verification**: `mvn test -Dtest=ProductRepositoryAdapterTest`
**Rollback**: `git checkout -- src/test/java/.../ProductRepositoryAdapterTest.java`

---

### Priority 2 — Branch Coverage < 85%

---

### Task P2.1: PriceWebAdapterTest — Add branch coverage for buildCheapest and edge cases [M]
**Files**: `src/test/java/.../price/infrastructure/adapter/web/PriceWebAdapterTest.java`
**Predecessors**: none
**Estimated size**: 80 lines added

**Missing branch coverage analysis:**
- `buildCheapest()`: 3 key branches:
  1. `cheapest == null` → return null (no prices)
  2. `store == null` in storeMap → fallback `storeInPort.findById()`
  3. `includeSavings` flag: true path (V1) with `prices.size() > 1` → calculate savings; V1 with single price → no savings; false path (V2) → skip savings
- `listProductPrices()`: various null filters
- `resolveRequest()` with null dateRange
- `doSearchV1()` with cheapest null → return response with no cheapest

**Tests to add:**

1. **`shouldReturnSearchResponseWithoutCheapestWhenNoPrices()`**
   - Given: Product found, no prices returned
   - When: `search(request)` called
   - Then: Response has productName set, `cheapest` is null (or not set)
   - **Given** productInPort returns product, priceInPort returns empty list

2. **`shouldCalculateSavingsInSearchV1WithMultiplePrices()`**
   - Given: Product found, multiple prices returned
   - When: `search(request)` called
   - Then: Response includes cheapest with `savings` > 0
   - **Given** productInPort returns product, priceInPort returns [price1=10000, price2=15000], cheapest is price1
   - **Then**: savings = avg(12500) - cheapest(10000) = 2500

3. **`shouldNotCalculateSavingsWithSinglePrice`**
   - Given: Product found, single price returned
   - When: `search(request)` called
   - Then: Savings is not set (prices.size() == 1)

4. **`shouldNotCalculateSavingsInSearchV2()`**
   - Given: Product found, multiple prices
   - When: `searchV2(request)` called
   - Then: Cheapest price has no savings (includeSavings=false)

5. **`shouldFallbackToStoreInPortWhenStoreNotInMap()`**
   - Given: Cheapest price has storeId not in storeMap
   - When: `search(request)` called
   - Then: `storeInPort.findById(cheapest.getStoreId())` is called

**Verification**: `mvn test -Dtest=PriceWebAdapterTest`
**Rollback**: `git checkout -- src/test/java/.../PriceWebAdapterTest.java`

---

### Task P2.2: RateLimitInterceptorTest — Add resolveLimit/resolveWindowSeconds fallback coverage [S]
**Files**: `src/test/java/.../config/ratelimit/RateLimitInterceptorTest.java`
**Predecessors**: none
**Estimated size**: 50 lines added

**Missing branch coverage analysis:**
- `resolveLimit()`: 
  1. Annotation present with limit > 0 → annotation value (TESTED)
  2. No annotation → endpoint config with limit > 0 → NOT TESTED
  3. No annotation, no endpoint → global default → NOT TESTED
- `resolveWindowSeconds()`: Same 3-branch pattern
- `extractClientIp()`: X-Forwarded-For header present and blank → NOT TESTED

**Tests to add:**

1. **`shouldUseEndpointConfigLimitWhenAnnotationAbsent()`**
   - Given: HandlerMethod has no @RateLimit annotation; endpoint config `/api/test` has limit=30
   - When: `preHandle(request, response, handlerMethod)` called
   - Then: Bucket created with limit=30
   - **Given** `properties.getEndpoints()` returns map with `/api/test → {limit: 30, windowSeconds: 60}`
   - **Mock**: `handlerMethod.getMethodAnnotation(RateLimit.class)` returns null

2. **`shouldUseGlobalDefaultWhenNoAnnotationAndNoEndpointConfig()`**
   - Given: No @RateLimit, no endpoint config
   - When: `preHandle()` called
   - Then: Uses `properties.getLimit()` = 60 (global default)

3. **`shouldUseEndpointConfigWindowSecondsWhenAnnotationAbsent()`**
   - Same pattern as #1 but tests windowSeconds fallback

4. **`shouldFallbackToRemoteAddrWhenXForwardedForIsBlank()`**
   - Given: X-Forwarded-For header is "  " (blank string)
   - When: `preHandle()` called
   - Then: Uses `request.getRemoteAddr()` = "10.0.0.1" as IP

**Verification**: `mvn test -Dtest=RateLimitInterceptorTest`
**Rollback**: `git checkout -- src/test/java/.../RateLimitInterceptorTest.java`

---

### Task P2.3: Create Mapper Tests for uncovered DtoMappers [S × 5]
**Files to create**:
- `src/test/java/.../product/infrastructure/adapter/web/mapper/ProductDtoMapperTest.java`
- `src/test/java/.../activity/infrastructure/adapter/web/mapper/ActivityLogDtoMapperTest.java`
- `src/test/java/.../alert/infrastructure/adapter/web/mapper/AlertDtoMapperTest.java`
- `src/test/java/.../shopping/infrastructure/adapter/web/mapper/ShoppingDtoMapperTest.java`
- `src/test/java/.../feedbackquestion/infrastructure/adapter/web/mapper/FeedbackQuestionDtoMapperTest.java`

**Predecessors**: none (all 5 can be parallel)
**Estimated size**: ~40 lines each

**Pattern for each test** (follow existing `PriceDtoMapperTest.java` pattern):
- Create domain model instances with null/default fields
- Call each mapper method
- Assert correct mapping of all fields
- Assert null handling (null storeName, null location, etc.)
- Assert enum mapping correctness

**Specific for ProductDtoMapper** (75% branch):
- Test `toApiProduct()` with null ProductDomain → returns null
- Test with valid domain, null summary
- Test with valid domain and valid summary
- Test `resolveStatusValue()` branches (if it has conditional logic)

**Specific for ActivityLogDtoMapper** (75% branch):
- Test null domain input
- Test all entity type mappings
- Test null action/type fields

**Specific for AlertDtoMapper** (60% branch):
- Test null domain input
- Test with different alert severity/types if conditional

**Specific for ShoppingDtoMapper** (80% branch):
- Test null store domain
- Test with null location/name

**Specific for FeedbackQuestionDtoMapper** (50% branch):
- Test null domain input
- Test question type mapping

**Verification**: `mvn test -Dtest=*DtoMapperTest`
**Rollback**: `git rm src/test/java/.../*DtoMapperTest.java`

---

### Task P2.4: ReceiptServiceTest — Add extractTotalAmount branches [S]
**Files**: `src/test/java/.../receipt/application/domain/service/ReceiptServiceTest.java`
**Predecessors**: none
**Estimated size**: 20 lines added

**Missing branch coverage analysis:**
- `extractTotalAmount()` private method: null value → return null, Number value → BigDecimal.valueOf, String value that parses → BigDecimal, String value that throws NumberFormatException → return null
- `process()` with null imageBytes → fallback to receiptRepository.findById().getImageData()

**Tests to add:**

1. **`shouldExtractTotalAmountAsNullWhenValueIsNull()`**
   - Given: LLM returns map with totalAmount = null
   - When: `process()` called
   - Then: Receipt saved with null totalAmount (extractTotalAmount returns null)

2. **`shouldHandleTotalAmountAsNumber()`**
   - Given: LLM returns map with totalAmount = 15000 (Integer)
   - When: `process()` called
   - Then: BigDecimal.valueOf(15000) used

3. **`shouldHandleTotalAmountAsString()`**
   - Given: LLM returns map with totalAmount = "15000.50"
   - When: `process()` called
   - Then: new BigDecimal("15000.50") used

4. **`shouldHandleInvalidTotalAmountString()`**
   - Given: LLM returns map with totalAmount = "N/A"
   - When: `process()` called
   - Then: null totalAmount (NumberFormatException caught)

5. **`shouldProcessWithNullImageBytesFallingBackToRepositoryData()`**
   - Given: `process(id, null)` called; `receiptRepository.findById(id).getImageData()` returns non-null bytes
   - Then: Falls back to stored image data; `llmProvider.extractReceiptData()` called with base64 of stored bytes

**Verification**: `mvn test -Dtest=ReceiptServiceTest`
**Rollback**: `git checkout -- src/test/java/.../ReceiptServiceTest.java`

---

### Task P2.5: receipt/infrastructure/handler/event — Add event handler branch coverage [S]
**Files**: `src/test/java/.../receipt/infrastructure/handler/event/*Test.java`
**Predecessors**: none
**Estimated size**: 40 lines added

**Missing coverage**: 76.9% branch in receipt event handlers. Need to check which handlers exist.

**Tests to add** (based on analyzing handler event flows):
- Test `onApplicationEvent()` with null/non-null receipt ID branches
- Test conditional logic in event processing (if any if/else/switch)
- Test exception handling paths in handlers

**Verification**: `mvn test -Dtest=*EventHandler*Test`
**Rollback**: `git checkout -- src/test/java/.../handler/event/*Test.java`

---

### Task P2.6: common/service — Add service layer branch coverage [S]
**Files**: `src/test/java/.../common/service/*Test.java`
**Predecessors**: none
**Estimated size**: 20 lines added

**Missing coverage**: 75% branch in common/service. Need to check AbstractGenericService base test patterns.

**Tests to add** (based on existing `AbstractGenericServiceTest` pattern):
- Search/status filter branches (null search, null status)
- Page request edge cases

**Verification**: `mvn test -Dtest=*Service*Test`
**Rollback**: `git checkout -- src/test/java/.../common/service/*Test.java`

---

### Task P2.7: feedbackquestion/infrastructure/adapter/persistence — Add persistence branch coverage [S]
**Files**: `src/test/java/.../feedbackquestion/infrastructure/adapter/persistence/*Test.java`
**Predecessors**: none
**Estimated size**: 20 lines added

**Missing coverage**: 83.3% branch — close to threshold.
- Add test for edge cases in query methods: null/empty filters, boundary conditions

**Verification**: `mvn test -Dtest=*FeedbackQuestion*Test`
**Rollback**: `git checkout -- src/test/java/.../feedbackquestion/*Test.java`

---

### Task P2.8: unit/infrastructure/adapter/web — Add web adapter branch coverage [S]
**Files**: `src/test/java/.../unit/infrastructure/adapter/web/*Test.java`
**Predecessors**: none
**Estimated size**: 20 lines added

**Missing coverage**: 50% branch in unit web adapter.
- Test all controller/web adapter method branches
- Test null/empty input handling
- Test error response mapping

**Verification**: `mvn test -Dtest=*Unit*Web*Test`
**Rollback**: `git checkout -- src/test/java/.../unit/*Test.java`

---

## Dependency Graph

```
T1.1 (ActivityLogAspectTest)  → root
T1.2 (GeminiLlmProviderTest)  → root
T1.3 (LlmProviderConfigurationTest) → root
T1.4 (ProductRepositoryAdapterTest) → root
T2.1 (PriceWebAdapterTest) → root
T2.2 (RateLimitInterceptorTest) → root
T2.3a-e (5 mapper tests) → root [PARALLEL]
T2.4 (ReceiptServiceTest) → root
T2.5 (EventHandlerTest) → root
T2.6 (CommonServiceTest) → root
T2.7 (FeedbackQuestionTest) → root
T2.8 (UnitWebAdapterTest) → root
```

All tasks are root-level (no interdependencies) because they modify different files.

## Parallel Execution Plan
All 15 tasks are safe to parallelize since they touch different test files:
- **Wave 1 (8 agents)**: T1.1, T1.2, T1.3, T1.4, T2.1, T2.2, T2.3a, T2.3b
- **Wave 2 (7 agents)**: T2.3c, T2.3d, T2.3e, T2.4, T2.5, T2.6, T2.7, T2.8

## Verification Steps
1. `mvn compile` — all modules compile
2. `mvn test` — all 1,076+ tests pass
3. `mvn spotless:apply` — formatting clean
4. `gitnexus_detect_changes()` — only expected test files changed
5. Verify JaCoCo report after full build
