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

### Async Receipt Processing Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    CLIENT APPLICATIONS                       │
│  (Mobile App / Web Dashboard / Admin Portal)                │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       │ POST /receipts (Image)
                       ▼
┌─────────────────────────────────────────────────────────────┐
│              GOODS-PRICE-COMPARISON-SERVICE                  │
│                     (Spring Boot)                            │
│                                                               │
│  ┌──────────────────────────────────────────────────────┐  │
│  │           Receipt Upload API (Sync)                   │  │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────────────┐   │  │
│  │  │ Calculate│  │ Check DB │  │ Create Receipt   │   │  │
│  │  │  Hash    │──│Duplicate?│──│ (PENDING status) │   │  │
│  │  └──────────┘  └──────────┘  └──────────────────┘   │  │
│  │                          │                          │  │
│  │                          ▼                          │  │
│  │                   ┌──────────────┐                  │  │
│  │                   │ Return 202   │                  │  │
│  │                   │ with Job ID  │                  │  │
│  │                   └──────────────┘                  │  │
│  └──────────────────────────────────────────────────────┘  │
│                          │                                   │
│                          │ Fire Event (After Tx Commit)      │
│                          ▼                                   │
│  ┌──────────────────────────────────────────────────────┐  │
│  │         Async Processor (Background Thread)           │  │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────────────┐   │  │
│  │  │ PROCESS  │──│   LLM    │──│ Store Results    │   │  │
│  │  │  STATUS  │  │  (Gemini)│  │ (COMPLETED/FAILED│   │  │
│  │  └──────────┘  └──────────┘  └──────────────────┘   │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                               │
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
│              PostgreSQL Database                             │
│  ┌──────────┐  ┌──────────┐  ┌────────────────────────┐   │
│  │ receipts │  │  stores  │  │    prices              │   │
│  │(async    │  │  (50-100)│  │    (100K-10M)          │   │
│  │ tracking)│  └──────────┘  └────────────────────────┘   │
│  └──────────┘  ┌──────────┐  ┌────────────────────────┐   │
│                │ products │  │   receipt_items        │   │
│                │(1K-10K)  │  │   (per-receipt lines) │   │
│                └──────────┘  └────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Async Flow Steps:

1. **Image Upload** 📤 (Non-blocking)
   - User takes photo of receipt
   - Uploads via mobile app or web
   - System calculates SHA-256 hash for deduplication
   - If duplicate (COMPLETED/PROCESSING): return existing job ID
   - If duplicate (FAILED): delete old record and retry
   - Creates receipt record with PENDING status
   - Returns immediately with job ID (202 ACCEPTED)

2. **Background Processing** ⚙️ (Async)
   - Spring Event fires after transaction commits
   - Async processor picks up the job
   - Updates status to PROCESSING
   - Google Gemini extracts text and structure
   - Stores results (store, date, items, prices) as JSON
   - Updates status to COMPLETED or FAILED

3. **Status Polling** 📊
   - Client polls `GET /receipts/{id}/status`
   - Returns: PENDING → PROCESSING → COMPLETED/FAILED
   - When COMPLETED, fetch results with `GET /receipts/{id}/results`

4. **Retry Support** 🔄
   - If processing fails, receipt marked as FAILED
   - User can re-upload same image (new attempt)
   - Old failed record deleted, new one created
   - Fresh processing with same or different result

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
| **Flyway** | Database migrations (via Maven profile) |
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
| **Kubernetes** | Container orchestration |
| **Vault** | Secrets management |
| **Terraform** | Infrastructure as code |
| **Kind** | Local Kubernetes development |

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

## 📊 Database Schema (5 Tables)

```
stores (50-100 records)
├── id (PK, BIGINT)
├── name (VARCHAR 255)
├── location (VARCHAR 255)
├── created_at, updated_at

products (1,000-10,000 records)
├── id (PK, BIGINT)
├── name (VARCHAR 255)
├── category (VARCHAR 255)
├── unit (VARCHAR 255)
├── created_at, updated_at

receipts (async tracking)
├── id (PK, UUID)
├── image_hash (UNIQUE)
├── original_filename
├── status (PENDING/PROCESSING/COMPLETED/FAILED)
├── error_message
├── store_name, store_location, receipt_date
├── total_amount, extracted_data
├── created_at, updated_at, processed_at

prices (100K-10M records)
├── id (PK, BIGINT)
├── product_id (FK → products)
├── store_id (FK → stores)
├── price, unit_price
├── date_recorded
├── is_promo
├── created_at, updated_at

receipt_items (per-receipt line items)
├── id (PK, BIGINT)
├── receipt_id (FK → receipts)
├── product_name, category
├── quantity, unit_price, total_price, unit
```

**Why 5 Tables?**
- ✅ Minimal storage (no duplication)
- ✅ Fast queries (2 JOINs max)
- ✅ Easy to maintain
- ✅ Scalable to millions of records
- ✅ Receipt tracking with async processing
- ✅ Per-item receipt line extraction

### Database Migrations (Flyway)

Flyway is **disabled on application startup** and run via Maven profile for CI/CD control.

**Migration structure:**
```
db/migration/
├── tables/              ← CREATE TABLE scripts (per-table)
│   ├── V1__create_stores_table.sql
│   ├── V2__create_products_table.sql
│   ├── V3__create_receipts_table.sql
│   ├── V4__create_prices_table.sql
│   └── V5__create_receipt_items_table.sql
├── alter/               ← ALTER TABLE scripts (future)
└── data/               ← Seed data scripts (future)
```

**Run migrations:**
```bash
# Migrate (apply pending)
mvn flyway:migrate -Pflyway \
  -Ddatabase-name=goods-price-service \
  -Ddatabase-username=your_user \
  -Ddatabase-password=your_password

# Check status
mvn flyway:info -Pflyway

# Validate
mvn flyway:validate -Pflyway
```

**During tests:** Flyway runs automatically against H2 in-memory database with `ddl-auto=validate`.

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

### Quick Start

```bash
# 1. Clone the repository
git clone https://github.com/RizkiRachman/goods-price-comparison-service.git
cd goods-price-comparison-service

# 2. Install shared libraries (skip if already installed)
cd ../common-utils-java && mvn clean install
cd ../common-exception-java && mvn clean install

# 3. Build and test
cd ../goods-price-comparison-service
mvn clean verify

# 4. Run database migrations (requires PostgreSQL)
mvn flyway:migrate -Pflyway \
  -Ddatabase-name=goods-price-service \
  -Ddatabase-username=your_user \
  -Ddatabase-password=your_password

# 5. Run the application
mvn spring-boot:run
```

### Configuration

The application uses PostgreSQL with parameterized credentials. Set these properties via environment variables, Maven `-D` flags, or a properties file:

| Parameter | Description | Default |
|-----------|-------------|---------|
| `database-name` | Database name | - |
| `database-username` | Database user | - |
| `database-password` | Database password | - |
| `DATABASE_HOST` | PostgreSQL host | `localhost` |
| `DATABASE_PORT` | PostgreSQL port | `5432` |

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

```properties
# Database
database-name=goods-price-service
database-username=your_user
database-password=your_password
DATABASE_HOST=localhost
DATABASE_PORT=5432

# LLM
GEMINI_API_KEY=your_gemini_api_key
```

### Project Structure

```
com.example.goodsprice
├── Application.java
├── config/                          ← Application-wide configs
│   ├── AsyncConfiguration.java
│   └── CacheConfiguration.java
├── controller/                      ← REST controllers
└── module/                          ← Domain modules
    ├── llm/
    │   ├── config/LlmProperties.java
    │   ├── LLMProvider.java
    │   ├── LLMService.java
    │   ├── LlmProviderFactory.java
    │   ├── GeminiLLMProvider.java
    │   └── LocalLLMProvider.java
    ├── price/
    │   ├── entity/Price.java
    │   └── repository/PriceRepository.java
    ├── product/
    │   ├── entity/Product.java
    │   └── repository/ProductRepository.java
    ├── receipt/
    │   ├── entity/ (Receipt, ReceiptItem, ReceiptStatus)
    │   ├── dto/ (ReceiptResult, ReceiptUploadData, ReceiptUploadResult)
    │   ├── repository/ (ReceiptRepository, ReceiptItemRepository)
    │   ├── service/ReceiptService.java
    │   └── event/ (ReceiptProcessEvent, ReceiptProcessEventListener)
    └── store/
        ├── entity/Store.java
        └── repository/StoreRepository.java
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

- [dev-infrastructure](https://github.com/RizkiRachman/dev-infrastructure) - Initial setup tools and infrastructure configuration
- [common-utils-java](https://github.com/RizkiRachman/common-utils-java) - Shared utility library
- [common-exception-java](https://github.com/RizkiRachman/common-exception-java) - Shared exception library
- [spring-boot-playground](https://github.com/RizkiRachman/spring-boot-playground) - Proof of concept

---

**Built with ❤️ using Spring Boot, Java, and Smart Shopping Principles**

*Helping you save money, one receipt at a time! 💰🛒*