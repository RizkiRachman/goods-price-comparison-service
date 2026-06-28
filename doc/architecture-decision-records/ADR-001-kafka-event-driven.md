# ADR-001: Event-Driven Architecture with Kafka

**Status:** Proposed (architecture designed, Kafka adapter written, not deployed)

## Context

The price comparison service needs to handle:
- Receipt OCR processing (async, can take 5-30s per image)
- Price refresh across multiple stores
- Provider status changes (LLM providers come and go)
- Audit trail of all price comparisons

Synchronous REST calls for OCR processing block HTTP threads for too long. A poll-and-wait approach wastes resources.

## Decision

Adopt event-driven architecture with Apache Kafka as the event backbone.

| Event | Producer | Consumer | Topic |
|---|---|---|---|
| ReceiptUploaded | API Gateway | OCR Service | receipt.uploaded |
| OCRCompleted | OCR Service | Comparison Service | receipt.ocr.completed |
| PriceRefreshRequested | Scheduler | Store Adapters | price.refresh.requested |
| PriceUpdated | Store Adapters | Notification Service | price.updated |
| ProviderStatusChanged | Health Monitor | Router Service | provider.status |

## Consequences

**Positive:**
- OCR processing decoupled from HTTP thread pool
- Backpressure via consumer lag
- Message replay for failure recovery
- Audit trail (each event is immutable in the log)
- Multiple consumers can subscribe independently

**Negative:**
- Operational complexity of running Kafka
- Event schema evolution must be managed
- At-least-once delivery means idempotent consumers
- Eventual consistency gap between write and read models

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| RabbitMQ | Good for work queues, weaker log/audit/replay story |
| AWS SQS | Vendor lock-in, no ordering guarantees across shards |
| Database poller | Poor scalability, tight coupling, no backpressure |
| gRPC streaming | Requires persistent connections, complex for async OCR |
