# API Documentation

## Overview

REST API endpoints for the Goods Price Comparison Service.

**Base URL**: `http://localhost:8080/api`

**Content-Type**: `application/json`

---

## Endpoints

### 1. Receipt Upload

Upload receipt image for OCR processing.

**Endpoint:** `POST /receipts/upload`

**Request:**
```bash
curl -X POST http://localhost:8080/api/receipts/upload \
  -H "Content-Type: multipart/form-data" \
  -F "image=@receipt.jpg"
```

**Response:**
```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "PROCESSING",
  "message": "Receipt uploaded successfully. Processing started."
}
```

**Status Codes:**
- `202 Accepted` - Upload successful, processing started
- `400 Bad Request` - Invalid image format or size
- `413 Payload Too Large` - Image exceeds 10MB limit

---

### 2. Check OCR Status

Check the processing status of an uploaded receipt.

**Endpoint:** `GET /receipts/{id}/status`

**Request:**
```bash
curl http://localhost:8080/api/receipts/550e8400-e29b-41d4-a716-446655440000/status
```

**Response:**
```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED",
  "progress": 100,
  "message": "OCR processing completed successfully"
}
```

**Status Values:**
- `PROCESSING` - OCR in progress
- `COMPLETED` - Successfully processed
- `FAILED` - Processing failed

**Status Codes:**
- `200 OK` - Status retrieved
- `404 Not Found` - Job ID not found

---

### 3. Get OCR Results

Retrieve extracted data from a processed receipt.

**Endpoint:** `GET /receipts/{id}/results`

**Request:**
```bash
curl http://localhost:8080/api/receipts/550e8400-e29b-41d4-a716-446655440000/results
```

**Response:**
```json
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "storeName": "DIAMOND",
  "storeLocation": "Poins Square",
  "date": "2026-03-29",
  "items": [
    {
      "productName": "Ultra Milk Plain Slim 200ml",
      "category": "Dairy",
      "quantity": 48,
      "unitPrice": 5600,
      "totalPrice": 268800,
      "unit": "bottle"
    },
    {
      "productName": "Bawang Merah",
      "category": "Fresh Produce",
      "quantity": 0.18,
      "unitPrice": 55250,
      "totalPrice": 9945,
      "unit": "kg"
    }
  ],
  "totalAmount": 1207206
}
```

**Status Codes:**
- `200 OK` - Results retrieved
- `404 Not Found` - Job ID not found
- `409 Conflict` - Processing not yet completed

---

### 4. Search Prices

Search for product prices across stores.

**Endpoint:** `POST /prices/search`

**Request:**
```bash
curl -X POST http://localhost:8080/api/prices/search \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Ultra Milk Plain Slim",
    "dateRange": {
      "from": "2026-03-01",
      "to": "2026-03-29"
    },
    "location": "Jakarta Selatan",
    "sortBy": "price",
    "sortOrder": "asc"
  }'
```

**Response:**
```json
{
  "productName": "Ultra Milk Plain Slim 200ml",
  "results": [
    {
      "storeId": 1,
      "storeName": "DIAMOND",
      "storeLocation": "Poins Square",
      "price": 5600,
      "unitPrice": 5600,
      "dateRecorded": "2026-03-29",
      "isPromo": false
    },
    {
      "storeId": 2,
      "storeName": "Superindo",
      "storeLocation": "Multiple locations",
      "price": 5400,
      "unitPrice": 5400,
      "dateRecorded": "2026-03-28",
      "isPromo": true
    }
  ],
  "cheapest": {
    "storeName": "Superindo",
    "price": 5400,
    "savings": 200
  }
}
```

**Status Codes:**
- `200 OK` - Search completed
- `400 Bad Request` - Invalid search parameters
- `404 Not Found` - Product not found

---

### 5. Optimize Shopping Route

Get optimized shopping route for a list of items.

**Endpoint:** `POST /shopping/optimize`

**Request:**
```bash
curl -X POST http://localhost:8080/api/shopping/optimize \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      "Ultra Milk Plain Slim 200ml",
      "Bawang Merah",
      "Daging Giling Biasa",
      "Telur Ayam",
      "Beras Premium 5kg"
    ],
    "preferences": {
      "maxStores": 3,
      " prioritizePrice": true
    }
  }'
```

