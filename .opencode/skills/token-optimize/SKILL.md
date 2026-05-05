---
name: token-optimize
description: Guidelines for efficient token usage - how to read files, batch operations, and manage context to minimize token consumption
license: MIT
compatibility: opencode
---

# Token Optimization Guide 💡

## Overview

Efficient token usage is critical for maintaining context window and reducing costs. This guide provides practical strategies for minimizing token consumption while working effectively.

---

## File Reading Efficiency

### Use Targeted Reads

**❌ BAD — Reading entire large files**
```
read filePath="/src/main/java/com/example/Service.java"
// Reads all 500 lines when you only need the method signature
```

**✅ GOOD — Read only what you need**
```
read filePath="/src/main/java/com/example/Service.java" limit=50
// Reads only first 50 lines (class declaration + imports)

read filePath="/src/main/java/com/example/Service.java" offset=200 limit=30
// Reads specific section (lines 200-230)
```

### Search Before Reading

**❌ BAD — Reading files blindly**
```
read filePath="/src/main/java/com/example/Service.java"
read filePath="/src/main/java/com/example/Repository.java"
read filePath="/src/main/java/com/example/Controller.java"
// Reading 3 files without knowing which contains the target code
```

**✅ GOOD — Search first, then read**
```
grep pattern="calculateTotal" path="/src/main/java/com/example"
// Find which file contains the method

read filePath="/src/main/java/com/example/Service.java" offset=150 limit=20
// Read only the relevant section
```

---

## Batch Operations

### Parallel Tool Calls

**❌ BAD — Sequential independent calls**
```
read filePath="/src/main/java/com/example/Product.java"
read filePath="/src/main/java/com/example/Order.java"
read filePath="/src/main/java/com/example/Customer.java"
// Each waits for the previous to complete
```

**✅ GOOD — Parallel calls**
```
read filePath="/src/main/java/com/example/Product.java"
read filePath="/src/main/java/com/example/Order.java"
read filePath="/src/main/java/com/example/Customer.java"
// All three read simultaneously (if independent)
```

### Combine Searches

**❌ BAD — Multiple separate searches**
```
grep pattern="class Product"
grep pattern="class Order"
grep pattern="class Customer"
```

**✅ GOOD — Single comprehensive search**
```
glob pattern="**/*.java"
grep pattern="class (Product|Order|Customer)"
```

---

## Context Management

### Focus on Relevant Files

**❌ BAD — Loading entire codebase**
```
glob pattern="**/*"
// Loads everything including tests, docs, config
```

**✅ GOOD — Narrow scope**
```
glob pattern="src/main/java/com/example/service/**/*.java"
// Only service layer files
```

### Concise Summaries

**❌ BAD — Verbose output**
```
"I found the file. It contains a class called ProductService with methods 
createProduct, updateProduct, deleteProduct, findById, findAll, searchProducts...
[200 lines of method descriptions]"
```

**✅ GOOD — Concise summary**
```
"ProductService found with 6 CRUD methods. Key: createProduct() uses 
ProductRepository.save(), handles null checks with ObjectUtils."
```

### Clear Context When Switching

**When moving to a new task:**
- Summarize findings from previous task
- Explicitly state what's being discarded
- Start fresh context for new task

---

## Task Context Reuse

### Preserve Context with task_id

**❌ BAD — Starting fresh each time**
```java
// First call
Task(description="Create service", prompt="Create ProductService...")

// Second call (reloads everything)
Task(description="Add method", prompt="Add updateProduct to ProductService...")
// Re-reads Product, ProductRepository, etc.
```

**✅ GOOD — Reuse task_id**
```java
// First call
Task(description="Create service", prompt="Create ProductService...", 
     task_id="ses_abc123")

// Second call (continues same context)
Task(description="Add method", prompt="Add updateProduct method...",
     task_id="ses_abc123")
// Already has ProductService context, just adds new method
```

### When to Use task_id

**Use the same task_id when:**
- Continuing implementation on same feature
- Adding to recently created code
- Fixing bugs in recently written code
- Making incremental improvements

**Start fresh (new task_id) when:**
- Switching to completely different feature
- After long gap (context likely stale)
- Working on unrelated module
- User explicitly requests fresh start

---

## Practical Examples

### Example 1: Finding a Method

**❌ Inefficient**
```
read filePath="/src/Service.java"  // 300 lines
grep pattern="calculatePrice"  // Search through output manually
```

**✅ Efficient**
```
grep pattern="calculatePrice" path="/src"
read filePath="/src/Service.java" offset=120 limit=15
```

### Example 2: Understanding Architecture

**❌ Inefficient**
```
read filePath="/architecture.md"
read filePath="/README.md"
read filePath="/CONTRIBUTING.md"
// Reading all docs upfront
```

**✅ Efficient**
```
read filePath="/AGENTS.md" limit=100  // Only first 100 lines
// Start working, read more only when needed
```

### Example 3: Reviewing Multiple Files

**❌ Inefficient**
```
read filePath="/Product.java"
read filePath="/ProductService.java"  
read filePath="/ProductRepository.java"
read filePath="/ProductController.java"
read filePath="/ProductMapper.java"
// Sequential, lots of content
```

**✅ Efficient**
```
glob pattern="**/Product*.java"
grep pattern="(class|interface) Product"  // Just find definitions
read filePath="/ProductService.java" limit=50  // Only key methods
// Read others only when specific questions arise
```

---

## Token Budget Guidelines

### Typical Costs

| Operation | Approximate Tokens |
|-----------|-------------------|
| Read small file (<100 lines) | 500-1000 |
| Read large file (>500 lines) | 3000-5000 |
| Grep search | 200-500 |
| Glob listing | 100-300 |
| Parallel batch (5 files) | 1500-3000 |
| Sequential (5 files) | 1500-3000 (but slower) |
| Full context reload | 3000-8000 |

### Budgeting Strategy

**For complex tasks:**
1. **Discovery phase** (10% of budget) — glob + grep to find relevant files
2. **Analysis phase** (30% of budget) — read key sections
3. **Implementation phase** (50% of budget) — write code
4. **Verification phase** (10% of budget) — run tests, check quality

**Warning signs of token waste:**
- Reading same file multiple times
- Loading entire directories when only few files needed
- Verbose explanations instead of concise summaries
- Not using task_id for related work

---

## Quick Reference

### Checklist Before Reading Files

- [ ] Can I use `grep` to find the specific line first?
- [ ] Do I need the whole file or just a section?
- [ ] Can I use `limit` and `offset` parameters?
- [ ] Is there a smaller file with the same info?

### Checklist Before Tool Calls

- [ ] Can these calls be parallelized?
- [ ] Am I searching for multiple related patterns?
- [ ] Can I combine glob + grep instead of reading files?
- [ ] Do I already have this info in context?

### Checklist for Context Management

- [ ] Am I reusing task_id for related work?
- [ ] Should I summarize and clear context before switching tasks?
- [ ] Is my output concise or verbose?
- [ ] Am I focusing on relevant files only?

---

## Summary

**Remember:**
1. 🔍 Search before reading
2. 📄 Read sections, not entire files
3. ⚡ Parallelize independent operations
4. 🎯 Stay focused on relevant files
5. 📝 Be concise in summaries
6. 🔄 Reuse task_id for related work

**Result:** More efficient token usage, faster responses, better context retention.
