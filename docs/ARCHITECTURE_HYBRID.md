# Hybrid Architecture: Microservices → Hexagonal → Event-Driven

A three-layer hybrid architecture combining microservice boundaries, hexagonal structure, and event-driven
communication.

```
┌─────────────────────────────────────────────────────────────────────┐
│  LAYER 1: MICROSERVICES (Domain Boundaries)                         │
│  ┌──────────────┐ ┌──────────┐ ┌───────┐ ┌──────┐ ┌────────────┐  │
│  │  Receipt     │ │ Product  │ │ Store │ │ Price│ │ Shopping   │  │
│  │  Service     │ │ Service  │ │Service│ │Service│ │ Service    │  │
│  └──────┬───────┘ └────┬─────┘ └───┬───┘ └──┬───┘ └──────┬─────┘  │
│         │              │            │        │             │        │
│  ┌──────▼──────────────▼────────────▼────────▼─────────────▼──────┐ │
│  │  LAYER 3: EVENT BUS (Kafka / RabbitMQ / Spring Events)         │ │
│  │  receipt.processed │ product.created │ price.updated │ ...     │ │
│  └────────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────────┘

  Each Microservice Internally (LAYER 2):
  ┌──────────────────────────────────────────┐
  │  infrastructure/                         │
  │   ├── adapter/web/    (inbound)          │
  │   ├── adapter/persistence/ (outbound)    │
  │   ├── adapter/event/   (outbound events) │
  │   └── handler/event/   (inbound events)  │
  │  application/                            │
  │   ├── domain/ (pure Java business core)  │
  │   └── port/   (interfaces)              │
  └──────────────────────────────────────────┘
```

---

## Layer 1: Microservices (Domain Boundaries)

Each bounded context is a separate Maven module. In the current monorepo these are modules; in production they deploy
independently.

### Service Boundaries

| Service                  | Responsibility                                 | Owns                        |
|--------------------------|------------------------------------------------|-----------------------------|
| **receipt-service**      | Upload images, OCR processing, status tracking | `receipts`, `receipt_items` |
| **product-service**      | Product catalog, categorization, units         | `products`                  |
| **store-service**        | Store directory, locations                     | `stores`                    |
| **price-service**        | Price records, trends, comparisons             | `prices`                    |
| **shopping-service**     | Shopping optimization, route planning          | none (reads only)           |
| **notification-service** | Alerts, price drops, push notifications        | `alerts`                    |

### Database Per Service

Each service owns its own database schema / set of tables. No service ever queries another service's database directly —
all cross-service data access goes through events or API calls.

```
receipt-service:    receipts, receipt_items
product-service:    products
store-service:      stores
price-service:      prices
notification-service: alerts, watchlists
```

### Multi-Module Structure

```
goods-price-comparison-service/
├── pom.xml                          ← Parent POM (packaging: pom)
├── common/                          ← Shared kernel
│   ├── pom.xml
│   └── src/main/java/com/example/goodsprice/common/
│       ├── event/                   ← Event schemas (POJOs, no infra deps)
│       │   ├── ReceiptProcessedEvent.java
│       │   ├── ReceiptApprovedEvent.java
│       │   ├── ProductCreatedEvent.java
│       │   ├── PriceUpdatedEvent.java
│       │   └── StoreCreatedEvent.java
│       └── type/                    ← Shared value objects
│           ├── Money.java
│           ├── Quantity.java
│           └── ImageHash.java
│
├── receipt-service/
│   ├── pom.xml
│   └── src/main/java/com/example/goodsprice/receipt/
│       ├── ReceiptServiceApplication.java
│       └── ... (hexagonal structure)
│
├── product-service/
│   ├── pom.xml
│   └── src/main/java/com/example/goodsprice/product/
│
├── store-service/
│   └── ...
│
├── price-service/
│   └── ...
│
├── shopping-service/
│   └── ... (read-only, aggregates from events)
│
└── notification-service/
    └── ...
```

