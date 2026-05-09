<a id="readme-top"></a>

[![REST API][rest-shield]][rest-url]

<br />

<div align="center">
  <h3 align="center">Goods Price Comparison Service — User Guide</h3>
  <p align="center">
    Track and compare prices from your shopping receipts.
    <br />
    <a href="DEVELOPER_GUIDE.md"><strong>Developer Guide »</strong></a>
  </p>
</div>

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#what-is-this">What Is This?</a></li>
    <li><a href="#quick-overview">Quick Overview</a></li>
    <li><a href="#how-to-use">How to Use</a></li>
    <li><a href="#understanding-your-data">Understanding Your Data</a></li>
    <li><a href="#example-workflow">Example Workflow</a></li>
    <li><a href="#tips">Tips</a></li>
  </ol>
</details>

## What Is This?

Upload a photo of your receipt and the system will:

1. **Extract items and prices** using AI
2. **Store the data** for future reference
3. **Show price comparisons** across different stores
4. **Alert you** when prices drop

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Quick Overview

```
Take Photo → AI Reads It → Save Prices → Compare & Alert
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## How to Use

### Step 1: Upload a Receipt

```bash
curl -X POST http://localhost:8080/receipts \
  -F "image=@your_receipt.jpg"
```

Or use [Postman](https://www.postman.com/) or any HTTP client.

**What happens:**
- System checks for duplicates (same receipt won't process twice)
- Returns a job ID immediately
- Processing happens in the background

### Step 2: Check Status

```bash
curl http://localhost:8080/receipts/{job-id}/status
```

| Status | Meaning |
|--------|---------|
| `PENDING` | Waiting to be processed |
| `PROCESSING` | AI is reading the receipt |
| `COMPLETED` | Done! Prices extracted |
| `FAILED` | Something went wrong (retry) |

### Step 3: View Results

```bash
curl http://localhost:8080/receipts/{job-id}
```

You'll see store name, location, purchase date, items with prices, and total amount.

### Step 4: Search & Compare

```bash
curl "http://localhost:8080/prices?productName=milk"
curl "http://localhost:8080/prices/compare?productId=123"
curl "http://localhost:8080/prices/history?productId=123"
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Understanding Your Data

**Products** — Items you buy (e.g., "Whole Milk"). Created automatically when new items appear on receipts.

**Stores** — Where you shop (e.g., "Walmart"). Each has a name and location.

**Prices** — Linked to both a product and a store with the date recorded. Enables tracking price changes over time.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Example Workflow

```bash
# Upload receipt
$ curl -X POST http://localhost:8080/receipts -F "image=@receipt.jpg"
{"id": "550e8400-e29b-41d4-a716-446655440000"}

# Check status
$ curl http://localhost:8080/receipts/550e8400-e29b-41d4-a716-446655440000/status
{"status": "COMPLETED"}

# View extracted items
$ curl http://localhost:8080/receipts/550e8400-e29b-41d4-a716-446655440000
{
  "storeName": "SuperMart",
  "totalAmount": 45.67,
  "items": [
    {"productName": "Milk 1L", "price": 3.49},
    {"productName": "Bread", "price": 2.99}
  ]
}

# Find cheapest milk
$ curl "http://localhost:8080/prices?productName=milk&sort=price,asc"
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Tips

- **Clear photos** — Better image quality means better AI recognition
- **Wait for processing** — PENDING for a few seconds is normal
- **No duplicates** — Same receipt won't process twice
- **Compare before shopping** — Check prices across stores to save money

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

[rest-shield]: https://img.shields.io/badge/REST_API-005571?style=for-the-badge&logo=swagger&logoColor=white
[rest-url]: http://localhost:8080/swagger-ui.html
