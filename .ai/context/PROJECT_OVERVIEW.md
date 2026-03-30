# Project Overview

## Goods Price Comparison Service

A Spring Boot microservice that extracts product prices from receipt images using OCR, stores them in PostgreSQL, and provides price comparison and shopping optimization APIs.

---

## 🎯 Purpose

**Problem:** Shoppers waste time and money by not knowing which store offers the best price for groceries and daily necessities.

**Solution:** This service allows users to:
- 📸 Scan receipt images from any store
- 🤖 Automatically extract product names, prices, and quantities using OCR
- 🗄️ Store price data in a searchable database
- 🔍 Query "Where can I buy X the cheapest?"
- 🗺️ Optimize shopping routes across multiple stores
- 📊 Track price history and trends over time
- 🔔 Get alerts when prices drop or promotions appear

---

## 🏗️ Architecture

### Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Framework** | Spring Boot | 3.3.0 |
| **Language** | Java | 17+ |
| **Database** | PostgreSQL | 14+ (Production), H2 (Local) |
| **Migration** | Flyway | Auto-run |
| **Build Tool** | Maven | 3.9+ |
| **Testing** | JUnit 5 + Testcontainers | Latest |
| **Code Quality** | JaCoCo | 0.8.11 |

### API Integration

This service **imports OpenAPI specifications** from:
- **Repository:** `goods-price-comparison-api`
- **Purpose:** Defines REST API contracts
- **Usage:** Generates Java DTOs via Maven dependency

### Database Schema

```
stores (50-100 records)
├── store_id (PK)
├── name (Unique)
├── location
└── created_at

products (1,000-10,000 records)
├── product_id (PK)
├── name (Unique)
├── category
├── unit
└── created_at

price_records (100K-10M records)
├── record_id (PK)
├── product_id (FK)
├── store_id (FK)
├── price
├── quantity
├── unit_price
├── is_promo
├── source_image
├── date_recorded
└── created_at
```

---

## 📁 Project Structure

```
goods-price-comparison-service/
├── .ai/                    # AI agent guidelines
│   ├── AGENTS.md          # Main guidelines
│   ├── rules/             # Standards
│   │   ├── CODING_STANDARDS.md
│   │   └── PR_WORKFLOW.md
│   ├── skills/            # How-to guides
│   │   ├── JAVA.md
│   │   └── TESTING.md
│   └── context/           # Project info
│       └── PROJECT_OVERVIEW.md (this file)
├── docs/                   # Human documentation
│   ├── API.md
│   ├── ROADMAP.md
│   ├── ARCHITECTURE.md
│   ├── DATABASE.md
│   ├── DEPLOYMENT.md
│   └── TESTING.md
├── src/
│   ├── main/java/com/example/goodsprice/
│   │   ├── config/        # Configuration classes
│   │   ├── controller/    # REST controllers
│   │   ├── dto/          # Data transfer objects
│   │   ├── exception/    # Custom exceptions
│   │   ├── model/        # Entity/domain classes
│   │   ├── repository/   # Database repositories
│   │   ├── service/      # Business logic
│   │   └── util/         # Utility classes
│   └── test/java/        # Tests
└── pom.xml               # Maven configuration
```

---

## 🚀 Development Workflow

### Local Development

```bash
# Run with H2 database (no PostgreSQL needed)
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Access H2 Console
open http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:price_comparison
```

### Production Build

```bash
# Full verification
mvn clean verify

# Run with PostgreSQL
mvn spring-boot:run
```

---

## 📊 Current Status

**Phase:** Phase 1 - Week 1 ✅ Completed
**Next:** Phase 1 - Week 2 (Core Entities & Repositories)

### Completed ✅
- Spring Boot project setup
- Maven configuration with dependencies
- Application profiles (local, production)
- Database configuration (PostgreSQL + H2)
- Flyway migrations setup
- Testing framework (JUnit 5, Testcontainers)
- Code quality tools (JaCoCo, Checkstyle, SpotBugs)

### In Progress 🔄
- Database migrations (stores, products, price_records)
- Entity classes (Store, Product, PriceRecord)
- JPA repositories
- Unit tests (100% coverage)

---

## 🔗 Related Projects

- [goods-price-comparison-api](https://github.com/RizkiRachman/goods-price-comparison-api) - OpenAPI specifications
- [common-utils-java](https://github.com/RizkiRachman/common-utils-java) - Shared utilities
- [common-exception-java](https://github.com/RizkiRachman/common-exception-java) - Shared exceptions

---

## 📞 Contact

**Maintainer:** Rizki Rachman  
**Email:** rizkifaizalr@gmail.com  
**GitHub:** [@RizkiRachman](https://github.com/RizkiRachman)

---

*Last updated: 2026-03-30*