### Service Communication Rules

1. **No direct service-to-service calls** — use events or API gateway
2. **Eventual consistency** — services never wait synchronously for another service
3. **Each service owns its data** — no shared databases
4. **Compensating actions** — on failure, publish failure events for saga patterns

---

## Layer 2: Hexagonal (Inside Each Microservice)

Each microservice internally follows ports & adapters. Structure per service:

```
receipt-service/
├── pom.xml
└── src/main/java/com/example/goodsprice/receipt/
    ├── ReceiptServiceApplication.java
    ├── config/                              ← Module-specific config
    │   ├── AsyncConfig.java
    │   └── ReceiptProperties.java
    │
    ├── application/                         ← APPLICATION LAYER (pure Java)
    │   ├── domain/                          ← Domain core
    │   │   ├── model/
    │   │   │   ├── Receipt.java
    │   │   │   ├── ReceiptItem.java
    │   │   │   ├── ReceiptStatus.java
    │   │   │   └── ImageHash.java
    │   │   └── service/
    │   │       └── ReceiptService.java      ← Implements ReceiptInPort
    │   │
    │   ├── port/
    │   │   ├── in/
    │   │   │   └── ReceiptInPort.java       ← Driving port (for web adapters)
    │   │   └── out/
    │   │       ├── ReceiptRepositoryPort.java  ← Driven port (persistence)
    │   │       └── ReceiptEventOutPort.java    ← Driven port (event publishing)
    │   │
    │   └── exception/
    │       └── ReceiptNotFoundException.java
    │
    └── infrastructure/                     ← INFRASTRUCTURE LAYER
        ├── adapter/
        │   ├── web/                        ← Inbound web
        │   │   ├── ReceiptController.java
        │   │   └── ReceiptWebAdapter.java
        │   │
        │   ├── persistence/                ← Outbound persistence
        │   │   ├── entity/
        │   │   │   └── ReceiptEntity.java
        │   │   ├── ReceiptRepositoryAdapter.java
        │   │   └── JpaReceiptRepository.java
        │   │
        │   └── event/                      ← Outbound event publishing
        │       └── ReceiptEventAdapter.java ← Implements ReceiptEventOutPort
        │
        └── handler/
            └── event/                      ← Inbound event consumption
                └── ProductEventHandler.java ← Listens to product.created
```

### Port Naming Convention

| Layer                     | Convention          | Example                 |
|---------------------------|---------------------|-------------------------|
| Driving port (web)        | `XxxInPort`         | `ReceiptInPort`         |
| Driven port (persistence) | `XxxRepositoryPort` | `ReceiptRepositoryPort` |
| Driven port (events out)  | `XxxEventOutPort`   | `ReceiptEventOutPort`   |
| Event handler             | `XxxEventHandler`   | `ProductEventHandler`   |

### Inside each port/adapter

```
Driving Port (in):
  ReceiptInPort ← ReceiptService (impl) ← ReceiptWebAdapter (consumer)

Driven Ports (out):
  ReceiptRepositoryPort ← ReceiptRepositoryAdapter (impl) ← ReceiptService (consumer)
  ReceiptEventOutPort   ← ReceiptEventAdapter (impl)       ← ReceiptService (consumer)
```

---

## Layer 3: Event-Driven Communication

### Event Schema (in `common` module)

All events in `common/event/` — pure POJOs with timestamp:

```java
// common/src/main/java/.../common/event/ReceiptProcessedEvent.java
// All events are records — immutable, zero boilerplate, no Lombok needed
public record ReceiptProcessedEvent(
    UUID receiptId,
    UUID storeId,
    Instant processedAt,
    String status,
    String errorMessage
) {}
```

### Event Flow Patterns

#### 1. Service publishes event (outbound)

