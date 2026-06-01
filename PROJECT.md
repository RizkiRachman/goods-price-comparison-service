# Goods Price Comparison Service

## Vision
A modular, event-driven platform for aggregating, comparing, and alerting on goods prices across multiple stores. Enables users to track price history, set price alerts, and leverage LLM-powered receipt analysis.

## Tech Stack
- **Language**: Java 21
- **Framework**: Spring Boot 3.4
- **Build**: Maven
- **Architecture**: Hexagonal (ports & adapters) per service, event-driven between services
- **Database**: PostgreSQL (H2 in test)
- **API Spec**: OpenAPI-generated controllers from `goods-price-comparison-api:1.3.0`
- **Quality**: ArchUnit + SpotBugs + PMD CPD + Spotless (Google Java Style) + JaCoCo

## Architecture (Three-Layer Hybrid)
1. **Microservice boundaries** — 8 services under `com.example.goodsprice`: receipt, price, product, store, llm, shopping, alert, system
2. **Hexagonal per service** — `application/` (pure Java) + `infrastructure/` (Spring adapters)
3. **Event-driven** — Spring ApplicationEvent + @Async + @TransactionalEventListener(AFTER_COMMIT)


