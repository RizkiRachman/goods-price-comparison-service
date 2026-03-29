# AI Documentation Index

Welcome, AI Agent! This folder contains all guidelines for working on the goods-price-comparison-service project.

---

## 📚 Quick Reference

| Document | Purpose | When to Read |
|----------|---------|--------------|
| [AGENTS.md](AGENTS.md) | Main guidelines, PR blocking rules, quality standards | **Before starting any work** |
| [RULES.md](RULES.md) | Coding standards, naming conventions, forbidden patterns | **Before writing code** |
| [SKILLS.md](SKILLS.md) | Required technical skills and capabilities | **Review for context** |
| [PR_WORKFLOW.md](PR_WORKFLOW.md) | Step-by-step PR creation with blocking checks | **Before creating PR** |
| [COVERAGE.md](COVERAGE.md) | Detailed coverage requirements and how to achieve | **When tests fail coverage** |

---

## 🚀 Quick Start for AI Agents

### 1. First Time Here?

1. Read [AGENTS.md](AGENTS.md) - This is your bible
2. Read [RULES.md](RULES.md) - Learn the coding standards
3. Review project [README.md](../README.md) - Understand the project

### 2. Before Writing Code

Check [RULES.md](RULES.md) for:
- Naming conventions
- Code structure
- Testing requirements
- Documentation standards

### 3. Before Creating PR

Follow [PR_WORKFLOW.md](PR_WORKFLOW.md):
```bash
# Run these in order:
1. mvn clean compile -q          # Must pass
2. mvn test                      # Must pass (100%)
3. mvn jacoco:report             # Check coverage >= 90%
4. mvn checkstyle:check          # Must pass (0 violations)
5. mvn spotbugs:check            # Must pass (0 high priority)
6. ./scripts/update-readme.sh    # Update documentation
7. git commit && git push
8. Create PR only after ALL pass
```

---

## 🎯 Key Reminders

### PR Blocking (NON-NEGOTIABLE)

```
❌ Cannot create PR if:
   - Build fails
   - Tests fail
   - Coverage < 90% (existing) or < 100% (new)
   - Checkstyle violations
   - SpotBugs high priority bugs
   - README not updated
```

### Code Coverage Requirements

```
Existing Code: >= 90%
New Code:      100% (mandatory)
Target:        95%+
```

### Documentation Structure

```
Root: Only README.md
Docs: Link to docs/ folder
AI:   This .ai/ folder (AI only, humans don't edit)
```

---

## 🆘 Need Help?

### Common Issues

**Build fails?**
→ Check [AGENTS.md](AGENTS.md) - Build Verification section

**Tests fail?**
→ Check [RULES.md](RULES.md) - Testing Rules section

**Coverage low?**
→ Check [COVERAGE.md](COVERAGE.md) - Coverage Improvement section

**Don't know how to structure code?**
→ Check [RULES.md](RULES.md) - Class Design Rules

**Unsure about a feature?**
→ Check project [README.md](../README.md) - Roadmap section

---

## 📋 Pre-Work Checklist

Before starting any task:

- [ ] Read AGENTS.md completely
- [ ] Understand the feature/requirement
- [ ] Check existing code patterns
- [ ] Review RULES.md for relevant section
- [ ] Plan test strategy (aim for 100% coverage)

---

## ✅ Pre-PR Checklist

Before creating PR:

- [ ] `mvn clean compile -q` passes
- [ ] `mvn test` passes (100%)
- [ ] Coverage >= 90% (100% for new code)
- [ ] Checkstyle passes (0 violations)
- [ ] SpotBugs passes (0 high priority)
- [ ] Integration tests pass
- [ ] README.md updated with changes
- [ ] JavaDoc added for public methods
- [ ] No TODO/FIXME without issue number
- [ ] Meaningful commit messages

**Only then create PR!**

---

## 🔗 External Resources

### Project Documentation
- [Main README](../README.md) - Project overview
- [Architecture](../docs/ARCHITECTURE.md) - System design
- [API Docs](../docs/API.md) - API endpoints
- [Database](../docs/DATABASE.md) - Schema details

### Related Projects
- [common-utils-java](https://github.com/RizkiRachman/common-utils-java)
- [common-exception-java](https://github.com/RizkiRachman/common-exception-java)

---

## 🎓 Learning Path

### For New AI Agents

1. **Day 1**: Read AGENTS.md + RULES.md
2. **Day 2**: Review existing code structure
3. **Day 3**: Attempt small fix/feature with PR
4. **Ongoing**: Reference docs as needed

### For Experienced Agents

1. Quick scan of AGENTS.md (check for updates)
2. Review PR_WORKFLOW.md for any changes
3. Start development

---

## 📝 Document Updates

These documents are living documents:
- Updated when new patterns emerge
- Modified when standards change
- Improved based on PR feedback

**Last Updated**: [AUTO-UPDATED BY CI]

---

**Remember: Quality over speed. Read before coding, test before PR.**

*Happy coding, AI Agent! 🤖*