```
ReceiptService (domain)
  │  after business logic completes
  ▼
ReceiptEventOutPort.publish(event)    ← Driven port interface
  │
  ▼
ReceiptEventAdapter                    ← Infrastructure adapter
  │  in monolith mode: Spring ApplicationEventPublisher.publishEvent()
  │  in distributed mode: KafkaTemplate.send("receipt.processed", event)
  ▼
Event Bus
```

```java
// In ReceiptService (domain service):
public void processReceipt(UUID id) {
    Receipt receipt = receiptRepositoryPort.read(id)
            .orElseThrow(() -> new ReceiptNotFoundException(id));
    receipt.process();
    receiptRepositoryPort.upsert(id, receipt);
    receiptEventOutPort.publish(new ReceiptProcessedEvent(id, receipt.getStoreId(), Instant.now(), COMPLETED, null));
}
```

#### 2. Service consumes event (inbound)

```
Event Bus
  │
  ▼
ReceiptProcessedEventHandler           ← Infrastructure handler
  │  in monolith: @EventListener
  │  in distributed: @KafkaListener
  ▼
PriceService.syncFromReceipt(event)    ← Domain service
```

```java
// In infrastructure/handler/event/:
@Component
@RequiredArgsConstructor
public class ReceiptProcessedEventHandler {

    private final PriceInPort priceInPort;  // driving port of price-service

    @EventListener  // or @KafkaListener(topics = "receipt.processed")
    public void handle(ReceiptProcessedEvent event) {
        priceInPort.syncFromReceipt(event.getReceiptId());
    }
}
```

### Event Types

| Event                   | Publisher       | Consumers                           | Trigger                |
|-------------------------|-----------------|-------------------------------------|------------------------|
| `ReceiptProcessedEvent` | receipt-service | price-service, notification-service | LLM finishes OCR       |
| `ReceiptApprovedEvent`  | receipt-service | price-service                       | User approves receipt  |
| `ProductCreatedEvent`   | product-service | price-service, shopping-service     | New product discovered |
| `PriceUpdatedEvent`     | price-service   | notification-service                | Price changed          |
| `StoreCreatedEvent`     | store-service   | price-service, shopping-service     | New store added        |

### Monolith → Distributed Transition

The event abstraction makes migration seamless:

```java
// Monolith phase — Spring ApplicationEvent (in-process, same JVM)
@Component
public class ReceiptEventAdapter implements ReceiptEventOutPort {
    private final ApplicationEventPublisher publisher;

    public void publish(ReceiptProcessedEvent event) {
        publisher.publishEvent(event);
    }
}

// Distributed phase — Kafka (message broker)
@Component
@ConditionalOnProperty(name = "events.mode", havingValue = "kafka")
public class ReceiptEventKafkaAdapter implements ReceiptEventOutPort {
    private final KafkaTemplate<String, Object> kafka;

    public void publish(ReceiptProcessedEvent event) {
        kafka.send("receipt.processed", event.getReceiptId().toString(), event);
    }
}
```

### Saga / Compensating Actions

For multi-step workflows, use choreographed sagas via events:

```
receipt-service: ReceiptProcessedEvent ──► price-service: sync prices
                                               │ on failure
                                               ▼
                                           PriceSyncFailedEvent
                                               │
                                               ▼
receipt-service: marks receipt as FAILED
```

---

## Complete Package Structure

