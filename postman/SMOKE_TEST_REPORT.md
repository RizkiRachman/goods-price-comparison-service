# Smoke Test Execution Report

**Date**: Wed, 03 Jun 2026 09:53:55 GMT
**Total Requests**: 45
**Passed Assertions**: All Passed (100%)

## Execution Summary

| Request Name | Method | URL | Status | Response Time |
| :--- | :---: | :--- | :---: | :---: |
| Version | **GET** | `http://localhost/v1/version` | `200 OK` | 39ms |
| Health | **GET** | `http://localhost/v1/health` | `200 OK` | 6ms |
| Metrics | **GET** | `http://localhost/v1/metrics` | `200 OK` | 6ms |
| Create | **POST** | `http://localhost/v1/categories` | `201 Created` | 75ms |
| List | **GET** | `http://localhost/v1/categories` | `200 OK` | 23ms |
| Get | **GET** | `http://localhost/v1/categories/TEST_CAT` | `200 OK` | 4ms |
| Update | **PUT** | `http://localhost/v1/categories/TEST_CAT` | `200 OK` | 11ms |
| Create | **POST** | `http://localhost/v1/units` | `201 Created` | 10ms |
| List | **GET** | `http://localhost/v1/units` | `200 OK` | 6ms |
| Get | **GET** | `http://localhost/v1/units/KG` | `200 OK` | 2ms |
| Update | **PUT** | `http://localhost/v1/units/KG` | `200 OK` | 6ms |
| Create | **POST** | `http://localhost/v1/stores` | `200 OK` | 17ms |
| List | **GET** | `http://localhost/v1/stores` | `200 OK` | 7ms |
| Get | **GET** | `http://localhost/v1/stores/1` | `200 OK` | 3ms |
| Update | **PUT** | `http://localhost/v1/stores/1` | `200 OK` | 7ms |
| Create Del | **POST** | `http://localhost/v1/stores` | `200 OK` | 4ms |
| Delete | **DELETE** | `http://localhost/v1/stores/2` | `204 No Content` | 6ms |
| Create | **POST** | `http://localhost/v1/products` | `200 OK` | 11ms |
| List | **GET** | `http://localhost/v1/products` | `200 OK` | 7ms |
| Get | **GET** | `http://localhost/v1/products/1` | `200 OK` | 3ms |
| Update | **PUT** | `http://localhost/v1/products/1` | `200 OK` | 7ms |
| Create Del | **POST** | `http://localhost/v1/products` | `200 OK` | 3ms |
| Delete | **DELETE** | `http://localhost/v1/products/2` | `204 No Content` | 4ms |
| Trend | **GET** | `http://localhost/v1/products/trend/1` | `200 OK` | 5ms |
| Create | **POST** | `http://localhost/v1/products/1/prices` | `201 Created` | 22ms |
| List | **GET** | `http://localhost/v1/products/1/prices` | `200 OK` | 11ms |
| Get | **GET** | `http://localhost/v1/prices/1` | `200 OK` | 3ms |
| Update | **PUT** | `http://localhost/v1/prices/1` | `200 OK` | 7ms |
| Create Del | **POST** | `http://localhost/v1/products/1/prices` | `201 Created` | 5ms |
| Delete | **DELETE** | `http://localhost/v1/prices/2` | `204 No Content` | 4ms |
| Search v1 | **POST** | `http://localhost/v1/prices/search` | `200 OK` | 27ms |
| Search v2 | **POST** | `http://localhost/v2/prices/search` | `200 OK` | 10ms |
| Search v1 - Null Product Name | **POST** | `http://localhost/v1/prices/search` | `400 Bad Request` | 9ms |
| Search v1 - Product Not Found | **POST** | `http://localhost/v1/prices/search` | `404 Not Found` | 3ms |
| Search v2 - Null Product Name | **POST** | `http://localhost/v2/prices/search` | `400 Bad Request` | 3ms |
| Search v2 - Product Not Found | **POST** | `http://localhost/v2/prices/search` | `404 Not Found` | 3ms |
| Get Non-Existent | **GET** | `http://localhost/v1/prices/99999` | `404 Not Found` | 3ms |
| Update Non-Existent | **PUT** | `http://localhost/v1/prices/99999` | `404 Not Found` | 4ms |
| Delete Non-Existent | **DELETE** | `http://localhost/v1/prices/99999` | `404 Not Found` | 3ms |
| Optimize | **POST** | `http://localhost/v1/shopping/optimize` | `200 OK` | 14ms |
| Create | **POST** | `http://localhost/v1/feedback-questions` | `201 Created` | 13ms |
| List | **GET** | `http://localhost/v1/feedback-questions` | `200 OK` | 6ms |
| Get | **GET** | `http://localhost/v1/feedback-questions/e960681d-6ea8-4206-94fe-ec1b26d2bddf` | `200 OK` | 2ms |
| List | **GET** | `http://localhost/v1/activity-logs` | `200 OK` | 9ms |
| Trigger Job | **POST** | `http://localhost/v1/admin/jobs/price-summary-update` | `200 OK` | 3ms |

