# Agent Orchestration — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add shared JSON envelope, state machine, scoring pipeline, and retry loop to the orchestrator — using existing tools, no new MCPs or plugins.

**Architecture:** The orchestrator reads `.opencode/orchestration/template.json` at session start, populates task-specific fields, writes back after each delegation. Between delegations it runs a scoring pipeline (rule checks + LLM judge) and decides PASS/RETRY/BLOCKED via state machine.

**Tech Stack:** Existing opencode agent system, lean-ctx MCP for persisting envelope, gitnexus for blast radius checks, existing subagent delegation.

**Files:**
- Create: `.opencode/orchestration/template.json`
- Modify: `.opencode/agents/orchestrator.md` — add envelope lifecycle, scoring step, retry loop, state transitions

---

### Task 1: Create the Shared JSON Envelope Template

**File:** Create `.opencode/orchestration/template.json`

This is the single source of truth that all agents read and the orchestrator manages. It starts empty (task populates fields) and persists across the session via `lean-ctx ctx_knowledge`.

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
    "active_agent": "",
    "mode": "",
    "applicable_skills": [],
    "rules_references": [
      { "source": "AGENTS.md", "sections": ["§3 - Non-Negotiable Rules", "§5 - Writing Order", "§8 - Null Handling"] },
      { "source": "ARCHITECTURE_HYBRID.md", "sections": ["§2 - Hexagonal Structure"] },
      { "source": "PROJECT.md", "sections": ["scope", "constraints"] }
    ],
    "current_guidance": "",
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

- [ ] **Step 1: Create directory and template file**

Run:
```bash
mkdir -p .opencode/orchestration
```

Then write the template JSON above to `.opencode/orchestration/template.json`.

- [ ] **Step 2: Commit**

```bash
git add .opencode/orchestration/template.json
git commit -m "feat: add shared JSON envelope template for agent orchestration"
```

---

### Task 2: Add Envelope Read/Writes to Orchestrator Prompt

**File:** Modify `.opencode/agents/orchestrator.md`

Add a new section to the orchestrator's Workflow that loads the template at start and persists it via `lean-ctx ctx_knowledge` after each delegation.

- [ ] **Step 1: Read current orchestrator.md**

Read `.opencode/agents/orchestrator.md` to understand current structure.

- [ ] **Step 2: Add envelope lifecycle to Context Load section**

Replace the existing `### 0. Context Load` section to include template loading:

```markdown
### 0. Context Load
- Read `PROJECT.md` for project vision, scope, and constraints
- Read `STATE.md` for current position, active decisions, and blockers
- Read `AGENTS.md` for project conventions (architecture, rules, writing order)
- **Load shared envelope** (`lean-ctx ctx_knowledge recall --key orchestration-envelope`)
  - If no envelope exists: read `.opencode/orchestration/template.json`, populate session fields, write via `lean-ctx ctx_knowledge remember key orchestration-envelope value <envelope JSON>`
  - If envelope exists: read `state` and `retry.issues` to know where to resume
- **Before editing any symbol:** run `gitnexus_impact({target, direction: "upstream"})` to check blast radius
- Load relevant skills via `/skill` as needed
- If resuming from a previous session: use `ctx_session load` to restore context
- If context grows large: use `ctx_session save` to persist state, then `ctx_compress --signatures` to compact the window
```

- [ ] **Step 3: Add envelope persistence after delegation**

Add at the end of the workflow, before Ship:

```markdown
### 5.5 Envelope Persist
- After each subagent returns and scoring completes, update the shared envelope:
  1. Read current envelope from `lean-ctx ctx_knowledge recall --key orchestration-envelope`
  2. Update `state`, `outputs.<phase>`, `score.*`, `retry.*`
  3. Write back via `lean-ctx ctx_knowledge remember key orchestration-envelope value <updated JSON>`
```

- [ ] **Step 4: Commit**

```bash
git add .opencode/agents/orchestrator.md
git commit -m "feat: add envelope lifecycle to orchestrator -- load template, persist after delegation"
```

---

### Task 3: Add State Machine to Orchestrator Prompt

**File:** Modify `.opencode/agents/orchestrator.md`

Add state machine rules that gate which delegation happens next based on `state`.

- [ ] **Step 1: Read orchestrator.md**

Read the full orchestrator.md to understand the current delegation flow.

- [ ] **Step 2: Add State Machine section**

Add after the envelope persist section:

```markdown
### 5.6 State Machine

The orchestrator drives transitions based on the shared envelope's `state` field:

```
INIT → PLAN → PLAN_SCORED → EXECUTE → EXECUTE_SCORED → REVIEW → REVIEW_SCORED → COMPLETE
BLOCKED (any phase) → user intervention → INIT
```

**Transition Rules:**
- `PLAN_SCORED → EXECUTE` — only if `score.combined ≥ 70`
- `EXECUTE_SCORED → REVIEW` — only if `score.combined ≥ 70`
- Any phase → `BLOCKED` — if `score.combined < escalation_threshold (50)` OR `retry.attempt ≥ max_attempts (3)`

**Before each delegation:**
1. Read envelope state
2. Validate the transition is legal
3. If illegal: set `state = BLOCKED`, write issue to `retry.issues[]`, persist envelope, stop

**After each delegation + scoring:**
1. Update envelope state to SCORED variant
2. Check score against thresholds
3. PASS → advance state, delegate next
4. RETRY → increment `retry.attempt`, re-delegate with `retry.issues[]` as feedback
5. BLOCKED → push partial work, stop
```

- [ ] **Step 3: Commit**

```bash
git add .opencode/agents/orchestrator.md
git commit -m "feat: add state machine with transition rules to orchestrator"
```

---

### Task 4: Add Scoring Pipeline to Orchestrator Prompt

