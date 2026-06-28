# ADR-003: Receipt OCR Processing Pipeline

**Status:** Proposed (architecture designed, awaiting Kafka deployment)

## Context

Receipt processing involves multiple steps:
1. Image validation (format, size, resolution)
2. Preprocessing (rotation, contrast enhancement, cropping)
3. Text detection (OCR via LLM vision capabilities or dedicated OCR engine)
4. Price extraction (parse detected text into product items and prices)
5. Validation (confidence check, anomaly detection)

Steps 1-3 are CPU/GPU-bound. Steps 4-5 are LLM-bound. Running them in a single synchronous request blocks the caller for 5-30 seconds.

## Decision

Split into a 3-stage pipeline connected by Kafka topics:

```
Stage 1: Image Validation     ->  receipt.image.validated
Stage 2: OCR + Extraction     ->  receipt.ocr.completed
Stage 3: Price Normalization  ->  receipt.price.normalized
```

Each stage is an independent consumer group, allowing:
- Independent scaling (Stage 2 may need GPU instances)
- Independent failure handling (retry Stage 2 without reprocessing Stage 1)
- Parallel processing of different receipts
- Visibility into stage-by-stage latency

## Consequences

**Positive:**
- Processing time visible per stage
- Failed stages can be retried independently
- Pipeline can be extended with additional stages
- Backpressure if downstream is slow

**Negative:**
- Total latency equals sum of stages (no speedup)
- Stale receipts may accumulate in topics
- Each stage must be idempotent

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| All-in-one async job | Harder to debug, can't retry partial failures |
| Synchronous with timeout | Blocks threads, poor user experience |