---

## Detailed Request & Response Logs

### 1. Version

- **Endpoint**: `GET http://localhost/v1/version`
- **Status**: `200 OK`
- **Response Time**: 39ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "version": "1.8.0",
  "fullVersion": "1.8.0",
  "status": "stable",
  "supportedVersions": []
}
```

</details>

---

### 2. Health

- **Endpoint**: `GET http://localhost/v1/health`
- **Status**: `200 OK`
- **Response Time**: 6ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "status": "UP",
  "components": {
    "api": "UP",
    "database": "UP",
    "ocr": "UP"
  },
  "timestamp": "2026-06-03T09:53:43.470173Z",
  "version": "1.8.0"
}
```

</details>

---

### 3. Metrics

- **Endpoint**: `GET http://localhost/v1/metrics`
- **Status**: `200 OK`
- **Response Time**: 6ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "uptime": 5,
  "requests": {
    "total": 0,
    "successful": 0,
    "failed": 0,
    "ratePerMinute": 0
  },
  "responseTime": {
    "average": 0,
    "p50": 0,
    "p95": 0,
    "p99": 0
  },
  "errors": {
    "validationErrors": 0,
    "notFoundErrors": 0,
    "serverErrors": 0
  },
  "timestamp": "2026-06-03T09:53:43.486123Z"
}
```

</details>

---

### 4. Create

- **Endpoint**: `POST http://localhost/v1/categories`
- **Status**: `201 Created`
- **Response Time**: 75ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "id": "TEST_CAT",
  "name": "Test Category",
  "description": "Testing"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": "TEST_CAT",
  "name": "Test Category",
  "description": "Testing",
  "status": null,
  "createdAt": null,
  "updatedAt": null
}
```

</details>

---

### 5. List

- **Endpoint**: `GET http://localhost/v1/categories`
- **Status**: `200 OK`
- **Response Time**: 23ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "data": [
    {
      "id": "TEST_CAT",
      "name": "Test Category",
      "description": "Testing",
      "status": null,
      "createdAt": null,
      "updatedAt": null
    }
  ],
  "pagination": {
    "page": 1,
    "pageSize": 20,
    "totalItems": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": true
  }
}
```

</details>

---

### 6. Get

- **Endpoint**: `GET http://localhost/v1/categories/TEST_CAT`
- **Status**: `200 OK`
- **Response Time**: 4ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": "TEST_CAT",
  "name": "Test Category",
  "description": "Testing",
  "status": null,
  "createdAt": null,
  "updatedAt": null
}
```

</details>

---

### 7. Update

- **Endpoint**: `PUT http://localhost/v1/categories/TEST_CAT`
- **Status**: `200 OK`
- **Response Time**: 11ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "name": "Updated",
  "description": "Updated"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": "TEST_CAT",
  "name": "Updated",
  "description": "Updated",
  "status": null,
  "createdAt": null,
  "updatedAt": null
}
```

</details>

---

### 8. Create

- **Endpoint**: `POST http://localhost/v1/units`
- **Status**: `201 Created`
- **Response Time**: 10ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "id": "KG",
  "name": "Kilogram",
  "symbol": "kg",
  "type": "WEIGHT"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": "KG",
  "name": "Kilogram",
  "symbol": "kg",
  "type": "WEIGHT",
  "description": null,
  "status": null,
  "createdAt": null,
  "updatedAt": null
}
```

</details>

---

### 9. List

- **Endpoint**: `GET http://localhost/v1/units`
- **Status**: `200 OK`
- **Response Time**: 6ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "data": [
    {
      "id": "KG",
      "name": "Kilogram",
      "symbol": "kg",
      "type": "WEIGHT",
      "description": null,
      "status": null,
      "createdAt": null,
      "updatedAt": null
    }
  ],
  "pagination": {
    "page": 1,
    "pageSize": 20,
    "totalItems": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": true
  }
}
```

</details>

---

### 10. Get

- **Endpoint**: `GET http://localhost/v1/units/KG`
- **Status**: `200 OK`
- **Response Time**: 2ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": "KG",
  "name": "Kilogram",
  "symbol": "kg",
  "type": "WEIGHT",
  "description": null,
  "status": null,
  "createdAt": null,
  "updatedAt": null
}
```

</details>

---

### 11. Update

- **Endpoint**: `PUT http://localhost/v1/units/KG`
- **Status**: `200 OK`
- **Response Time**: 6ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "name": "Gram",
  "symbol": "g",
  "type": "WEIGHT"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": "KG",
  "name": "Gram",
  "symbol": "g",
  "type": "WEIGHT",
  "description": null,
  "status": null,
  "createdAt": null,
  "updatedAt": null
}
```

</details>

---

### 12. Create

- **Endpoint**: `POST http://localhost/v1/stores`
- **Status**: `200 OK`
- **Response Time**: 17ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "name": "Test Store",
  "location": "Test Loc",
  "chain": "Test"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": 1,
  "name": "Test Store",
  "location": "Test Loc",
  "chain": "Test",
  "address": null,
  "latitude": null,
  "longitude": null,
  "status": null,
  "createdAt": null
}
```

</details>

---

### 13. List

- **Endpoint**: `GET http://localhost/v1/stores`
- **Status**: `200 OK`
- **Response Time**: 7ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "data": [
    {
      "id": 1,
      "name": "Test Store",
      "location": "Test Loc",
      "chain": "Test",
      "address": null,
      "latitude": null,
      "longitude": null,
      "status": null,
      "createdAt": null
    }
  ],
  "pagination": {
    "page": 1,
    "pageSize": 20,
    "totalItems": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": true
  }
}
```

</details>

---

### 14. Get

- **Endpoint**: `GET http://localhost/v1/stores/1`
- **Status**: `200 OK`
- **Response Time**: 3ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": 1,
  "name": "Test Store",
  "location": "Test Loc",
  "chain": "Test",
  "address": null,
  "latitude": null,
  "longitude": null,
  "status": null,
  "createdAt": null
}
```

</details>

---

### 15. Update

- **Endpoint**: `PUT http://localhost/v1/stores/1`
- **Status**: `200 OK`
- **Response Time**: 7ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "name": "Updated",
  "location": "Upd",
  "chain": "Upd"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": 1,
  "name": "Updated",
  "location": "Upd",
  "chain": "Upd",
  "address": null,
  "latitude": null,
  "longitude": null,
  "status": null,
  "createdAt": null
}
```

</details>

---

### 16. Create Del

- **Endpoint**: `POST http://localhost/v1/stores`
- **Status**: `200 OK`
- **Response Time**: 4ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "name": "Del Store",
  "location": "Del",
  "chain": "Del"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": 2,
  "name": "Del Store",
  "location": "Del",
  "chain": "Del",
  "address": null,
  "latitude": null,
  "longitude": null,
  "status": null,
  "createdAt": null
}
```

</details>

---

### 17. Delete

- **Endpoint**: `DELETE http://localhost/v1/stores/2`
- **Status**: `204 No Content`
- **Response Time**: 6ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json