**Response:**
```json
{
  "totalItems": 5,
  "totalCost": 375200,
  "storesToVisit": 2,
  "route": [
    {
      "storeId": 3,
      "storeName": "Superindo",
      "storeLocation": "Poins Square",
      "items": [
        {
          "productName": "Ultra Milk Plain Slim 200ml",
          "price": 5400,
          "quantity": 1
        },
        {
          "productName": "Telur Ayam",
          "price": 27500,
          "quantity": 1
        },
        {
          "productName": "Beras Premium 5kg",
          "price": 62000,
          "quantity": 1
        }
      ],
      "subtotal": 94900,
      "estimatedTime": "15 mins"
    },
    {
      "storeId": 4,
      "storeName": "Pasar Tradisional",
      "storeLocation": "Labak Bulus",
      "items": [
        {
          "productName": "Bawang Merah",
          "price": 45000,
          "quantity": 0.5
        },
        {
          "productName": "Daging Giling Biasa",
          "price": 125000,
          "quantity": 1
        }
      ],
      "subtotal": 170000,
      "estimatedTime": "10 mins"
    }
  ],
  "savings": {
    "comparedToSingleStore": 45200,
    "percentage": 10.7
  }
}
```

**Status Codes:**
- `200 OK` - Optimization completed
- `400 Bad Request` - Invalid items list
- `404 Not Found` - One or more items not found

---

### 6. Get Price Trend

Get price history/trend for a specific product.

**Endpoint:** `GET /products/trend/{productId}`

**Query Parameters:**
- `from` (optional) - Start date (YYYY-MM-DD)
- `to` (optional) - End date (YYYY-MM-DD)
- `granularity` (optional) - `daily`, `weekly`, `monthly` (default: daily)

**Request:**
```bash
curl "http://localhost:8080/api/products/trend/1?from=2026-01-01&to=2026-03-29&granularity=weekly"
```

**Response:**
```json
{
  "productId": 1,
  "productName": "Ultra Milk Plain Slim 200ml",
  "trend": [
    {
      "period": "2026-01-01",
      "avgPrice": 5800,
      "minPrice": 5600,
      "maxPrice": 6000,
      "dataPoints": 5
    },
    {
      "period": "2026-02-01",
      "avgPrice": 5750,
      "minPrice": 5500,
      "maxPrice": 5900,
      "dataPoints": 4
    },
    {
      "period": "2026-03-01",
      "avgPrice": 5600,
      "minPrice": 5400,
      "maxPrice": 5800,
      "dataPoints": 6
    }
  ],
  "trendDirection": "decreasing",
  "priceChange": -3.4
}
```

**Status Codes:**
- `200 OK` - Trend data retrieved
- `404 Not Found` - Product not found

---

### 7. Subscribe to Price Alerts

Subscribe to price drop alerts for a product.

**Endpoint:** `POST /alerts/subscribe`

**Request:**
```bash
curl -X POST http://localhost:8080/api/alerts/subscribe \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "targetPrice": 5000,
    "notificationMethod": "email",
    "email": "user@example.com"
  }'
```

**Response:**
```json
{
  "subscriptionId": "sub-12345",
  "status": "ACTIVE",
  "productName": "Ultra Milk Plain Slim 200ml",
  "currentPrice": 5600,
  "targetPrice": 5000,
  "message": "You will be notified when price drops to Rp 5,000 or below"
}
```

**Status Codes:**
- `201 Created` - Subscription created
- `400 Bad Request` - Invalid subscription data
- `404 Not Found` - Product not found

---

## Error Response Format

All errors follow this format:

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid request data",
    "details": [
      {
        "field": "productName",
        "message": "Product name is required"
      }
    ],
    "timestamp": "2026-03-29T10:30:00Z",
    "path": "/api/prices/search"
  }
}
```

**Common Error Codes:**
- `VALIDATION_ERROR` - Invalid input data
- `NOT_FOUND` - Resource not found
- `PROCESSING_ERROR` - OCR processing failed
- `RATE_LIMIT_EXCEEDED` - Too many requests
- `INTERNAL_ERROR` - Server error

---

## Rate Limiting

- **Default limit**: 100 requests per minute per IP
- **Upload limit**: 10 requests per minute per IP
- **Headers returned**:
  - `X-RateLimit-Limit`: 100
  - `X-RateLimit-Remaining`: 95
  - `X-RateLimit-Reset`: 1648556400

---

## Authentication

> **Note**: Authentication will be implemented in Phase 2

Current endpoints are public. Future versions will require:
- API Key in header: `X-API-Key: your-api-key`
- JWT token: `Authorization: Bearer <token>`

---

## Versioning

API version is included in URL path:
- Current: `/api/v1/...`
- Future: `/api/v2/...`

Version changes will be announced 30 days in advance.