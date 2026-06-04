# Project State

## Completed Work
- **Generic Repository Port Pattern Enforced (2026-06-04)**:
  - 6 port interfaces now extend `GenericRepositoryPort<T, ID>` with typed domain/ID parameters: Product, Store, Price, Alert, Receipt, PriceSummary
  - 2 adapters converted to extend `AbstractRepositoryAdapter<T, ID, E>`: ReceiptRepositoryAdapter, PriceSummaryRepositoryAdapter
  - `AbstractRepositoryAdapter` gained `findAll(PageRequestDto, String, String)` default implementation — fixed 5 compile errors at once
  - ~12 duplicate CRUD method declarations removed across port interfaces
  - All custom query methods preserved unchanged in every port/adapter
  - ReceiptItem skipped (no standalone ID concept — child entity pattern)
  - Branch: `feature/20260604-generic-repository-refactor` → PR #117 (open)
  - Verdict: 99/100 (mvn verify: 859 tests, SpotBugs 0, PMD clean, ArchUnit 7/7, conventions pass, 52/52 smoke tests pass)
- **PMD Violations Fix + 8 Security Dependabot PRs Merged (2026-06-04)**:
  - Fixed 12 PMD violations exposed by PMD 7.17.0 upgrade: 10 `@FunctionalInterface` on port interfaces, 2 `@SuppressWarnings` (StoreDtoMapper MapStruct, DateUtils legacy bridge)
  - Branch: `feature/20260604-pmd-violations-fix` → PR #115 (merged)
  - Merged all 8 Dependabot PRs sequentially: #57 (maven-minor-patch 14 updates), #58 (OWASP DepCheck 9.2.0→12.2.2), #59 (Spotless 2.43.0→3.4.0), #60 (actions/labeler v5→v6), #61 (upload-artifact v4→v7), #62 (docker/setup-buildx v3→v4), #63 (docker/login v3→v4), #64 (GCP auth v2→v3)
  - Knowledge persisted: PMD 7.17.0 gotchas, MapStruct@FunctionalInterface rule, PMD pre-scan pattern
  - Verdict: 97/100 score (learner post-execution review PASS)
- **Smoke Test Data Cleanup (2026-06-04)**:
  - Added Cleanup folder to Postman collection (Price→Product→Store DELETE, reverse dep order, strict=false)
  - 52 total smoke test items (49 original + 3 cleanup)
  - Category/Unit/Feedback Question not deletable — no DELETE endpoint in API spec
  - Branch: `feature/20260604-smoketest-cleanup` → PR #112
  - Verified: 52/52 smoke tests pass, 0 failures
- **Config/Common Test Coverage (2026-06-04)**:
  - Created `CacheConfigurationTest`: verifies cache names, @EnableCaching, @Configuration annotations
  - Created `RateLimitConfigurationTest`: verifies interceptor registration in registry
  - Fixed `GlobalExceptionHandlerTest`: added missing `ConstraintViolationException` import, fixed `ConstraintViolationException` constructor (raw→wildcard type), fixed `HttpMessageNotReadableException` constructor (needs HttpInputMessage param)
  - Note: All 18 A) Common and 8/10 B) Config/Security test files already existed with comprehensive coverage. Only 2 missing files created.

## Current Focus
- **None** — PR #117 (Generic Repository Pattern Enforced) open for review. Ready for next task.

- **Infrastructure Test Coverage (2026-06-04)**:
  (superseded by Coverage 80% Initiative above)
