# Agent Orchestration Design — Single Model, Contract-Based with Scoring

**Date**: 2026-06-04
**Status**: Draft

---

## Problem

Using different AI models across agents causes:
- **Protocol drift** — different models produce different output formats/styles
- **Rules not followed** — cheaper models skip governance constraints
- **Chain chaos** — fallback chain switches mid-task to a model with different behavior
- **Direct push / bypass** — agents push to main or skip steps
- **Late feedback** — user only sees results at PR time; big rework cycles

## Solution

One model for all agents + four pillars: **Shared JSON Envelope**, **State Machine**, **Scoring Pipeline**, **Contract-Based Handoffs**.

---

## 1. The Shared JSON Envelope

Persisted in `.opencode/orchestration/template.json` — the single source of truth that every agent reads and the orchestrator updates.

```json
{
  "state": "INIT",
  "session": {
    "task_id": "",
    "branch": "",
    "created_at": ""
  },
  "requirements": {
    "goal": "",
    "acceptance_criteria": [],
    "constraints": []
  },
  "decisions": {
    "approved_architecture": null,
    "coding_standard": [],
    "rejected_approaches": []
  },
  "governance": {
    "active_agent": "orchestrator",
    "mode": "primary",
    "applicable_skills": [],
    "rules_references": [
      { "source": "AGENTS.md", "sections": ["§3 - Non-Negotiable Rules", "§5 - Writing Order", "§8 - Null Handling"] },
      { "source": "ARCHITECTURE_HYBRID.md", "sections": ["§2 - Hexagonal Structure"] },
      { "source": "PROJECT.md", "sections": ["scope", "constraints"] }
    ],
    "current_guidance": "",
    "permissions": {
      "do": [],
      "dont": [
        "push to main branch",
        "modify CI/CD or .opencode/ config",
        "use fully qualified names inline",
        "return Optional<T> from ports",
        "use JPA relationship annotations (@ManyToOne etc.)"
      ]
    },
    "previous_blockers": [],
    "decisions_log": []
  },
  "outputs": {
    "plan": null,
    "architecture": null,
    "code_changes": [],
    "test_results": null
  },
  "score": {
    "rules": { "pass": 0, "fail": 0, "deduction": 0, "subtotal": 0 },
    "judge": { "score": 0, "rationale": "", "missing_items": [] },
    "combined": 0,
    "verdict": "PENDING"
  },
  "retry": {
    "current_phase": null,
    "attempt": 0,
    "max_attempts": 3,
    "score_threshold": 70,
    "escalation_threshold": 50,
    "issues": []
  }
}
```

Per-agent `permissions.do/dont` are derived per-agent config — the template above shows the shared defaults.

---

## 2. State Machine

### States

```
INIT → PLAN → PLAN_SCORED → EXECUTE → EXECUTE_SCORED → REVIEW → REVIEW_SCORED → COMPLETE
```

### Transitions

| From | To | Gate |
|------|----|------|
| `INIT` | `PLAN` | Task assigned, requirements populated |
| `PLAN` | `PLAN_SCORED` | Planner output received |
| `PLAN_SCORED` | `EXECUTE` | Combined score ≥ 70 |
| `PLAN_SCORED` | `BLOCKED` | Score < 50, or 3 retries exhausted |
| `EXECUTE` | `EXECUTE_SCORED` | Task-manager output received |
| `EXECUTE_SCORED` | `REVIEW` | Combined score ≥ 70 |
| `EXECUTE_SCORED` | `BLOCKED` | Score < 50, or 3 retries exhausted |
| `REVIEW` | `REVIEW_SCORED` | Code-reviewer output received |
| `REVIEW_SCORED` | `COMPLETE` | Combined score ≥ 70 |
| `REVIEW_SCORED` | `BLOCKED` | Score < 50, or 3 retries exhausted |
| `BLOCKED` | `INIT` | User intervenes, adjusts requirements/thresholds |

### Auto-Escalation

