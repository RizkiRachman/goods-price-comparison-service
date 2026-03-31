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

#### Receipts (Async Processing)
```
POST /v1/receipts/upload       # Upload receipt for async processing
GET  /v1/receipts/{id}/status  # Check processing status
GET  /v1/receipts/{id}/results # Get extracted data when complete
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

## Async Receipt Processing

The receipt upload endpoint uses **async processing** to handle long-running OCR operations without blocking the HTTP request.

### Flow

```
1. POST /v1/receipts/upload (Image)
   └─> Returns immediately with job ID (202 ACCEPTED)

2. GET /v1/receipts/{id}/status (Poll)
   └─> Returns: PROCESSING → COMPLETED/FAILED

3. GET /v1/receipts/{id}/results (When COMPLETED)
   └─> Returns extracted data (store, items, prices)
```

### Example: Upload Receipt

**Request:**
```bash
curl -X POST http://localhost:8080/v1/receipts/upload \
  -H "Content-Type: multipart/form-data" \
  -F "image=@receipt.jpg"
```

**Response (202 ACCEPTED):**
```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PROCESSING"
}
```

**Response (200 OK - Duplicate):**
```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED"
}
```

### Example: Check Status

**Request:**
```bash
curl http://localhost:8080/v1/receipts/550e8400-e29b-41d4-a716-446655440000/status
```

**Response (Processing):**
```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PROCESSING"
}
```

**Response (Completed):**
```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED"
}
```

**Response (Failed):**
```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "FAILED"
}
```

### Example: Get Results

**Request:**
```bash
curl http://localhost:8080/v1/receipts/550e8400-e29b-41d4-a716-446655440000/results
```

**Response (Success):**
```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "storeName": "DIAMOND",
  "storeLocation": "Poins Square",
  "date": "2026-03-29",
  "totalAmount": 1207206.00,
  "items": [
    {
      "productName": "Ultra Milk",
      "category": "Dairy",
      "quantity": 48,
      "unit": "pcs",
      "unitPrice": 5600.00,
      "totalPrice": 268800.00
    }
  ]
}
```

**Response (Not Ready):**
```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "storeName": null,
  "date": null,
  "totalAmount": null,
  "items": []
}
```

---

## Features

### Image Deduplication
- Calculates SHA-256 hash of uploaded images
- Returns existing result if same image uploaded again
- Prevents duplicate processing and API costs

### Retry Support
- Failed receipts can be re-uploaded
- System deletes old failed record and creates new one
- Fresh processing attempt with new job ID

### Status Tracking
- **PENDING**: Receipt uploaded, queued for processing
- **PROCESSING**: Currently extracting data from receipt
- **COMPLETED**: Successfully processed, results available
- **FAILED**: Error during processing, can retry

### Timeout & Error Handling
- Background processing has timeout protection
- Failed jobs don't block the system
- Automatic cleanup of resources

---

## Example Usage

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

| Endpoint | Controller | Business Logic | Status |
|----------|-----------|----------------|--------|
| System - Version | ✅ | ✅ Static | ✅ Complete |
| Receipts - Upload | ✅ | ✅ Async Processing | ✅ Complete |
| Receipts - Status | ✅ | ✅ Status Tracking | ✅ Complete |
| Receipts - Results | ✅ | ✅ Data Retrieval | ✅ Complete |
| Prices - Search | ✅ | 🚧 TODO | ⏳ Pending |
| Shopping - Optimize | ✅ | 🚧 TODO | ⏳ Pending |
| Products - Trend | ✅ | 🚧 TODO | ⏳ Pending |
| Alerts - Subscribe | ✅ | 🚧 TODO | ⏳ Pending |

**Legend:** ✅ Complete | 🚧 TODO | ⏳ Pending

### Recent Updates

**Async Receipt Processing (March 2026):**
- ✅ Non-blocking upload API
- ✅ Background processing with Spring Events
- ✅ Status tracking (PENDING → PROCESSING → COMPLETED/FAILED)
- ✅ Image deduplication using SHA-256
- ✅ Retry support for failed receipts
- ✅ Database persistence with audit trail
- ✅ Thread pool optimization
- ✅ Timeout and error handling

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