- **Price Domain Test Coverage (2026-06-04)**:
  (superseded by Coverage 80% Initiative above)
  1. ✅ **ProductPriceQueryServiceTest** — 4 tests: null/empty storeIds, valid, empty repository result
  2. ✅ **PriceSummaryQueryServiceTest** — 4 tests: null/empty productIds, valid, empty repository result
  3. ✅ **PriceBatchProcessorTest** — 9 tests: weight unit path, non-weight unit, null product IDs filtered, zero prices edge case, null unit, empty prices, exception handling, summary timing update, null price values
  4. ✅ **PriceRepositoryAdapterTest** — 14 tests: save, findById, not found, saveAll, findByProductId, findByProductIdAndDateRange, findCheapest, findByProductIds, findAllByProductIds, findProductIdsByStoreIds, findByCriteria, deleteById, existsById, findAll (deprecated)
  5. ✅ **PriceMapperTest** — 6 tests: domain→entity, entity→domain, null inputs for both directions, null fields in both
  6. ✅ **PriceDtoMapperTest** — 11 tests: toPriceRecord, toResult, toResultV2, toCheapestPrice, null store for all, null date, null price
  7. ✅ **PriceWebAdapterTest** — extended with 5 CRUD tests: create, get, delete, update, list
  8. ✅ **PriceControllerTest** — 7 tests: create (201), get (200), delete (204), list (200), search, searchV2, update
  9. ✅ **PriceSummaryEventAdapterTest** — 1 test: publishes event correctly
  10. ✅ **PriceSummaryUpdateRequestedEventHandlerTest** — 2 tests: triggers batch, handles exception
  11. ✅ **ProductPricesCalculateJobTest** — 2 tests: registers with correct name, executes batch on run
  12. ✅ **PriceSummaryBatchSchedulerTest** — 2 tests: triggers batch, handles exception



## Active Decisions
- **GSD integration**: 11 `/gsd-*` commands available for structured phases (discuss, plan, execute, review, verify, ship, etc.)
- **Lean-ctx MCP**: Available at project level for token-efficient file ops
- **AGENTS.md trimmed**: 691→277 lines, reference content moved to skills
- **Parallel orchestrator**: Multi-service changes can fan-out across parallel subagents
- **Sub-agent modes established**: orchestrator=chat, planner=task, task-manager=single_turn, code-reviewer=task (Google ADK-inspired)
- **Skills enriched**: All 9 skills now have `tags`, `file_patterns`, and standardized `metadata.role` for richer LLM auto-matching
- **Permissions documented**: All 4 sub-agent files now have explicit `## Permissions` sections documenting read/write/execute boundaries
- **gitnexus_impact() enforced**: Embedded in all 4 sub-agent prompts as mandatory pre-edit step
- **Branching strategy**: Agents MUST create feature branch before development. `opencode.json` denies `git push origin main`. See `AGENTS.md` §1.1.

## Known Blockers
- **Pre-existing compilation errors** in other packages (`category`, `common`, `config`, `store`, `unit`) — prevent full `mvn test` run. All new receipt test files compile cleanly (zero errors).
- **None currently**. All 130 receipt domain tests pass.
- **None for agent config** — `.opencode/` files are gitignored, no quality gates apply


## Active Decisions (Provider Config)
- **Google Gemini & Sumopod Configured** (2026-06-02): `@ai-sdk/google` with models `gemini-3.5-flash`, `gemini-3.5-flash-lite`, `gemini-2.5-pro`, etc. Orchestrator uses `google/gemini-3.5-flash` as primary model. Subagents use `google/gemini-2.5-flash` and `google/gemini-2.5-flash-lite` to optimize cost and latency. Fallback chain: Google → Sumopod. NVIDIA NIM is configured as a provider but not used in the fallback chain to optimize cost and latency.

## Architecture Improvement Plan (2026-06-02)

### Analysis: Generic Abstractions Usage
Existing generics in `common/` (`AbstractGenericService`, `GenericRepositoryPort`, `AbstractRepositoryAdapter`, `AbstractCrudWebAdapter`) are **inconsistently adopted**:

| Service | `AbstractRepositoryAdapter` | `AbstractGenericService` | `GenericRepositoryPort` | `AbstractCrudWebAdapter` |
|---------|:--------------------------:|:------------------------:|:-----------------------:|:------------------------:|
| **Category** | ✅ | ✅ | ✅ | ✅ |
| **Store** | ✅ | ✅ | ✅ | ✅ |
| **Product** | ✅ | ✅ | ✅ | ✅ |
| **Price** | ✅ | ✅ | ✅ | ✅ |
| **Receipt** | ✅ (2026-06-04) | ❌ | ✅ (2026-06-04) | ❌ |
| **Alert** | ✅ (2026-06-04) | ❌ | ✅ (2026-06-04) | ❌ |
| **Activity** | ✅ | ✅ | ✅ | ✅ |
| **Unit** | ✅ | ✅ | ✅ | ✅ |
| **FeedbackQ** | ✅ | ✅ | ✅ | ✅ |
| **PriceSummary** | ✅ (2026-06-04) | ❌ | ✅ (2026-06-04) | ❌ |