**File:** Modify `.opencode/agents/orchestrator.md`

Add the three-tier scoring step that runs after each subagent delegation.

- [ ] **Step 1: Read orchestrator.md**

Verify current structure to know where to insert the scoring step.

- [ ] **Step 2: Add Scoring Step section**

Add between delegation and state machine:

```markdown
### 5.4 Scoring Pipeline

After each subagent returns output, run the three-tier scoring pipeline.

**Tier 1 — Rule-Based Scoring (tool calls, no LLM):**

Base score: 100. Deduct for each violation found:

| Check | Method | Deduction |
|-------|--------|-----------|
| Schema valid | Parse output against expected structure | -15 |
| Permissions violated | Check for forbidden patterns (JPA annotations, FQN, push to main) | -40 |
| Blast radius safe | `gitnexus_impact` on changed symbols | -40 if HIGH/CRITICAL |
| Writing order correct | Verify port→service→mapper→adapter order | -15 |
| Required fields present | Check expected keys are non-null | -15 |

If Tier 1 subtotal < 70: set `score.verdict = "RETRY"`, skip Tier 2.

If Tier 1 subtotal ≥ 70: proceed to Tier 2.

**Tier 2 — LLM-as-Judge (delegate to scorer subagent):**

Create a temporary judge prompt:

```
Given task requirements, governance rules, and the agent's output:
Score 0-100 on:
- Fulfills requirements? (0-40)
- Follows governance? (0-30)
- Completeness? (0-20)
- Edge cases covered? (0-10)
Return JSON: { score: N, rationale: "...", missing_items: ["..."] }
```

Run this via `subtask()` or a lightweight agent call. Parse the JSON result.

**Tier 3 — Combined Verdict:**

```
combined = Tier 2 score (or Tier 1 subtotal if Tier 2 skipped)

combined ≥ 70  → verdict = "PASS"
combined 50-69 → verdict = "RETRY" (if attempt < max_attempts)
combined < 50  → verdict = "BLOCKED"
```

**Update envelope:**
```json
{
  "score": {
    "rules": { "pass": N, "fail": N, "deduction": N, "subtotal": N },
    "judge": { "score": N, "rationale": "...", "missing_items": [...] },
    "combined": N,
    "verdict": "PASS|RETRY|BLOCKED"
  },
  "retry": {
    "attempt": N,
    "issues": [...]
  }
}
```

After scoring, persist envelope via `lean-ctx ctx_knowledge`.
```

- [ ] **Step 3: Commit**

```bash
git add .opencode/agents/orchestrator.md
git commit -m "feat: add three-tier scoring pipeline -- rule checks + LLM judge + combined verdict"
```

---

### Task 5: Add BLOCKED Escalation to Orchestrator Prompt

**File:** Modify `.opencode/agents/orchestrator.md`

Add the escalation path when the state machine hits BLOCKED.

- [ ] **Step 1: Read orchestrator.md**

- [ ] **Step 2: Add escalation to Ship section**

Add to the Ship section:

```markdown
### 6. Ship

**BLOCKED escalation:**
If state = BLOCKED:
1. Read envelope from `lean-ctx ctx_knowledge recall --key orchestration-envelope`
2. Push current branch with `[BLOCKED]` prefix to indicate partial work
3. Write issues to branch commit message: `"BLOCKED at ${phase}: ${issues[0]}"`
4. Summarize blockers to user: `"I hit BLOCKED at ${phase}. Issues: ${issues}. Please review and decide: adjust threshold, fix guidance, or discard."`
5. Stop — do not continue.

**Normal completion:**
If state = COMPLETE and scoring passed:
- Resolve any remaining issues
- Confirm deploy safety: migrations backward-compatible, env vars documented, rollback ready
- Run `/skill release-plan` for release process guidance
- Summarize what was done
- Confirm ready for deployment
```

- [ ] **Step 3: Commit**

```bash
git add .opencode/agents/orchestrator.md
git commit -m "feat: add BLOCKED escalation -- push partial work, notify user with issues"
```

---

### Task 6: Wire Envelope Into Subagent Delegation

**File:** Modify `.opencode/agents/orchestrator.md`

When delegating to planner/task-manager/code-reviewer, inject relevant envelope sections into their prompt context so they know what to work on and what constraints apply.

- [ ] **Step 1: Read orchestrator.md delegation sections**

- [ ] **Step 2: Update delegation calls to include envelope context**

In each delegation section (Plan / Build / Review), add:

```markdown
### 2. Plan
Delegate to @planner (`.opencode/agents/planner.md`). Inject envelope context:
- Include `requirements`, `governance.rules_references`, `governance.current_guidance` in the prompt
- Include `retry.issues[]` if this is a retry (so planner knows what was wrong before)
```

- [ ] **Step 3: Commit**

```bash
git add .opencode/agents/orchestrator.md
git commit -m "feat: inject envelope context into subagent delegations -- requirements, governance, retry feedback"
```

---

### Task 7: Self-Review & Verification

- [ ] **Step 1: Read the full updated orchestrator.md**

Read `.opencode/agents/orchestrator.md` end-to-end and verify:
- Envelope lifecycle (load → delegate → score → persist → transition) is complete and sequential
- State transitions correctly gate each delegation
- Scoring deduplicates: Tier 2 only runs if Tier 1 passes
- BLOCKED escalation stops execution cleanly
- No section contradicts another (e.g., scoring thresholds match between Scoring and State Machine sections)
- All lean-ctx commands use the correct key name: `orchestration-envelope`

- [ ] **Step 2: Verify template.json**

Read `.opencode/orchestration/template.json` and verify all fields match the spec.

- [ ] **Step 3: Create feature branch and push**

```bash
git checkout -b feature/20260604-agent-orchestration
git push origin feature/20260604-agent-orchestration
```
