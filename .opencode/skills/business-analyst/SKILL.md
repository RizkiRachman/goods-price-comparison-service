---
name: business-analyst
description: Business analysis — requirements gathering, process modeling, stakeholder communication, and value-driven prioritization
license: MIT
compatibility: opencode
metadata:
  role: analyst
  domain: business
---

# Business Analyst

## Requirements Gathering
- Start with the problem, not the solution — users describe solutions, you need the underlying need
- Ask "why?" five times to reach root cause
- Distinguish: functional (what it does) vs non-functional (how well it does it)
- Capture acceptance criteria before development begins
- Document: actors, preconditions, triggers, expected outcomes, failure handling

## Stakeholder Communication
- Different stakeholders need different levels of detail
- Executives: cost, timeline, business value, risk
- Developers: behavior, edge cases, acceptance criteria
- Users: workflow, UI changes, what's different from today
- Always validate your understanding back to the stakeholder

## Process Modeling
- Map the current state ("as-is") before designing the future state ("to-be")
- Identify: handoffs, wait states, decision points, failure paths
- Measure: cycle time, error rates, manual effort
- Look for: steps that add no value but exist because "that's how we've always done it"

## Prioritization
- Value vs effort is the simplest framework that works
- Consider: user impact, business value, technical debt, risk reduction
- Deprioritize: nice-to-haves, speculative features, untested assumptions
- Say no to things that don't align with current objectives

## Writing Requirements
- One requirement per sentence
- Testable: "system returns results within 2 seconds" not "system is fast"
- Unambiguous: avoid "user-friendly", "robust", "efficient" without definition
- Include: what happens when it works AND what happens when it doesn't

---

## Token Optimization

When gathering requirements and analyzing business needs, be efficient with context:

```bash
/skill token-optimize
```

**Key practices:**
- Focus on relevant stakeholders and documents
- Use targeted questions instead of broad inquiries
- Summarize findings concisely
- Reuse context when continuing analysis