```

</details>

---

### 18. Create

- **Endpoint**: `POST http://localhost/v1/products`
- **Status**: `200 OK`
- **Response Time**: 11ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "name": "Test Product",
  "brand": "Test",
  "category": "TEST_CAT",
  "unit": "KG"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": 1,
  "name": "Test Product",
  "category": "TEST_CAT",
  "brand": "Test",
  "unit": "KG",
  "status": null,
  "createdAt": null,
  "updatedAt": null,
  "detail": null
}
```

</details>

---

### 19. List

- **Endpoint**: `GET http://localhost/v1/products`
- **Status**: `200 OK`
- **Response Time**: 7ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "data": [
    {
      "id": 1,
      "name": "Test Product",
      "category": "TEST_CAT",
      "brand": "Test",
      "unit": "KG",
      "status": null,
      "createdAt": null,
      "updatedAt": null,
      "detail": null
    }
  ],
  "pagination": {
    "page": 0,
    "pageSize": 20,
    "totalItems": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

</details>

---

### 20. Get

- **Endpoint**: `GET http://localhost/v1/products/1`
- **Status**: `200 OK`
- **Response Time**: 3ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": 1,
  "name": "Test Product",
  "category": "TEST_CAT",
  "brand": "Test",
  "unit": "KG",
  "status": null,
  "createdAt": null,
  "updatedAt": null,
  "detail": null
}
```

</details>

---

### 21. Update

- **Endpoint**: `PUT http://localhost/v1/products/1`
- **Status**: `200 OK`
- **Response Time**: 7ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "name": "Updated",
  "brand": "Upd",
  "category": "TEST_CAT",
  "unit": "KG"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": 1,
  "name": "Updated",
  "category": "TEST_CAT",
  "brand": "Upd",
  "unit": "KG",
  "status": null,
  "createdAt": null,
  "updatedAt": null,
  "detail": null
}
```

</details>

---

### 22. Create Del

- **Endpoint**: `POST http://localhost/v1/products`
- **Status**: `200 OK`
- **Response Time**: 3ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "name": "Del Prod",
  "brand": "Del",
  "category": "TEST_CAT",
  "unit": "KG"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": 2,
  "name": "Del Prod",
  "category": "TEST_CAT",
  "brand": "Del",
  "unit": "KG",
  "status": null,
  "createdAt": null,
  "updatedAt": null,
  "detail": null
}
```

</details>

---

### 23. Delete

- **Endpoint**: `DELETE http://localhost/v1/products/2`
- **Status**: `204 No Content`
- **Response Time**: 4ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json

