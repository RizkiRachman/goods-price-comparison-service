# Smoke Testing Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a comprehensive smoke test suite covering all 45 API endpoints of the Goods Price Comparison Service using a Postman collection and environment.

**Architecture:** A programmatic generator script written in Node.js will build the Postman collection JSON. This ensures clean, maintainable, and error-free JSON generation without manual escaping. The collection will be organized into folders by domain, with sequential requests that capture and reuse IDs (e.g., `storeId`, `productId`, `categoryId`) via Postman environment variables.

**Tech Stack:** Node.js (for generation), Postman Collection v2.1.0 format.

---

## File Structure

- Create: `postman/generate-collection.js` — Programmatic generator script for the Postman collection and environment.
- Create: `postman/goods-price-comparison-service.postman_collection.json` — Generated Postman collection.
- Create: `postman/goods-price-comparison-service.postman_environment.json` — Generated Postman environment.
- Create: `postman/README.md` — Documentation on how to run the smoke tests.

---

## Tasks

### Task 1: Create the Postman Collection Generator Script

**Files:**
- Create: `postman/generate-collection.js`

- [ ] **Step 1: Write the generator script**
  Create a Node.js script that programmatically builds the Postman collection JSON and environment JSON. The script will define all 45 endpoints with appropriate methods, paths, headers, request bodies, pre-request scripts, and test assertions.

```javascript
const fs = require('fs');
const path = require('path');

// Define the collection structure
const collection = {
  info: {
    name: "Goods Price Comparison Service Smoke Tests",
    schema: "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  item: []
};

// Helper to create a request item
function createRequest(name, method, pathSegments, options = {}) {
  const testScripts = options.tests || [];
  const preRequestScripts = options.preRequest || [];
  
  return {
    name: name,
    event: [
      {
        listen: "test",
        script: {
          exec: testScripts,
          type: "text/javascript"
        }
      },
      {
        listen: "prerequest",
        script: {
          exec: preRequestScripts,
          type: "text/javascript"
        }
      }
    ],
    request: {
      method: method,
      header: options.headers || [
        {
          key: "Content-Type",
          value: "application/json"
        }
      ],
      body: options.body ? {
        mode: "raw",
        raw: JSON.stringify(options.body, null, 2)
      } : undefined,
      url: {
        raw: "{{baseUrl}}/" + pathSegments.join('/'),
        host: ["{{baseUrl}}"],
        path: pathSegments
      }
    }
  };
}

// Add folders and requests programmatically...
// (Full implementation will be written in the file)
```

- [ ] **Step 2: Run the generator script to verify it runs without errors**
  Run: `node postman/generate-collection.js`
  Expected: Successful generation of `postman/goods-price-comparison-service.postman_collection.json` and `postman/goods-price-comparison-service.postman_environment.json`.

- [ ] **Step 3: Commit**
  ```bash
  git add postman/generate-collection.js
  git commit -m "test: add programmatic postman collection generator"
  ```

---

### Task 2: Generate and Verify the Postman Collection

**Files:**
- Modify: `postman/goods-price-comparison-service.postman_collection.json` (generated)
- Modify: `postman/goods-price-comparison-service.postman_environment.json` (generated)

- [ ] **Step 1: Run the generator script**
  Run: `node postman/generate-collection.js`
  Expected: Files generated successfully.

- [ ] **Step 2: Verify the generated JSON files are valid JSON**
  Run: `node -e "JSON.parse(fs.readFileSync('postman/goods-price-comparison-service.postman_collection.json'))"`
  Expected: No syntax errors.

- [ ] **Step 3: Commit**
  ```bash
  git add postman/goods-price-comparison-service.postman_collection.json postman/goods-price-comparison-service.postman_environment.json
  git commit -m "test: generate postman collection and environment"
  ```

---

### Task 3: Add Documentation and Update STATE.md

**Files:**
- Create: `postman/README.md`
- Modify: `STATE.md`

- [ ] **Step 1: Create README.md with instructions on how to run the smoke tests**
  Write clear instructions on how to import the collection and environment into Postman, and how to run them using Newman.

- [ ] **Step 2: Update STATE.md to mark the Smoke Testing task as complete**
  Move the Smoke Testing task to the completed section in `STATE.md`.

- [ ] **Step 3: Commit**
  ```bash
  git add postman/README.md STATE.md
  git commit -m "docs: add postman smoke testing documentation and update state"
  ```
