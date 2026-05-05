# User Guide

A simple guide for using the Goods Price Comparison Service.

## What Is This?

This service helps you track and compare prices from your shopping receipts. Simply take a photo of your receipt, upload it, and the system will:

1. **Extract items and prices** using AI
2. **Store the data** for future reference
3. **Show price comparisons** across different stores
4. **Alert you** when prices drop

## Quick Overview

```
📸 Take Photo → 🤖 AI Reads It → 💾 Save Prices → 🔍 Compare & Alert
```

## How to Use

### Step 1: Upload a Receipt

Send your receipt image to the service:

```bash
curl -X POST http://localhost:8080/receipts \
  -F "image=@your_receipt.jpg"
```

Or use a tool like [Postman](https://www.postman.com/) or any HTTP client.

**What happens:**
- The system checks if you've already uploaded this receipt (duplicate detection)
- Returns a job ID immediately
- Processing happens in the background

### Step 2: Check Processing Status

Receipt processing takes a few seconds. Check the status:

```bash
curl http://localhost:8080/receipts/{job-id}/status
```

**Possible statuses:**
- `PENDING` - Waiting to be processed
- `PROCESSING` - AI is reading the receipt
- `COMPLETED` - Done! Prices extracted successfully
- `FAILED` - Something went wrong (you can retry)

### Step 3: View Extracted Data

Once complete, view the receipt details:

```bash
curl http://localhost:8080/receipts/{job-id}
```

You'll see:
- Store name and location
- Purchase date
- List of items with prices
- Total amount

### Step 4: Search and Compare Prices

Find the best prices for products:

```bash
# Search by product name
curl "http://localhost:8080/prices?productName=milk"

# Compare prices across stores
curl "http://localhost:8080/prices/compare?productId=123"

# View price history
curl "http://localhost:8080/prices/history?productId=123"
```

## Understanding Your Data

### Products
Products are items you buy (e.g., "Whole Milk", "Organic Eggs"). The system automatically creates products when it sees new items on receipts.

### Stores
Stores are where you shop (e.g., "Walmart", "Target"). Each store has a name and location.

### Prices
Prices are linked to both a product and a store, with the date they were recorded. This lets you track price changes over time.

## Example Workflow

```bash
# 1. Upload a receipt
$ curl -X POST http://localhost:8080/receipts -F "image=@grocery_receipt.jpg"
{"id": "550e8400-e29b-41d4-a716-446655440000"}

# 2. Check status
$ curl http://localhost:8080/receipts/550e8400-e29b-41d4-a716-446655440000/status
{"status": "COMPLETED"}

# 3. View extracted items
$ curl http://localhost:8080/receipts/550e8400-e29b-41d4-a716-446655440000
{
  "storeName": "SuperMart",
  "totalAmount": 45.67,
  "items": [
    {"productName": "Milk 1L", "price": 3.49},
    {"productName": "Bread", "price": 2.99}
  ]
}

# 4. Find cheapest milk
$ curl "http://localhost:8080/prices?productName=milk&sort=price,asc"
```

## Tips

- **Upload clear photos** - Better image quality means better AI recognition
- **Wait for processing** - Don't worry if status shows PENDING for a few seconds
- **Duplicates are blocked** - Same receipt won't be processed twice
- **Compare before shopping** - Check prices across stores to save money

## API Documentation

Full API documentation is available at:
- **Swagger UI**: http://localhost:8080/swagger-ui.html (when running locally)

## Getting Help

- **For developers**: See [Developer Guide](DEVELOPER_GUIDE.md)
- **For architecture details**: See [Architecture Overview](ARCHITECTURE_HYBRID.md)
- **For database info**: See [Database ERD](ERD.md)