```
goods-price-comparison-service/
├── pom.xml (packaging: pom)
│
├── common/
│   └── src/main/java/com/example/goodsprice/common/
│       ├── event/
│       │   ├── ReceiptProcessedEvent.java
│       │   ├── ReceiptApprovedEvent.java
│       │   ├── PriceSyncFailedEvent.java
│       │   ├── ProductCreatedEvent.java
│       │   ├── PriceUpdatedEvent.java
│       │   └── StoreCreatedEvent.java
│       └── type/
│           ├── Money.java
│           └── ImageHash.java
│
├── receipt-service/
│   └── src/main/java/com/example/goodsprice/receipt/
│       ├── ReceiptServiceApplication.java
│       ├── config/
│       │   ├── AsyncConfig.java
│       │   └── ReceiptProperties.java
│       ├── application/
│       │   ├── domain/
│       │   │   ├── model/
│       │   │   │   ├── Receipt.java
│       │   │   │   ├── ReceiptItem.java
│       │   │   │   └── ReceiptStatus.java
│       │   │   └── service/
│       │   │       └── ReceiptService.java
│       │   ├── port/
│       │   │   ├── in/
│       │   │   │   └── ReceiptInPort.java
│       │   │   └── out/
│       │   │       ├── ReceiptRepositoryPort.java
│       │   │       ├── ReceiptEventOutPort.java
│       │   │       └── LlmProviderPort.java
│       │   └── exception/
│       │       ├── ReceiptNotFoundException.java
│       │       └── DuplicateReceiptException.java
│       └── infrastructure/
│           ├── adapter/
│           │   ├── web/
│           │   │   ├── ReceiptController.java
│           │   │   └── ReceiptWebAdapter.java
│           │   ├── persistence/
│           │   │   ├── entity/ReceiptEntity.java
│           │   │   ├── entity/ReceiptItemEntity.java
│           │   │   ├── ReceiptRepositoryAdapter.java
│           │   │   └── JpaReceiptRepository.java
│           │   ├── llm/
│           │   │   ├── GeminiLlmAdapter.java
│           │   │   └── LocalLlmAdapter.java
│           │   └── event/
│           │       └── ReceiptEventAdapter.java
│           └── handler/event/
│               └── ProductEventHandler.java
│
├── product-service/
│   └── src/main/java/com/example/goodsprice/product/
│       ├── ProductServiceApplication.java
│       ├── config/
│       ├── application/
│       │   ├── domain/
│       │   │   ├── model/Product.java
│       │   │   └── service/ProductService.java
│       │   ├── port/
│       │   │   ├── in/ProductInPort.java
│       │   │   └── out/
│       │   │       ├── ProductRepositoryPort.java
│       │   │       └── ProductEventOutPort.java
│       │   └── exception/ProductNotFoundException.java
│       └── infrastructure/
│           ├── adapter/
│           │   ├── web/ProductController.java, ProductWebAdapter.java
│           │   ├── persistence/entity/ProductEntity.java, ProductRepositoryAdapter.java, JpaProductRepository.java
│           │   └── event/ProductEventAdapter.java
│           └── handler/event/
│               └── ReceiptEventHandler.java
│
├── store-service/
│   └── (same structure as above)
│
├── price-service/
│   └── (same structure)
│
├── shopping-service/
│   └── (read-only, no persistence adapter)
│
└── notification-service/
    └── (alerts, watchlists, push)
```

---

## Data Flow: End-to-End Receipt Processing

```
User uploads receipt image
         │
         ▼
┌───────────────────────────────────────────────────┐
│  RECEIPT-SERVICE                                   │
│                                                    │
│  POST /receipts (MultipartFile)                    │
│    → ReceiptController                             │
│    → ReceiptWebAdapter (MultipartFile → Receipt)   │
│    → ReceiptInPort.create(receipt)                 │
│    → ReceiptService.create()                       │
│      → ReceiptRepositoryPort.create(receipt)       │
│      → ReceiptRepositoryAdapter (Receipt → ReceiptEntity) │
│      → JpaReceiptRepository.save()                 │
│      → ReceiptEventOutPort.publish(                │
│          ReceiptUploadedEvent)                     │
│                                                    │
│  [Async] ReceiptProcessEventListener               │
│    → ReceiptInPort.process(id)                     │
│    → ReceiptService.process()                      │
│      → LlmProviderPort.extractReceiptData()        │
│      → GeminiLlmAdapter (OCR)                      │
│      → ReceiptRepositoryPort.upsert(receipt)       │
│      → ReceiptEventOutPort.publish(                │
│          ReceiptProcessedEvent)                    │
└─────────────────────┬─────────────────────────────┘
                      │
                      │ EVENT: ReceiptProcessedEvent
                      ▼
┌───────────────────────────────────────────────────┐
│  PRICE-SERVICE                                     │
│                                                    │
│  ReceiptProcessedEventHandler                      │
│    → PriceInPort.syncFromReceipt(event)            │
│    → PriceComparisonService.syncFromReceipt()      │
│      → Parses receipt items                        │
│      → Creates/updates Products (via event)        │
│      → Creates Price records                       │
│      → ProductEventOutPort.publish(                │
│          ProductCreatedEvent)                      │
│      → PriceEventOutPort.publish(                  │
│          PriceUpdatedEvent)                        │
└─────────────────────┬─────────────────────────────┘
                      │
                      │ EVENTS: ProductCreatedEvent, PriceUpdatedEvent
                      ▼
┌───────────────────────────────────────────────────┐
│  SHOPPING-SERVICE (reads event store / view)       │
│  NOTIFICATION-SERVICE (checks watchlists)          │
└───────────────────────────────────────────────────┘
```

