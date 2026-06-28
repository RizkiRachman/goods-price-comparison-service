# Gap Remediation Plan

Critical gaps identified in the scalability analysis. Ordered by impact.

## P0 — Deploy Kafka (Medium Effort)

**Current:** Kafka adapter written, no broker running.

Steps:
1. Add Kafka Docker Compose service to docker-compose.yml
2. Define topic names as configuration properties
3. Create topic admin config with retention policies
4. Wire existing producer/consumer beans to topics
5. Add Docker healthcheck for Kafka availability
6. Update CI to include Kafka service for integration tests

**Dependency:** None

## P0 — Add Redis Cache (Low Effort)

**Current:** Every price lookup hits PostgreSQL.

Steps:
1. Add spring-boot-starter-data-redis dependency
2. Configure CacheManager with Redis (expiry: 5 min for prices)
3. Cache ProductPrice queries via @Cacheable("prices")
4. Cache Store queries via @Cacheable("stores")
5. Add Redis Docker Compose service
6. Add cache eviction on price update events (when Kafka is deployed)

**Dependency:** None

## P0 — Add Resilience4j (Low Effort)

**Current:** No circuit breaker, retry, or bulkhead on LLM provider calls.

Steps:
1. Add spring-boot-starter-aop and resilience4j-spring-boot3
2. Configure circuit breaker around AbstractRestLlmProvider.callLlm():
   - Sliding window: 10 calls
   - Failure threshold: 50%
   - Wait duration: 30s
3. Configure retry with exponential backoff (initial: 1s, max: 10s, multiplier: 2)
4. Configure bulkhead for LLM provider thread pool (max: 5 concurrent)
5. Add FallbackMethod returning cached/partial results
6. Expose Resilience4j Actuator endpoints for monitoring

**Dependency:** None

## P1 — Structured Logging (Low Effort)

**Current:** Plain text logback.

Steps:
1. Configure Logback with JSON encoder (logstash-logback-encoder)
2. Add correlation ID filter (MDC) — extract from request header or generate
3. Add context fields: receiptId, storeId, providerName, durationMs
4. Verify with jq that logs parse as JSON

## P1 — CI Enhancement (Medium Effort)

**Current:** Build only.

Steps:
1. Add Testcontainers integration tests to CI workflow
2. Add trivy-action for dependency vulnerability scan
3. Add OpenRewrite or OWASP plugin to Maven build
4. Add checkstyle/spotbugs to build phase

## P2 — Load Testing (Medium Effort)

**Current:** Unknown capacity.

Steps:
1. Set up k6 or Locust test suite
2. Define baseline scenarios: concurrent receipt submissions, price lookups
3. Define SLA: p95 < 500ms for price lookups, p95 < 30s for OCR
4. Run against staging environment post-fixes
5. Document capacity ceiling per dimension
