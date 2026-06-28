# Awesome References — How These Catalogs Apply to This Project

Three curated knowledge catalogs are referenced as architecture benchmarks. This document maps their relevant sections to concrete project concerns.

## Awesome Scalability

Patterns of scalable, reliable, and performant large-scale systems.

| Project Concern | Relevant Pattern | Why |
|---|---|---|
| Price comparison across stores (parallel lookups) | CQRS / Command Query Responsibility Segregation | Read-heavy price queries vs write-heavy receipt ingestion benefit from separate read models |
| Receipt OCR processing | Event-driven architecture / Message queues | OCR jobs are async, benefit from backpressure and replay |
| LLM provider calls (price extraction, analysis) | Bulkhead / Circuit breaker | Provider latency spikes must not saturate worker threads |
| Product catalog caching | Cache-aside / Write-through | Price lookups dominate read traffic, caching reduces DB load by order of magnitude |
| Scheduled price refresh | Task scheduler patterns | Periodic jobs need idempotency and failure recovery |

## Awesome Design Patterns

Curated catalog of software and architecture design patterns.

| Pattern | Usage in Project |
|---|---|
| Adapter | AbstractRestLlmProvider — uniform interface across LLM providers |
| Strategy | Pluggable LLM extraction strategies, interchangeable |
| Repository | Spring Data JPA repositories abstract storage |
| Observer / Event Listener | Spring event publishing for OCR completion, price updates |
| Chain of Responsibility | OCR pipeline: image preprocess → detect → extract → validate |
| Factory | Provider factory for LLM client instantiation |
| DTO | Receipt DTO, PriceComparison DTO across service boundaries |
| Exception Shield | Common-exception-java error hierarchy |

## Awesome Java

Curated list of frameworks, libraries, and software for the Java ecosystem.

| Catalog Section | Used In Project |
|---|---|
| Build Tools | Apache Maven (pom.xml) |
| Frameworks | Spring Boot (Web, Data JPA, Kafka, Actuator) |
| Database | PostgreSQL with Spring Data JPA |
| Testing | JUnit 5, Mockito, ArchUnit, Testcontainers |
| Concurrency | java.util.concurrent, Spring @Async |
| Serialization | Jackson via Spring Boot starter |
| Logging | Logback (Spring Boot default) |
| Monitoring | Spring Actuator, Micrometer |
