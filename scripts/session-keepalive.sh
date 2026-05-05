#!/bin/bash
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
RUN_CONFIG="$PROJECT_ROOT/.run/Application.run.xml"
ACTION="${1:-start}"
shift || true

load_env() {
  if [ ! -f "$RUN_CONFIG" ]; then
    echo "Error: $RUN_CONFIG not found" >&2
    exit 1
  fi
  xmllint --xpath '//envs/env' "$RUN_CONFIG" 2>/dev/null | \
    sed -n 's/<env name="\([^"]*\)" value="\([^"]*\)"\/>/export \1="\2"/p'
}

set_tmux_env() {
  xmllint --xpath '//envs/env' "$RUN_CONFIG" 2>/dev/null | \
    sed -n 's/<env name="\([^"]*\)" value="\([^"]*\)"\/>/tmux set-environment -g \1 "\2"/p'
}

case "$ACTION" in
  start)
    eval "$(load_env)"
    exec caffeinate -i mvn spring-boot:run "$@"
    ;;
  tmux)
    SESSION="gpcs-$(date +%Y%m%d)"
    tmux kill-session -t "$SESSION" 2>/dev/null || true
    eval "$(load_env)"
    eval "$(set_tmux_env)"
    caffeinate -i tmux new-session -s "$SESSION" \; \
      set-option -g remain-on-exit on \; \
      send-keys "mvn spring-boot:run" Enter
    echo "tmux session: $SESSION (reattach: tmux attach -t $SESSION)"
    ;;
  help|*)
    echo "Usage: $0 {start|tmux}"
    echo ""
    echo "  start    Run mvn spring-boot:run with caffeinate (in current terminal)"
    echo "  tmux     Start in a tmux session (survives logout)"
    echo ""
    echo "Both load env vars from .run/Application.run.xml"
    ;;
esac
