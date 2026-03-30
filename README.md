# Goods Price Comparison Service 🛒💰

A smart microservice that extracts product prices from receipt images using OCR (Optical Character Recognition), stores them in a centralized database, and helps users find the cheapest goods across multiple stores.

## 🎯 Purpose

**Problem**: Shoppers waste time and money by not knowing which store offers the best price for their groceries and daily necessities.

**Solution**: This service allows users to:
- 📸 Scan receipt images from any store
- 🤖 Automatically extract product names, prices, and quantities using OCR
- 🗄️ Store price data in a searchable database
- 🔍 Query "Where can I buy X the cheapest?"
- 🗺️ Optimize shopping routes: "Buy these 5 items at Store A, these 3 at Store B"
- 📊 Track price history and trends over time
- 🔔 Get alerts when prices drop or promotions appear

**Target Users**:
- Smart shoppers looking to save money
- Families managing monthly budgets
- Students comparing prices
- Anyone who wants to optimize their grocery shopping

---

## 🏗️ Architecture Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT APPLICATIONS                       │
│  (Mobile App / Web Dashboard / Admin Portal)                │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       │ HTTP/REST API
                       ▼
┌─────────────────────────────────────────────────────────────┐
│              GOODS-PRICE-COMPARISON-SERVICE                  │
│                     (Spring Boot)                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   Receipt    │  │    OCR       │  │   Price      │      │
│  │   Upload     │──│   Service    │──│  Extraction  │      │
│  │   (Image)    │  │(Google Vision│  │   (Parser)   │      │
│  └──────────────┘  │   /Tesseract)│  └──────────────┘      │
│                    └──────────────┘           │             │
│                                               ▼             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │  Product     │  │   Store      │  │  Shopping    │      │
│  │   Query      │  │   Lookup     │  │ Optimization │      │
│  │   Service    │  │   Service    │  │   Engine     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│                                                               │
└──────────────────────┬──────────────────────────────────────┘
                        │
                        │ JDBC
                        ▼
┌─────────────────────────────────────────────────────────────┐
│              PostgreSQL Database*                            │
│  ┌────────────┐  ┌────────────┐  ┌────────────────────┐    │
│  │   stores   │  │  products  │  │   price_records    │    │
│  │   (50-100) │  │  (1K-10K)  │  │    (100K-10M)      │    │
│  └────────────┘  └────────────┘  └────────────────────┘    │
└─────────────────────────────────────────────────────────────┘

