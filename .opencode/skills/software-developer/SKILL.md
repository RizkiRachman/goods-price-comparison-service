---
name: software-developer
description: Full-stack software development following clean code, SOLID, and project-specific conventions
license: MIT
compatibility: opencode
metadata:
  role: developer
  domain: engineering
---

# Software Developer Expertise

## Mindset
- Write code for humans first, machines second
- Favor clarity over cleverness
- Every abstraction has a cost — justify it
- Prefer composition over inheritance

## Before Writing Code
1. Understand the existing patterns in the codebase (read 2-3 similar files)
2. Check AGENTS.md and available skills for project conventions
3. Identify edge cases and failure modes upfront
4. Consider testability — if it's hard to test, the design is wrong

## Code Quality
- One responsibility per function/class. If you need "and" to describe it, split it.
- Names reveal intent: `calculateTotal()` not `processData()`
- Minimize mutable state. Prefer immutable data and pure functions.
- Fail fast: validate inputs at boundaries, not deep inside logic
- Handle errors at the appropriate layer — don't swallow exceptions silently
- Avoid null. When you can't, check and handle at the boundary.

## Testing
- Write tests alongside code, not after
- One logical assertion per test method
- Test behavior, not implementation
- Edge cases: empty, null, boundary values, concurrent access
- Integration tests cover the happy path + one failure mode per component

## When Reviewing Code
- Does it solve the stated problem?
- Are there missing edge cases?
- Is there duplicated logic that should be extracted?
- Are error messages actionable?
- Would the next developer understand this in 6 months?

## Communication
- Explain tradeoffs, not just decisions
- When proposing a change, include: what, why, and what you didn't do
- Admit when you don't know — ask clarifying questions

## Naming Conventions

### Handling Model Name Conflicts

When working with external API models that conflict with internal domain models:

**Priority Order:**
1. **API Model** (external JAR) - keep simple name
2. **Domain Model** (internal) - add "Domain" suffix
3. **Entity** (database) - add "Entity" suffix

**Example:**
```java
// External API Model
import com.example.api.model.Product;

// Internal Domain Model
import com.example.app.domain.model.ProductDomain;

// Database Entity
import com.example.app.infrastructure.entity.ProductEntity;
```

**Benefits:**
- Clear distinction between layers
- No fully qualified names needed
- Consistent naming across codebase

## Code Quality

### Eliminate Redundant Variables

Don't create intermediate variables just to reassign them:

**❌ BAD — Unnecessary intermediate variable**
```java
var sorted = comparator.reversed().compare(a, b);
result = sorted;  // Redundant
```

**✅ GOOD — Direct assignment**
```java
result = comparator.reversed().compare(a, b);
```

**❌ BAD — Variable created just for immediate reassignment**
```java
var tempList = items.stream().filter(x -> x > 0).toList();
items = tempList;  // Redundant
```

**✅ GOOD — Direct reassignment**
```java
items = items.stream().filter(x -> x > 0).toList();
```

**Rule:** If you create a variable and immediately assign it to another variable, eliminate the intermediate step.

### No Dead Code (YAGNI)

Never write code that isn't immediately used:

**Don't create:**
- Methods that are never called
- Variables that are never referenced
- Parameters that aren't used
- "Future-proofing" that isn't needed now

**Exceptions:**
- ✅ Constants and configuration values
- ✅ Utility functions designed for reuse
- ✅ Interface implementations (required methods)

**❌ BAD — Unused method**
```java
private void logDebugInfo() {  // Never called
    logger.debug("Debug info");
}
```

**❌ BAD — Speculative parameter**
```java
public void processOrder(Order order, boolean sendNotification) {
    // sendNotification parameter never used
    saveOrder(order);
}
```

**✅ GOOD — Add when needed**
```java
// Start simple
public void processOrder(Order order) {
    saveOrder(order);
}

// Add parameter only when feature is implemented
public void processOrder(Order order, boolean sendNotification) {
    saveOrder(order);
    if (sendNotification) {
        notificationService.send(order);
    }
}
```

**Rule:** YAGNI — You Ain't Gonna Need It. Don't write it until you need it.

---

## Token Optimization

When developing software and writing code, optimize context usage:

```bash
/skill token-optimize
```

**Key practices:**
- Read existing code patterns efficiently before implementing
- Search for similar implementations to follow conventions
- Focus on relevant files and modules
- Reuse context when working on related features
- Summarize design decisions concisely
