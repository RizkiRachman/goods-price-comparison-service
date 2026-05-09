<a id="readme-top"></a>

[![Hexagonal][hexagonal-shield]][hexagonal-url]
[![Event-Driven][event-shield]][event-url]
[![Spring Boot][spring-shield]][spring-url]

<br />

<div align="center">
  <h3 align="center">Goods Price Comparison Service — Architecture</h3>
  <p align="center">
    Three-layer hybrid architecture: microservice boundaries, hexagonal structure, event-driven communication.
    <br />
    <a href="DEVELOPER_GUIDE.md"><strong>Read the Developer Guide »</strong></a>
  </p>
</div>

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#layer-1-microservices">Layer 1: Microservices</a></li>
    <li><a href="#layer-2-hexagonal">Layer 2: Hexagonal</a></li>
    <li><a href="#layer-3-event-driven">Layer 3: Event-Driven</a></li>
    <li><a href="#data-flow">Data Flow</a></li>
    <li><a href="#dependency-rules">Dependency Rules</a></li>
    <li><a href="#testing-strategy">Testing Strategy</a></li>
    <li><a href="#monolith-to-distributed">Monolith to Distributed</a></li>
  </ol>
</details>

## Layer 1: Microservices

Each bounded context is a separate service. In the current monorepo these are packages; in production they deploy independently.

### Service Boundaries

| Service | Responsibility | Owns |
|---------|---------------|------|
| **receipt** | Upload images, OCR processing, status tracking | `receipts`, `receipt_items` |
| **product** | Product catalog, categorization, units | `products` |
| **store** | Store directory, locations | `stores` |
| **price** | Price records, trends, comparisons | `prices` |
| **shopping** | Shopping optimization, route planning | none (reads only) |
| **notification** | Alerts, price drops, push notifications | `alerts` |

### Communication Rules

1. **No direct service-to-service calls** — communication through events only
2. **Eventual consistency** — services never wait synchronously for another service
3. **Each service owns its data** — no shared databases
4. **Compensating actions** — on failure, publish failure events for saga patterns

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Layer 2: Hexagonal

Each service follows ports and adapters pattern internally:

```
service/
├── application/                    # Pure Java, no framework dependencies
│   ├── domain/model/               # Business entities (@Builder @Getter @Setter)
│   ├── domain/service/             # Business logic (implements *InPort)
│   ├── port/in/                    # Driving ports (*InPort)
│   ├── port/out/                   # Driven ports (*RepositoryPort, *EventOutPort)
│   └── exception/                  # Domain exceptions
└── infrastructure/                 # Framework code
    ├── adapter/web/                # REST controllers, DTO mappers
    ├── adapter/persistence/        # JPA entities, repository adapters
    ├── adapter/event/              # Event publishers
    └── handler/event/              # Event consumers (@Async @TransactionalEventListener)
```

### Port Naming

| Layer | Convention | Example |
|-------|------------|---------|
| Driving port (web) | `XxxInPort` | `ReceiptInPort` |
| Driven port (persistence) | `XxxRepositoryPort` | `ReceiptRepositoryPort` |
| Driven port (events out) | `XxxEventOutPort` | `ReceiptEventOutPort` |
| Event handler | `XxxEventHandler` | `ProductEventHandler` |

### Inside Each Port/Adapter

```
Driving: XxxInPort ← DomainService (impl) ← WebAdapter (consumer)
Driven:  XxxRepositoryPort ← RepositoryAdapter (impl) ← DomainService (consumer)
Driven:  XxxEventOutPort   ← EventAdapter (impl)       ← DomainService (consumer)
```

### Complete Package Structure

```
src/main/java/com/example/goodsprice/
├── common/
│   ├── constant/           # AppConstants, ErrorCodes
│   ├── event/              # Event schemas (POJOs, no infra deps)
│   ├── exception/          # Shared exceptions
│   └── util/               # ObjectUtils, NumberUtils, JsonUtils
│
├── receipt/                # (same hexagonal structure for each service)
├── product/
├── price/
├── store/
├── llm/
├── shopping/
└── alert/
```

Each service follows the same hexagonal structure — see `receipt` as the reference implementation.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Layer 3: Event-Driven

### Event Schema

All events live in `common/event/` as immutable records:

```java
public record ReceiptProcessedEvent(
    UUID receiptId,
    UUID storeId,
    Instant processedAt,
    String status,
    String errorMessage
) {}
```

### Event Flow

**Publishing (outbound):**

```
DomainService → EventOutPort (interface) → EventAdapter → Event Bus
```

```java
public void processReceipt(UUID id) {
    Receipt receipt = receiptRepositoryPort.read(id);
    receipt.process();
    receiptRepositoryPort.upsert(id, receipt);
    receiptEventOutPort.publish(
        new ReceiptProcessedEvent(id, receipt.getStoreId(), Instant.now(), "COMPLETED", null)
    );
}
```

**Consuming (inbound):**

```
Event Bus → EventHandler → DomainService (of another service)
```

```java
@Component
@RequiredArgsConstructor
public class ReceiptProcessedEventHandler {

    private final PriceInPort priceInPort;

    @EventListener
    public void handle(ReceiptProcessedEvent event) {
        priceInPort.syncFromReceipt(event.getReceiptId());
    }
}
```