### Phase 1 — Complete ✅ (2026-06-02)
1. **Store** ✅: `StoreRepositoryPort extends GenericRepositoryPort`, `StoreService extends AbstractGenericService`, `StoreWebAdapter extends AbstractCrudWebAdapter`
2. **Product** ✅: `ProductRepositoryPort extends GenericRepositoryPort`, `ProductService extends AbstractGenericService`, `ProductWebAdapter extends AbstractCrudWebAdapter`
3. **Price** ✅: `PriceRepositoryPort extends GenericRepositoryPort`, `PriceService extends AbstractGenericService`, `PriceWebAdapter extends AbstractCrudWebAdapter`
4. **Persistence adapters** ✅: Added missing `findAll(PageRequestDto, String, String)` to Store/Product/Price adapters
5. **Tests** ✅: 178/178 pass, test constructors adapted for explicit constructor pattern
6. **Totals**: 14 files changed, 146 insertions(+), 150 deletions(-)

### Phase 2 — Medium Value (Considered)
- Standardize cache annotation patterns across Store/Category adapters
- Review port interfaces for parameter explosion (e.g., `StoreInPort.findAll()` with 8 params → criteria object)

### Phase 3 — YAGNI (Skip)
- Shopping, LLM — domain-specific logic, generics add no value (no RepositoryPort for Shopping; LLM is a provider facade)
- ReceiptItem — no standalone ID (child entity pattern)
- Note: Receipt, Alert, PriceSummary, Activity, Unit, Category, FeedbackQuestion all now extend `GenericRepositoryPort` via their port interfaces

### Estimated Line Reductions
| File | Current | After | Savings |
|------|:-------:|:-----:|:-------:|
| StoreService | 109 | ~50 | -54% |
| StoreRepositoryPort | 26 | ~22 | -15% |
| StoreWebAdapter | 97 | ~80 | -17% |
| ProductService | 188 | ~165 | -12% |
| PriceService | 171 | ~150 | -12% |

