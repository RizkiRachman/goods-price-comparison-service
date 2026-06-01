# Project State

## Current Focus
AI agent configuration optimized: GSD wired, AGENTS.md trimmed 60%, dead plugins removed, craft dir deleted, orchestrator upgraded with parallel execution.

## Active Decisions
- **GSD integration**: 11 `/gsd-*` commands available for structured phases (discuss, plan, execute, review, verify, ship, etc.)
- **Lean-ctx MCP**: Available at project level for token-efficient file ops
- **AGENTS.md trimmed**: 691→277 lines, reference content moved to skills
- **Parallel orchestrator**: Multi-service changes can fan-out across parallel subagents
- **Dead weight removed**: `opencode-mem`, `envsitter-guard`, `opencode-notify` plugins removed; `caveman` skill removed; `craft/` directory deleted

## Known Blockers
None.

## Recent Changes
| Date | Change |
|------|--------|
| 2026-06-01 | AI config optimization pass 2: removed dead plugins/skills, improved GSD prompts, trimmed PROJECT.md |
| 2026-06-01 | AI config optimization pass 1: 6→4 agents, GSD wired, AGENTS.md →277 lines, craft deleted, lean-ctx added |

## Quality Metrics
- **Tests**: ArchUnit (7 rules) + unit tests — run via `mvn test`
- **Gates**: Spotless → ArchUnit → SpotBugs → PMD CPD → check-conventions.sh
- **Coverage**: JaCoCo (soft target 90%, check commented out in pom.xml)
