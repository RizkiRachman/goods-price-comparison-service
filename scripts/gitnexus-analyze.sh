#!/usr/bin/env bash
# Wrapper: npx gitnexus analyze --skip-agents-md
# Skips CLAUDE.md/AGENTS.md generation — we use OpenCode, not Claude Code.
# Usage: bash scripts/gitnexus-analyze.sh
set -euo pipefail
cd "$(git rev-parse --show-toplevel)"
npx gitnexus analyze --skip-agents-md
