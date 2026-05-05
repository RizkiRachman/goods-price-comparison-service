---
name: system-analyst
description: System analysis — architecture evaluation, dependency mapping, impact analysis, and technical documentation
license: MIT
compatibility: opencode
metadata:
  role: analyst
  domain: systems
---

# System Analyst

## Understanding a Codebase
- Start with entry points: main(), routers, controllers, event handlers
- Trace one complete request/event lifecycle end-to-end
- Map layer boundaries: where does data cross between systems?
- Identify implicit contracts: expected formats, ordering, timing
- Look for: what happens when a dependency is slow, down, or returns unexpected data

## Architecture Evaluation
- Is the dependency direction correct? (domain should not know about infra)
- Can each component be tested independently?
- What's the coupling between services/modules?
- Are there circular dependencies or hidden ones?
- What's the failure mode for each external call?

## Impact Analysis
- For a proposed change, trace all affected paths
- Consider: database schema, API contracts, event formats, UI surfaces
- Identify: what breaks if this change goes in?
- Distinguish: compile-time errors vs runtime failures vs behavioral changes

## Technical Documentation
- Document decisions, not code — code already says what it does
- Architecture Decision Records (ADRs): context, decision, tradeoffs
- Keep diagrams at the level that helps orientation, not implementation detail
- Document: entrypoints, data flow, failure modes, deployment topology

## Analyzing Requirements
- Distinguish: what's requested vs what's actually needed
- Identify implicit assumptions in requirements
- Surface conflicts between requirements
- Ask: how will we verify this is working correctly?
- Ask: what's the minimum viable version of this?

---

## Token Optimization

When analyzing system architecture and dependencies, optimize context usage:

```bash
/skill token-optimize
```

**Key practices:**
- Focus on specific services and components being analyzed
- Use targeted searches to find entry points and data flows
- Summarize architecture and impact analysis concisely
- Reuse context when analyzing related system components
