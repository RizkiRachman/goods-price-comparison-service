# Postman Smoke Tests for Goods Price Comparison Service

This directory contains the generated Postman collection file for running smoke tests against the Goods Price Comparison Service.

## Files
- `Goods Price Comparison Service.postman_collection.json`: The main Postman collection with all API requests organized by domain.

- `generate-collection.js`: A Node.js script used to programmatically generate the above JSON files.

## How to Import and Run the Tests

### 1. Start the Goods Price Comparison Service
Ensure your Spring Boot application is running, typically on `http://localhost:8080`.

### 2. Import into Postman

1.  Open Postman.
2.  Click on `File > Import` (or the `Import` button in the sidebar).
3.  Select `Goods Price Comparison Service.postman_collection.json` from this `postman/` directory.
4.  Postman will import the collection, including all necessary variables embedded within it.

### 3. Run the Collection

1.  In the Postman sidebar, find the `Goods Price Comparison Service - Smoke Tests` collection.
2.  Click on the `...` (more actions) icon next to the collection name.
3.  Select `Run collection`.
4.  In the Collection Runner window, ensure all requests are selected.
5.  Click `Run Goods Price Comparison Service - Smoke Tests`.

### 4. Review Results

The Collection Runner will execute all requests sequentially. Each request includes test scripts to assert HTTP status codes and validate response body structures. Captured IDs from `POST` requests will be automatically stored as collection variables and used by subsequent dependent requests.

## Special Notes

### Upload Receipt Image Request
The `Upload Receipt Image (Placeholder)` request in the `Receipts` folder requires a `multipart/form-data` body with an `image` file. Due to the complexity of programmatically generating file uploads in a simple script, you will need to manually add a file to the `image` key in the form-data tab within Postman before running this specific request.

### Get Activity Log by ID Request
The `Get Activity Log by ID (Placeholder)` request requires an actual activity log ID. You might need to manually replace `some-log-id` with a valid ID obtained from a previous `Get All Activity Logs` response or from your application logs.