## Recent Changes
| Date | Change |
|------|--------|
| 2026-06-04 | **Generic Repository Pattern Enforced**: 6 ports extend `GenericRepositoryPort<T, ID>` (Product, Store, Price, Alert, Receipt, PriceSummary). 2 adapters converted to `AbstractRepositoryAdapter` (Receipt, PriceSummary). `AbstractRepositoryAdapter` gained `findAll()` base impl. ~12 duplicate CRUD method declarations removed. 9 files changed. 859/859 tests pass. PR #117. |
|------|--------|
| 2026-06-04 | **Agent Orchestration System**: Created `.opencode/orchestration/template.json` shared envelope. Modified orchestrator prompt with envelope lifecycle, state machine (PASS/RETRY/BLOCKED), three-tier scoring pipeline (rule checks + LLM judge + combined verdict), retry loop (max 3 attempts). Added session survivability: checkpoint before delegation, STATE.md sync, ctx_session save on every persist. Session resume detection auto-loads envelope and updates STATE.md. All local commits on main, no pushes. |
|------|--------|
| 2026-06-04 | **Infrastructure Test Coverage**: Created 26 new test files across 6 domains (ActivityLog: 9, FeedbackQuestion: 4, Alert: 4, Shopping: 4, Admin: 4, System: 1). Covers repository adapters, mappers, controllers, web adapters, event adapters/handlers, AOP aspect, job registry/executor. Extended existing DtoMapper tests with all enum mappings and null inputs. All new files compile cleanly. |
| 2026-06-03 | **Application Layer Test Coverage**: Added 32 new unit tests across 7 modules (SystemService, AdminService, AlertService, ActivityLogService, LlmService, UnitService, FeedbackQuestionService). Fixed Lombok annotation processing for test compilation. Added null validation to AdminService.triggerJob. Resolved 28 checkstyle violations. 310/310 tests pass. Coverage increased from 34.3% → 44.0%. |
| 2026-06-03 | **Receipt Status Exception Refactoring**: Refactored `ReceiptService.getStatus` to throw `NotFoundException.receipt(id)` instead of returning `null` when a receipt is not found. Added 2 comprehensive unit tests in `ReceiptServiceTest`. All 186 unit tests and 45 Postman smoke tests pass. |
| 2026-06-03 | **Price Null Handling & Validation Refactoring**: Refactored `PriceWebAdapter.resolveRequest` to throw `IllegalArgumentException` for null product names and `NotFoundException` for missing products instead of returning null. Simplified `search` and `searchV2` by removing redundant null checks. Added a global exception handler for `MethodArgumentNotValidException` in `GlobalExceptionHandler` to map validation errors to `400 Bad Request` instead of `500 Internal Server Error`. Created `PriceWebAdapterTest` with 6 comprehensive unit tests. Added 7 negative test cases to the Postman smoke tests. All 184 unit tests and 45 Postman smoke tests pass. |
| 2026-06-02 | **Documentation audit**: Updated `AGENTS.md` (§1.1 branching strategy), `CONTRIBUTING.md` (Java 21, branching, quality gates, hexagonal structure), `DEVELOPER_GUIDE.md` (Java 21, branching), `ARCHITECTURE_HYBRID.md` (service table, common/packages), `PULL_REQUEST_TEMPLATE.md` (quality checklist), `STATE.md` (current focus, decisions, changes). |
| 2026-06-02 | **Branching strategy enforced**: `opencode.json` denies `git push origin main`. `AGENTS.md` §1.1 mandates feature branch creation before development. |
| 2026-06-02 | **Phase 2 criteria refactoring COMPLETE**: `CategoryCriteria` + `CategoryInPort.findAll(CategoryCriteria)`, `FeedbackQuestionService.findAll(FeedbackQuestionCriteria)` fixed. 178/178 tests pass. |
| 2026-06-02 | **Google Gemini added as 1st provider**: `opencode.json` updated with `@ai-sdk/google`, API key, gemini-2.5-pro/flash/2.0-flash models. All 4 agents re-targeted to `google/gemini-2.5-pro` with Google→NVIDIA→OpenRouter→Sumopod fallback chain. |
| 2026-06-02 | **Phase 2 cache standardization started**: `STORES_CACHE` and `FEEDBACK_QUESTIONS_CACHE` constants + custom cache registrations added to `CacheConfiguration.java`. Category eviction implemented. Adapter constant references standardized. Criteria objects for Store, Price, and Unit ports implemented. Still pending: criteria objects for ActivityLog ports. |
| 2026-06-02 | **Phase 1 generification COMPLETE**: Store/Product/Price all extend generic abstractions. 14 files, 146+/-150 lines. 178/178 tests pass. |
| 2026-06-02 | Added architecture improvement plan to STATE.md with Phase 1-3 priorities |
| 2026-06-02 | Fixed deepseek-ai/deepseek-v4-flash → deepseek-ai/deepseek-v4-pro in opencode.json (4 agents, was timing out on NIM) |
| 2026-06-02 | NIM model audit: verified 5 working models, confirmed deepseek-v4-flash broken (times out) |
| 2026-06-02 | Agent config hardening: enriched 9 SKILL.md frontmatter (tags, file_patterns, role); added Permissions sections to 4 agent files; embedded gitnexus_impact in all agents; sub-agent modes in AGENTS.md |
| 2026-06-01 | AI config optimization pass 2: removed dead plugins/skills, improved GSD prompts, trimmed PROJECT.md |
| 2026-06-01 | AI config optimization pass 1: 6→4 agents, GSD wired, AGENTS.md →277 lines, craft deleted, lean-ctx added |

## Quality Metrics
- **Tests**: 859 (ArchUnit 7 + unit tests 852) — run via `mvn test`
- **Smoke Tests**: 52 Postman assertions — run via `npx newman run "postman/Goods Price Comparison Service.postman_collection.json"`
- **Gates**: Spotless → Checkstyle → ArchUnit → JaCoCo → SpotBugs → PMD CPD → check-conventions.sh → Newman smoke tests
- **Coverage**: 91.1% INSTRUCTION / 79.2% BRANCH (project-wide, only entity/dto/constant/exception/domain model/Application excluded)