```

</details>

---

### 24. Trend

- **Endpoint**: `GET http://localhost/v1/products/trend/1`
- **Status**: `200 OK`
- **Response Time**: 5ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "productId": 1,
  "productName": "Updated",
  "trend": null,
  "trendDirection": null,
  "priceChange": null
}
```

</details>

---

### 25. Create

- **Endpoint**: `POST http://localhost/v1/products/1/prices`
- **Status**: `201 Created`
- **Response Time**: 22ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "storeId": "1",
  "price": 10,
  "isPromo": false,
  "dateRecorded": "2026-06-03T00:00:00Z"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": 1,
  "productId": 1,
  "storeId": 1,
  "storeName": "Updated",
  "price": 10,
  "unitPrice": null,
  "dateRecorded": "2026-06-03T00:00:00Z",
  "isPromo": false,
  "availability": "in_stock",
  "status": "completed",
  "createdAt": null,
  "updatedAt": null
}
```

</details>

---

### 26. List

- **Endpoint**: `GET http://localhost/v1/products/1/prices`
- **Status**: `200 OK`
- **Response Time**: 11ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "data": [
    {
      "id": 1,
      "productId": 1,
      "storeId": 1,
      "storeName": "Updated",
      "price": 10,
      "unitPrice": null,
      "dateRecorded": "2026-06-03T00:00:00Z",
      "isPromo": false,
      "availability": "in_stock",
      "status": "completed",
      "createdAt": null,
      "updatedAt": null
    }
  ],
  "pagination": {
    "page": 1,
    "pageSize": 20,
    "totalItems": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": true
  }
}
```

</details>

---

### 27. Get

- **Endpoint**: `GET http://localhost/v1/prices/1`
- **Status**: `200 OK`
- **Response Time**: 3ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": 1,
  "productId": 1,
  "storeId": 1,
  "storeName": "Updated",
  "price": 10,
  "unitPrice": null,
  "dateRecorded": "2026-06-03T00:00:00Z",
  "isPromo": false,
  "availability": "in_stock",
  "status": "completed",
  "createdAt": null,
  "updatedAt": null
}
```

</details>

---

### 28. Update

- **Endpoint**: `PUT http://localhost/v1/prices/1`
- **Status**: `200 OK`
- **Response Time**: 7ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "price": 12,
  "isPromo": false
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": 1,
  "productId": 1,
  "storeId": 1,
  "storeName": "Updated",
  "price": 12,
  "unitPrice": null,
  "dateRecorded": "2026-06-03T00:00:00Z",
  "isPromo": false,
  "availability": "in_stock",
  "status": "completed",
  "createdAt": null,
  "updatedAt": null
}
```

</details>

---

### 29. Create Del

- **Endpoint**: `POST http://localhost/v1/products/1/prices`
- **Status**: `201 Created`
- **Response Time**: 5ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "storeId": "1",
  "price": 15,
  "isPromo": false,
  "dateRecorded": "2026-06-03T00:00:00Z"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": 2,
  "productId": 1,
  "storeId": 1,
  "storeName": "Updated",
  "price": 15,
  "unitPrice": null,
  "dateRecorded": "2026-06-03T00:00:00Z",
  "isPromo": false,
  "availability": "in_stock",
  "status": "completed",
  "createdAt": null,
  "updatedAt": null
}
```

</details>

---

### 30. Delete

- **Endpoint**: `DELETE http://localhost/v1/prices/2`
- **Status**: `204 No Content`
- **Response Time**: 4ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json