*For local development, H2 in-memory database can be used instead
```

### Flow Steps:

1. **Image Upload** 📤
   - User takes photo of receipt
   - Uploads via mobile app or web
   - Image stored temporarily

2. **OCR Processing** 🤖
   - Google Vision API or Tesseract extracts text
   - Raw text parsed into structured data
   - Product names, prices, quantities identified

3. **Data Storage** 💾
   - Extracted data normalized
   - Products matched to existing catalog or created new
   - Price records stored with store, date, and source image

4. **Query & Analysis** 🔍
   - Users search: "Where is Ultra Milk cheapest?"
   - System queries database and ranks by price
   - Shopping lists optimized for multiple items

5. **Shopping Route Optimization** 🗺️
   - Algorithm finds store with most items
   - Remaining items matched to cheapest individual stores
   - Route generated with GPS coordinates

---

## 🛠️ Tech Stack

### Backend Service

| Technology | Version | Purpose |
|------------|---------|---------|
| **Spring Boot** | 3.x | Main framework, REST API |
| **Java** | 17+ | Programming language |
| **PostgreSQL** | 14+ | Primary database |
| **Maven** | 3.9+ | Build tool |
| **Lombok** | Latest | Reduce boilerplate code |

### OCR & Image Processing

| Technology | Purpose |
|------------|---------|
| **Google Vision API** | Primary OCR (high accuracy) |
| **Tesseract** | Fallback OCR (open source) |
| **Apache Commons Imaging** | Image preprocessing |

### Shared Libraries (Reusable Components)

| Library | Purpose | Repository |
|---------|---------|------------|
| **common-utils-java** | StringUtils, utilities | [Link](https://github.com/RizkiRachman/common-utils-java) |
| **common-exception-java** | Standardized exceptions | [Link](https://github.com/RizkiRachman/common-exception-java) |

### Database

| Component | Purpose |
|-----------|---------|
| **PostgreSQL** | Relational data storage |
| **Flyway** | Database migrations (auto-run on startup) |
| **Partitioning** | Monthly partitions for price_records |
| **Indexes** | Optimized queries for product/store lookups |

### API & Integration

| Technology | Purpose |
|------------|---------|
| **REST API** | HTTP endpoints for client apps |
| **OpenAPI/Swagger** | API documentation |
| **Jackson** | JSON serialization |

### Testing

| Technology | Purpose |
|------------|---------|
| **JUnit 5** | Unit testing |
| **Mockito** | Mocking dependencies |
| **TestContainers** | Integration testing with PostgreSQL |

### DevOps & Deployment

| Technology | Purpose |
|------------|---------|
| **Docker** | Containerization |
| **Docker Compose** | Local development stack |
| **GitHub Actions** | CI/CD pipeline |

---

## 📚 Documentation

| Document | Description |
|----------|-------------|
| [API Documentation](docs/API.md) | REST API endpoints, request/response examples, error codes |
| [Project Roadmap](docs/ROADMAP.md) | Development phases, milestones, and timeline |
| [Architecture & Design](docs/ARCHITECTURE.md) | System architecture, diagrams, design decisions |
| [Database Schema](docs/DATABASE.md) | Schema details, migrations, optimization |
| [Testing Guide](docs/TESTING.md) | Test strategy, examples, coverage requirements |
| [Deployment Guide](docs/DEPLOYMENT.md) | Docker, Kubernetes, production setup |
| [AI Agent Guidelines](.ai/AGENTS.md) | For AI agents: coding standards, PR workflow |
| [Contributing Guide](CONTRIBUTING.md) | How to contribute, commit messages, PR process |
| [Changelog](CHANGELOG.md) | Version history and changes |

### 🤖 AI Documentation

> **For AI agents working on this project:**
> See [.ai/AGENTS.md](.ai/AGENTS.md) for guidelines including:
> - **PR workflow and strict merge rules** (explicit approval required)
> - Commit message guidelines (meaningful, simple)
> - 100% coverage requirements
> - Code quality standards
>
> **Important:** AI must ask for explicit merge permission every time. Never assume approval from previous sessions.

---

## 📊 Database Schema (3 Tables)

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

**Why 3 Tables?**
- ✅ Minimal storage (no duplication)
- ✅ Fast queries (2 JOINs max)
- ✅ Easy to maintain
- ✅ Scalable to millions of records
- ✅ Simple OCR integration

### Database Migrations (Flyway)

**No manual execution required!** Flyway runs automatically on application startup.

**How it works:**
1. Place SQL migration files in `src/main/resources/db/migration/`
2. Name format: `V1__create_stores_table.sql`
3. Spring Boot auto-detects and executes them
4. Flyway tracks applied migrations in `flyway_schema_history` table

**Example migration:**
```sql
-- V1__create_stores_table.sql
CREATE TABLE stores (
    store_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location VARCHAR(200),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Benefits:**
- ✅ Zero manual database setup
- ✅ Version controlled schema
- ✅ Auto-applies on startup
- ✅ Rollback support
- ✅ Works in all environments (dev, staging, prod)

---

## 🚀 Key Features

### 1. Receipt OCR 📸
- Upload receipt images (JPG, PNG, PDF)
- Automatic text extraction
- Smart parsing of products, prices, quantities
- Support for multiple receipt formats

### 2. Price Database 💾
- Historical price tracking
- Multiple store support
- Price trend analysis
- Promotion detection

### 3. Price Comparison 🔍
- "Where is X cheapest?" queries
- Price difference calculations
- Savings estimations
- Filter by date range

### 4. Shopping Optimization 🎯
- Input: Shopping list (10 items)
- Output: Optimized route
- Strategy: Max items per store + cheapest for remainder
- GPS navigation integration

### 5. Price Alerts 🔔
- Watchlist for favorite products
- Price drop notifications
- Promotion alerts
- Historical price charts

---

## 🏁 Getting Started

### Prerequisites

```bash
# Required software
- Java 17+
- Maven 3.9+
- PostgreSQL 14+ (for production only)
- Docker (optional, for local dev)

# Shared libraries (must be installed first)
- common-utils-java
- common-exception-java
```

### Quick Start (Local Development)

Run the application with H2 in-memory database (no PostgreSQL required):

```bash
# 1. Clone the repository
git clone https://github.com/RizkiRachman/goods-price-comparison-service.git
cd goods-price-comparison-service

# 2. Install shared libraries (skip if already installed)
cd ../common-utils-java && mvn clean install
cd ../common-exception-java && mvn clean install

# 3. Run with local profile (H2 database)
cd ../goods-price-comparison-service
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 4. Access H2 Console (optional)
open http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:price_comparison
```

### Production Setup (PostgreSQL)

For production deployment with PostgreSQL:

```bash
# 1. Setup database
createdb price_comparison
psql price_comparison < schema.sql

# 2. Run with default profile (uses application.properties)
mvn spring-boot:run
```

### Configuration Profiles

**Local Development** (`application-local.properties`):
- Uses H2 in-memory database
- No PostgreSQL required
- H2 console enabled at `/h2-console`
- Flyway disabled (auto DDL)

**Production** (`application.properties`):
- Uses PostgreSQL database
- Flyway migrations enabled
- All security features active

### Build & Test

```bash
# Build and run tests
mvn clean verify

# Run tests only
mvn clean test

# Build JAR
mvn clean package

# Run JAR with local profile
java -jar target/goods-price-comparison-service-1.0.0-SNAPSHOT.jar --spring.profiles.active=local
```

### Environment Variables

Create `.env` file for local development:

```properties
# Database (only needed for production profile)
DATABASE_URL=jdbc:postgresql://localhost:5432/price_comparison
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=your_password

# OCR Services
GOOGLE_VISION_API_KEY=your_google_vision_api_key

# File Upload
UPLOAD_DIR=/tmp/receipts
```

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/RizkiRachman/goods-price-comparison-service.git
cd goods-price-comparison-service

# 2. Install shared libraries
cd ../common-utils-java && mvn clean install
cd ../common-exception-java && mvn clean install

# 3. Setup database
createdb price_comparison
cd ../goods-price-comparison-service
psql price_comparison < schema.sql

# 4. Run the application
mvn spring-boot:run
```

### Configuration

Create `application-local.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/price_comparison
spring.datasource.username=your_username
spring.datasource.password=your_password

# OCR
ocr.google.vision.api.key=${GOOGLE_VISION_API_KEY}
ocr.fallback.enabled=true

# File Upload
upload.max.size=10MB
upload.temp.dir=/tmp/receipts
```

---

## 🧪 Testing Strategy

- **Unit Tests**: Service layer, OCR parser
- **Integration Tests**: Database, API endpoints
- **End-to-End Tests**: Full receipt upload → query flow
- **Load Tests**: Handle 1000+ receipts/day

---

## 🤝 Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for:
- Development setup
- Code standards
- Testing requirements
- **Commit message guidelines** (meaningful and clear)

**Quick start:**
```bash
git checkout -b feature/your-feature
git commit -m "Add clear description of changes"
git push origin feature/your-feature
# Then create PR
```

**Requirements:**
- All tests pass
- Meaningful commit messages
- Code coverage > 90%

**Note:** Coverage check temporarily disabled during initial setup phase.

---

## 📄 License

MIT License - see [LICENSE](LICENSE) file for details.

---

## 🔗 Related Projects

- [common-utils-java](https://github.com/RizkiRachman/common-utils-java) - Shared utility library
- [common-exception-java](https://github.com/RizkiRachman/common-exception-java) - Shared exception library
- [spring-boot-playground](https://github.com/RizkiRachman/spring-boot-playground) - Proof of concept

---

**Built with ❤️ using Spring Boot, Java, and Smart Shopping Principles**

*Helping you save money, one receipt at a time! 💰🛒*