When `state = BLOCKED`:
1. Orchestrator writes `retry.issues[]` to envelope
2. Writes a summary to the PR description: `"[BLOCKED] Blocked at ${phase}: ${issues}"`
3. Pushes branch with partial work
4. Stops — user reviews and decides: adjust threshold, fix guidance, or discard

---

## 3. Output Scoring — Three-Tier Pipeline

### Tier 1: Rule-Based (tool calls, no LLM)

| Check | Method | Weight |
|-------|--------|--------|
| Schema valid | `JSON.parse` against expected schema | -15 on fail |
| Permissions violated | grep/regex for forbidden patterns | -40 on fail |
| Blast radius safe | `gitnexus_impact` | -40 on HIGH/CRITICAL |
| Writing order correct | grep for port → service → mapper order | -15 on fail |
| Required fields present | null check on expected keys | -15 on fail |

Base score: 100. Each fail deducts.

### Tier 2: LLM-as-Judge (dedicated scorer agent)

If Tier 1 passes (subtotal ≥ 70):

```
Judge prompt:
  Given: requirements, governance rules, agent output
  Evaluate:
    - Fulfills requirements? (0-40)
    - Follows governance? (0-30)
    - Completeness? (0-20)
    - Edge cases covered? (0-10)
  Output: { score: N, rationale: "...", missing_items: [...] }
```

### Tier 3: Combined Decision

| Combined Score | Verdict | Action |
|---------------|---------|--------|
| ≥ 70 | `PASS` | Advance state |
| 50-69, attempt < 3 | `RETRY` | Re-run agent with `issues[]` as feedback |
| < 50, or attempt ≥ 3 | `BLOCKED` | Stop, escalate to user |

### Retry Loop

```
Agent output → score 58, attempt 1
  → Re-run with feedback: "Missing edge cases for null inputs"
  → New output → score 72, attempt 2 → PASS
```

---

## 4. Agent Handoff Contracts

Each agent produces a typed JSON output with a defined schema. The next agent receives it as structured input.

### Planner Output
```json
{ "plan": { "goal": "", "files": [], "order": [], "risks": [] } }
```

### Task-Manager Output
```json
{ "changes": [{ "file": "", "type": "create|modify", "summary": "" }], "tests": [] }
```

### Code-Reviewer Output
```json
{ "findings": [{ "severity": "HIGH", "file": "", "issue": "", "recommendation": "" }] }
```

The orchestrator validates each output against its schema before scoring. Mismatch → instant retry.

---

## 5. MCP & Tool Usage

| Tool | Used For |
|------|----------|
| `lean-ctx ctx_knowledge recall` | Load envelope at session start |
| `lean-ctx ctx_knowledge remember` | Persist envelope after each state change |
| `lean-ctx ctx_read` | Read template.json + agent configs |
| `gitnexus_impact` | Tier 1 blast radius check on code changes |
| `gitnexus_detect_changes` | Verify scope before PR |
| All existing tools | Normal agent work |

No new MCPs or plugins needed.

---

## 6. Implementation Order

1. **Create `.opencode/orchestration/template.json`** — the envelope template
2. **Update orchestrator prompt** — read template at start, write after each delegation, enforce state machine
3. **Add scoring step** — Tier 1 rule checks as tool calls, Tier 2 judge as scorer agent delegation
4. **Add retry loop** — orchestrator loops on low score, tracks `retry.attempt`
5. **Wire BLOCKED escalation** — push branch with partial work, write issues to PR description
6. **Per-agent contract validation** — define schemas per agent output, validate before scoring

---

## Edge Cases & Risks

| Scenario | Mitigation |
|----------|------------|
| Retry loop infinite | Hard cap at 3 attempts per phase → BLOCKED |
| Template grows too large | Archive old decisions to `lean-ctx`, keep only current session in template |
| Scorer is wrong | User can override score and manually advance state |
| Compaction loses envelope | `lean-ctx ctx_knowledge` is compaction-proof; orchestrator re-reads it |
| Agent bypasses template | Template is injected into agent system prompt; `permissions.dont` catches violations |