```

</details>

---

### 31. Search v1

- **Endpoint**: `POST http://localhost/v1/prices/search`
- **Status**: `200 OK`
- **Response Time**: 27ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "productName": "Updated"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "productName": "Updated",
  "results": [
    {
      "storeId": 1,
      "storeName": "Updated",
      "storeLocation": "Upd",
      "price": 12,
      "unitPrice": null,
      "dateRecorded": "2026-06-03",
      "isPromo": false
    }
  ],
  "cheapest": {
    "storeName": "Updated",
    "price": 12,
    "savings": null
  }
}
```

</details>

---

### 32. Search v2

- **Endpoint**: `POST http://localhost/v2/prices/search`
- **Status**: `200 OK`
- **Response Time**: 10ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "productName": "Updated"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "productName": "Updated",
  "results": [
    {
      "storeId": 1,
      "storeName": "Updated",
      "storeLocation": null,
      "storeChain": null,
      "price": 12,
      "unitPrice": null,
      "dateRecorded": "2026-06-03T00:00:00Z",
      "isPromo": false,
      "priceChange": null,
      "availability": null,
      "distance": null,
      "relevanceScore": null
    }
  ],
  "cheapest": {
    "storeName": "Updated",
    "price": 12,
    "savings": null
  },
  "pagination": null,
  "metadata": null
}
```

</details>

---

### 33. Search v1 - Null Product Name

- **Endpoint**: `POST http://localhost/v1/prices/search`
- **Status**: `400 Bad Request`
- **Response Time**: 9ms

<details>
<summary><b>Request Body</b></summary>

```json
{}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "error": "VALIDATION_ERROR",
  "message": "productName must not be null"
}
```

</details>

---

### 34. Search v1 - Product Not Found

- **Endpoint**: `POST http://localhost/v1/prices/search`
- **Status**: `404 Not Found`
- **Response Time**: 3ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "productName": "NonExistentProduct"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "error": "PRODUCT_NOT_FOUND",
  "message": "Product not found with name: NonExistentProduct"
}
```

</details>

---

### 35. Search v2 - Null Product Name

- **Endpoint**: `POST http://localhost/v2/prices/search`
- **Status**: `400 Bad Request`
- **Response Time**: 3ms

<details>
<summary><b>Request Body</b></summary>

```json
{}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "error": "VALIDATION_ERROR",
  "message": "productName must not be null"
}
```

</details>

---

### 36. Search v2 - Product Not Found

- **Endpoint**: `POST http://localhost/v2/prices/search`
- **Status**: `404 Not Found`
- **Response Time**: 3ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "productName": "NonExistentProduct"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "error": "PRODUCT_NOT_FOUND",
  "message": "Product not found with name: NonExistentProduct"
}
```

</details>

---

### 37. Get Non-Existent

- **Endpoint**: `GET http://localhost/v1/prices/99999`
- **Status**: `404 Not Found`
- **Response Time**: 3ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "error": "PRICE_NOT_FOUND",
  "message": "Price not found with id: 99999"
}
```

</details>

---

### 38. Update Non-Existent

- **Endpoint**: `PUT http://localhost/v1/prices/99999`
- **Status**: `404 Not Found`
- **Response Time**: 4ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "price": 12,
  "isPromo": false
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "error": "PRICE_NOT_FOUND",
  "message": "Price not found with id: 99999"
}
```

</details>

---

### 39. Delete Non-Existent

- **Endpoint**: `DELETE http://localhost/v1/prices/99999`
- **Status**: `404 Not Found`
- **Response Time**: 3ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "error": "PRICE_NOT_FOUND",
  "message": "Price not found with id: 99999"
}
```

</details>

---

### 40. Optimize

- **Endpoint**: `POST http://localhost/v1/shopping/optimize`
- **Status**: `200 OK`
- **Response Time**: 14ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "items": [
    "Updated"
  ]
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "totalItems": 0,
  "totalCost": 0,
  "storesToVisit": 0,
  "route": [],
  "savings": {
    "comparedToSingleStore": 0,
    "percentage": 0
  }
}
```

</details>

---

### 41. Create

- **Endpoint**: `POST http://localhost/v1/feedback-questions`
- **Status**: `201 Created`
- **Response Time**: 13ms