---

## Dependency Rules

### Within a Microservice (Hexagonal)

```
infrastructure/adapter/web/          → application/port/in/
infrastructure/adapter/persistence/  → application/port/out/
infrastructure/adapter/event/        → application/port/out/
infrastructure/handler/event/        → application/port/in/ (of this service)
application/domain/service/          → application/port/out/
application/                         → nothing (pure Java)
config/                              → application/, infrastructure/
```

### Between Microservices (Event-Driven)

```
receipt-service  ──event──→ price-service
receipt-service  ──event──→ notification-service
product-service  ──event──→ price-service
price-service    ──event──→ notification-service
store-service    ──event──→ price-service
store-service    ──event──→ shopping-service
```

**No service imports another service's internal classes.** The only shared dependency is `common` (event schemas, base
types).

---

## Testing Strategy

### In-Process (Monolith Phase)

- Unit: `application/domain/service/*Test` — mock all ports
- Integration: `infrastructure/adapter/persistence/*Test` — `@DataJpaTest`
- Controller: `infrastructure/adapter/web/*Test` — `@WebMvcTest`
- Event: `infrastructure/handler/event/*Test` — verify event consumption
- Full saga: publish event → verify downstream service reacts

### Distributed Phase

- Same unit/integration tests per service
- Contract tests for event schemas
- Integration test with embedded Kafka (`Testcontainers`)
- End-to-end tests across service boundaries

### Event Testing

```java
// Test that domain service publishes correct event
@Test
void shouldPublishEventWhenReceiptProcessed() {
    var eventCaptor = new ArgumentCaptor<ReceiptProcessedEvent>();
    given(receiptRepositoryPort.read(any())).willReturn(Optional.of(receipt));

    receiptService.processReceipt(receiptId);

    then(receiptEventOutPort).should().publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue().getReceiptId()).isEqualTo(receiptId);
    assertThat(eventCaptor.getValue().getStatus()).isEqualTo("COMPLETED");
}
```

---

## Transition: Monolith → Distributed

### Phase 1: Monolith with Module Separation

- Multi-module Maven structure
- Each module has own hexagonal structure
- Events via Spring `ApplicationEvent` (in-process)
- Single database with schema-per-service

### Phase 2: Event Gateway Abstraction

- Add `EventOutPort` adapters with `@ConditionalOnProperty`
- Default: `ApplicationEventPublisher`
- Option: configure for Kafka
- All cross-service communication goes through events only

### Phase 3: Extract Services

- Extract module → standalone Spring Boot app
- Replace in-process event adapter with Kafka adapter
- Extract database (schema → separate DB instance)
- API Gateway for external requests
- Service discovery (Kubernetes / Eureka)

### Phase 4: Distributed Production

- Each service in its own container
- Kafka/RabbitMQ for events
- Separate databases per service
- Monitoring, tracing, saga orchestration
