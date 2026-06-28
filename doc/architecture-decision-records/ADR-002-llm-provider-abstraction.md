# ADR-002: LLM Provider Abstraction via Adapter Pattern

**Status:** Implemented (AbstractRestLlmProvider)

## Context

The service extracts product prices from receipt images using LLMs. Multiple providers exist (OpenAI GPT-4o, Google Gemini 2.0 Flash) with different:
- API shapes (REST endpoints, request/response schemas)
- Rate limits
- Pricing models
- Latency profiles

Hardcoding each provider creates duplication and makes switching expensive.

## Decision

Use the Adapter pattern with a common abstract base class AbstractRestLlmProvider.

```
LlmProvider (interface)
  └── AbstractRestLlmProvider (abstract class)
        ├── OpenAiLlmProvider
        ├── GeminiLlmProvider
        └── (future providers)
```

The abstract class handles:
- Request serialization/deserialization via Jackson
- HTTP transport via RestTemplate/WebClient
- Retry with exponential backoff (planned Resilience4j integration)
- Response parsing into a common LlmExtractionResult

Each concrete provider only implements:
- Provider-specific request construction
- Provider-specific response parsing
- Provider metadata (name, model, max tokens)

## Consequences

**Positive:**
- Adding a provider takes hours, not days
- Testing with mock providers is straightforward
- Can A/B test providers in production
- Cost optimization: route simple receipts to cheap providers

**Negative:**
- Common schema means lowest-common-denominator features
- Provider-specific capabilities (e.g., Gemini's vision features) need special handling

## Alternatives Considered

| Alternative | Reason Rejected |
|---|---|
| Single provider | Vendor lock-in, no fallback |
| LangChain/Spring AI | Overkill for two REST calls, adds dependency churn |