### Event Catalog

| Event | Publisher | Consumers | Trigger |
|-------|-----------|-----------|---------|
| `ReceiptProcessedEvent` | receipt | price, notification | OCR processing completes |
| `ReceiptApprovedEvent` | receipt | price | User approves receipt |
| `ProductCreatedEvent` | product | price, shopping | New product discovered |
| `PriceUpdatedEvent` | price | notification | Price changed |
| `StoreCreatedEvent` | store | price, shopping | New store added |

### Monolith to Distributed Transition

The event abstraction makes migration seamless — the adapter is the only thing that changes:

```java
// Monolith — Spring ApplicationEvent (in-process)
@Component
public class ReceiptEventAdapter implements ReceiptEventOutPort {
    private final ApplicationEventPublisher publisher;

    public void publish(ReceiptProcessedEvent event) {
        publisher.publishEvent(event);
    }
}

// Distributed — Kafka
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

For multi-step workflows, use choreographed sagas:

```
receipt-service: ReceiptProcessedEvent ──► price-service: sync prices
                                               │ on failure
                                               ▼
                                           PriceSyncFailedEvent
                                               │
                                               ▼
receipt-service: marks receipt as FAILED
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Data Flow

### End-to-End Receipt Processing

```
User uploads receipt image
         │
         ▼
RECEIPT-SERVICE
  POST /receipts (MultipartFile)
    → ReceiptController
    → ReceiptWebAdapter (MultipartFile → Receipt)
    → ReceiptInPort.create(receipt)
    → ReceiptService.create()
      → ReceiptRepositoryPort.create(receipt)
      → ReceiptRepositoryAdapter (Receipt → ReceiptEntity)
      → JpaReceiptRepository.save()
      → ReceiptEventOutPort.publish(ReceiptUploadedEvent)

  [Async] ReceiptProcessEventListener
    → ReceiptInPort.process(id)
    → ReceiptService.process()
      → ProviderPort.extractReceiptData()
      → ProviderAdapter (OCR)
      → ReceiptRepositoryPort.upsert(receipt)
      → ReceiptEventOutPort.publish(ReceiptProcessedEvent)
         │
         │ EVENT
         ▼
PRICE-SERVICE
  ReceiptProcessedEventHandler
    → PriceInPort.syncFromReceipt(event)
    → Parses receipt items
    → Creates/updates Products
    → Creates Price records
    → Publishes ProductCreatedEvent, PriceUpdatedEvent
         │
         │ EVENTS
         ▼
SHOPPING-SERVICE (reads event store)
NOTIFICATION-SERVICE (checks watchlists)
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Dependency Rules

### Within a Service (Hexagonal)

```
infrastructure/adapter/web/         → application/port/in/
infrastructure/adapter/persistence/ → application/port/out/
infrastructure/adapter/event/       → application/port/out/
infrastructure/handler/event/       → application/port/in/
application/domain/service/         → application/port/out/
application/                        → nothing (pure Java)
```

### Between Services (Event-Driven)

```
receipt  ──event──→ price, notification
product  ──event──→ price, shopping
price    ──event──→ notification
store    ──event──→ price, shopping
```

No service imports another service's internal classes. The only shared dependency is `common` (event schemas, base types).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Testing Strategy

### In-Process (Monolith Phase)

| Test Type | Location | Technique |
|-----------|----------|-----------|
| Unit | `application/domain/service/*Test` | Mock all ports |
| Integration | `infrastructure/adapter/persistence/*Test` | `@DataJpaTest` |
| Controller | `infrastructure/adapter/web/*Test` | `@WebMvcTest` |
| Event | `infrastructure/handler/event/*Test` | Verify event consumption |
| Saga | End-to-end | Publish event → verify downstream |

### Distributed Phase

Same unit/integration tests per service, plus contract tests for event schemas and integration with embedded Kafka.

### Event Testing Example

```java
@Test
void shouldPublishEventWhenReceiptProcessed() {
    var eventCaptor = new ArgumentCaptor<ReceiptProcessedEvent>();
    receiptService.processReceipt(receiptId);

    then(receiptEventOutPort).should().publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue().getReceiptId()).isEqualTo(receiptId);
}
```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Monolith to Distributed

| Phase | What Changes |
|-------|-------------|
| **1. Monolith** | Multi-module Maven, Spring events, shared database |
| **2. Event Abstraction** | `EventOutPort` adapters with `@ConditionalOnProperty` |
| **3. Extract Services** | Module → standalone app, Kafka adapter, separate DB |
| **4. Distributed** | Containers, message broker, per-service databases, monitoring |

<p align="right">(<a href="#readme-top">back to top</a>)</p>

---

[hexagonal-shield]: https://img.shields.io/badge/Hexagonal-0052CC?style=for-the-badge&logo=hexo&logoColor=white
[hexagonal-url]: https://alistair.cockburn.us/hexagonal-architecture/
[event-shield]: https://img.shields.io/badge/Event_Driven-FF6B6B?style=for-the-badge&logo=apachekafka&logoColor=white
[event-url]: https://spring.io/guides/gs/messaging-rabbitmq/
[spring-shield]: https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[spring-url]: https://spring.io/projects/spring-boot
