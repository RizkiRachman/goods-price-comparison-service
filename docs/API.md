# API Documentation

## Overview

This service implements the **Goods Price Comparison API** specification defined in the separate API repository.

**Base URL:** `http://localhost:8080`

**API Specification Repository:** https://github.com/RizkiRachman/goods-price-comparison-api

---

## Interactive Documentation

When the application is running, access the interactive Swagger UI:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/api-docs

---

## OpenAPI Specification

The complete API specification is maintained in the `goods-price-comparison-api` repository:

### Complete API Spec
- **[openapi-bundled.yaml](https://github.com/RizkiRachman/goods-price-comparison-api/blob/main/src/main/resources/openapi/openapi-bundled.yaml)** - Complete bundled OpenAPI specification with all endpoints

---

## Quick Reference

### Available Endpoints

#### System
```
GET /v1/version
```

#### Receipts
```
POST /v1/receipts/upload
GET  /v1/receipts/{id}/status
GET  /v1/receipts/{id}/results
```

#### Prices
```
POST /v1/prices/search
POST /v2/prices/search
```

#### Shopping
```
POST /v1/shopping/optimize
```

#### Products
```
GET /v1/products/trend/{productId}
```

#### Alerts
```
POST /v1/alerts/subscribe
```

---

## Example Usage

### Upload Receipt
```bash
curl -X POST http://localhost:8080/v1/receipts/upload \
  -H "Content-Type: multipart/form-data" \
  -F "image=@receipt.jpg"
```

### Search Prices
```bash
curl -X POST http://localhost:8080/v1/prices/search \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Milk",
    "location": "Jakarta"
  }'
```

---

## Generated DTOs

The API specification generates Java DTOs used by this service:

**Package:** `com.example.goodsprice.api.model`

**Maven Dependency:**
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>goods-price-comparison-api</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

**To update DTOs:**
```bash
cd ../goods-price-comparison-api && mvn clean install
cd ../goods-price-comparison-service && mvn clean compile
```

---

## Implementation Status

| Endpoint | Controller | Business Logic |
|----------|-----------|----------------|
| System - Version | ✅ | ✅ Static |
| Receipts - Upload | ✅ | 🚧 TODO |
| Receipts - Status | ✅ | 🚧 TODO |
| Receipts - Results | ✅ | 🚧 TODO |
| Prices - Search | ✅ | 🚧 TODO |
| Shopping - Optimize | ✅ | 🚧 TODO |
| Products - Trend | ✅ | 🚧 TODO |
| Alerts - Subscribe | ✅ | 🚧 TODO |

**Legend:** ✅ Implemented | 🚧 TODO

---

## Development Tools

- **H2 Console:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:price_comparison`
  - User: `sa`
  - Password: *(empty)*

---

## Related Documentation

- [Configuration Guide](CONFIGURATION.md)
- [Architecture](../ARCHITECTURE.md)
- [Database](../DATABASE.md)

---

*Last updated: This documentation references the OpenAPI specification from [goods-price-comparison-api](https://github.com/RizkiRachman/goods-price-comparison-api)*