<details>
<summary><b>Request Body</b></summary>

```json
{
  "userName": "Test",
  "userEmail": "t@t.com",
  "message": "How do I compare?",
  "type": "question"
}
```

</details>

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": "e960681d-6ea8-4206-94fe-ec1b26d2bddf",
  "userName": "Test",
  "userEmail": "t@t.com",
  "type": "question",
  "message": "How do I compare?",
  "createdAt": null,
  "updatedAt": null
}
```

</details>

---

### 42. List

- **Endpoint**: `GET http://localhost/v1/feedback-questions`
- **Status**: `200 OK`
- **Response Time**: 6ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "data": [
    {
      "id": "e960681d-6ea8-4206-94fe-ec1b26d2bddf",
      "userName": "Test",
      "userEmail": "t@t.com",
      "type": "question",
      "message": "How do I compare?",
      "createdAt": "2026-06-03T16:53:44.195198+07:00",
      "updatedAt": "2026-06-03T16:53:44.195204+07:00"
    }
  ],
  "pagination": {
    "page": 1,
    "pageSize": 20,
    "totalItems": 1,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": true
  }
}
```

</details>

---

### 43. Get

- **Endpoint**: `GET http://localhost/v1/feedback-questions/e960681d-6ea8-4206-94fe-ec1b26d2bddf`
- **Status**: `200 OK`
- **Response Time**: 2ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "id": "e960681d-6ea8-4206-94fe-ec1b26d2bddf",
  "userName": "Test",
  "userEmail": "t@t.com",
  "type": "question",
  "message": "How do I compare?",
  "createdAt": null,
  "updatedAt": null
}
```

</details>

---

### 44. List

- **Endpoint**: `GET http://localhost/v1/activity-logs`
- **Status**: `200 OK`
- **Response Time**: 9ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "data": [
    {
      "id": "777a0e8d-272b-4989-9ac1-bc49a33861cf",
      "type": "FEEDBACK_QUESTION",
      "action": "CREATE",
      "description": "FEEDBACK_QUESTION created (id: e960681d-6ea8-4206-94fe-ec1b26d2bddf)",
      "createdAt": "2026-06-03T16:53:44.196281Z",
      "updatedAt": "2026-06-03T16:53:44.196284Z"
    },
    {
      "id": "f3b2e258-eacc-430f-997b-b521e09fd6fc",
      "type": "PRICE_RECORD",
      "action": "UPDATE",
      "description": "PRICE_RECORD updated (id: 2)",
      "createdAt": "2026-06-03T16:53:44.008098Z",
      "updatedAt": "2026-06-03T16:53:44.008103Z"
    },
    {
      "id": "170021c3-e671-4b38-9164-f0768f0e70d1",
      "type": "PRICE_RECORD",
      "action": "CREATE",
      "description": "PRICE_RECORD created (id: 2)",
      "createdAt": "2026-06-03T16:53:43.994623Z",
      "updatedAt": "2026-06-03T16:53:43.994628Z"
    },
    {
      "id": "005d5cc8-6c14-46e3-9fe1-4b29882b3772",
      "type": "PRICE_RECORD",
      "action": "UPDATE",
      "description": "PRICE_RECORD updated (id: 1)",
      "createdAt": "2026-06-03T16:53:43.980864Z",
      "updatedAt": "2026-06-03T16:53:43.98087Z"
    },
    {
      "id": "f5cd6e5a-80cb-4812-b4c6-9e075195a86d",
      "type": "PRICE_RECORD",
      "action": "CREATE",
      "description": "PRICE_RECORD created (id: 1)",
      "createdAt": "2026-06-03T16:53:43.933128Z",
      "updatedAt": "2026-06-03T16:53:43.933134Z"
    },
    {
      "id": "bb32d032-14fa-48aa-8917-0446888263dc",
      "type": "PRODUCT",
      "action": "UPDATE",
      "description": "PRODUCT updated (id: 2)",
      "createdAt": "2026-06-03T16:53:43.888243Z",
      "updatedAt": "2026-06-03T16:53:43.888248Z"
    },
    {
      "id": "bdf95a69-ad80-4282-8eda-860e8856e13d",
      "type": "PRODUCT",
      "action": "CREATE",
      "description": "PRODUCT created (id: 2)",
      "createdAt": "2026-06-03T16:53:43.875115Z",
      "updatedAt": "2026-06-03T16:53:43.87512Z"
    },
    {
      "id": "43cf54c7-3dde-4064-8788-552b8959ba58",
      "type": "PRODUCT",
      "action": "UPDATE",
      "description": "PRODUCT updated (id: 1)",
      "createdAt": "2026-06-03T16:53:43.862764Z",
      "updatedAt": "2026-06-03T16:53:43.86277Z"
    },
    {
      "id": "1fec8b71-268b-417c-baa5-351476e5f026",
      "type": "PRODUCT",
      "action": "CREATE",
      "description": "PRODUCT created (id: 1)",
      "createdAt": "2026-06-03T16:53:43.817453Z",
      "updatedAt": "2026-06-03T16:53:43.817458Z"
    },
    {
      "id": "a9294d87-2a4f-41ec-aac3-dc04c3797a78",
      "type": "STORE",
      "action": "UPDATE",
      "description": "STORE updated (id: 2)",
      "createdAt": "2026-06-03T16:53:43.79862Z",
      "updatedAt": "2026-06-03T16:53:43.798627Z"
    },
    {
      "id": "0c75a623-26f0-4a11-a47f-edf876d00f66",
      "type": "STORE",
      "action": "CREATE",
      "description": "STORE created (id: 2)",
      "createdAt": "2026-06-03T16:53:43.783604Z",
      "updatedAt": "2026-06-03T16:53:43.783611Z"
    },
    {
      "id": "1d791829-39bd-401f-a6c7-91c3fe9c0404",
      "type": "STORE",
      "action": "UPDATE",
      "description": "STORE updated (id: 1)",
      "createdAt": "2026-06-03T16:53:43.77025Z",
      "updatedAt": "2026-06-03T16:53:43.770256Z"
    },
    {
      "id": "8278d2ea-34c0-4bac-ac6c-831cb1b043c2",
      "type": "STORE",
      "action": "CREATE",
      "description": "STORE created (id: 1)",
      "createdAt": "2026-06-03T16:53:43.725176Z",
      "updatedAt": "2026-06-03T16:53:43.725182Z"
    },
    {
      "id": "2edfbdee-f4b6-46cb-a59a-e70aae46b130",
      "type": "UNIT",
      "action": "UPDATE",
      "description": "UNIT updated (id: KG)",
      "createdAt": "2026-06-03T16:53:43.699305Z",
      "updatedAt": "2026-06-03T16:53:43.699312Z"
    },
    {
      "id": "de6be44e-54fe-44c8-9431-0084fe0e252e",
      "type": "UNIT",
      "action": "CREATE",
      "description": "UNIT created (id: KG)",
      "createdAt": "2026-06-03T16:53:43.656415Z",
      "updatedAt": "2026-06-03T16:53:43.656421Z"
    },
    {
      "id": "d3cc039c-b245-4967-87f7-a95810e0d414",
      "type": "CATEGORY",
      "action": "UPDATE",
      "description": "CATEGORY updated (id: TEST_CAT)",
      "createdAt": "2026-06-03T16:53:43.637793Z",
      "updatedAt": "2026-06-03T16:53:43.6378Z"
    },
    {
      "id": "e6ac0e3c-c502-4610-a555-21398350f32f",
      "type": "CATEGORY",
      "action": "CREATE",
      "description": "CATEGORY created (id: TEST_CAT)",
      "createdAt": "2026-06-03T16:53:43.572164Z",
      "updatedAt": "2026-06-03T16:53:43.572172Z"
    }
  ],
  "pagination": {
    "page": 1,
    "pageSize": 20,
    "totalItems": 17,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": true
  }
}
```

</details>

---

### 45. Trigger Job

- **Endpoint**: `POST http://localhost/v1/admin/jobs/price-summary-update`
- **Status**: `200 OK`
- **Response Time**: 3ms

*No Request Body*

<details>
<summary><b>Response Body</b></summary>

```json
{
  "jobName": "price-summary-update",
  "message": "Unknown job: price-summary-update",
  "triggeredAt": "2026-06-03T09:53:44.250647Z"
}
```

</details>

---

