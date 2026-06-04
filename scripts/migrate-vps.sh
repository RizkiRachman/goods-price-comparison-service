#!/usr/bin/env bash
# =============================================
# VPS Flyway Migration Script
# =============================================
# Usage: ./scripts/migrate-vps.sh
#
# Required environment variables:
#   DATABASE_HOST     - PostgreSQL host (default: localhost)
#   DATABASE_PORT     - PostgreSQL port (default: 5432)
#   DATABASE_NAME     - Database name
#   DATABASE_USERNAME - Database user
#   DATABASE_PASSWORD - Database password
#
# Optional:
#   MAVEN_FLAGS       - Additional Maven flags (e.g. "-o" for offline)
# =============================================

set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

err() { echo -e "${RED}[ERROR]${NC} $*" >&2; }
info() { echo -e "${GREEN}[INFO]${NC} $*"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $*"; }

# Validate required vars
REQUIRED_VARS=(DATABASE_NAME DATABASE_USERNAME DATABASE_PASSWORD)
MISSING=false
for var in "${REQUIRED_VARS[@]}"; do
  if [ -z "${!var:-}" ]; then
    err "$var is not set"
    MISSING=true
  fi
done

if [ "$MISSING" = true ]; then
  err "Missing required environment variables. Aborting."
  exit 1
fi

HOST="${DATABASE_HOST:-localhost}"
PORT="${DATABASE_PORT:-5432}"

info "Target: postgresql://$HOST:$PORT/${DATABASE_NAME}"
info "User:   $DATABASE_USERNAME"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$SCRIPT_DIR"

if [ ! -f pom.xml ]; then
  err "pom.xml not found in $SCRIPT_DIR. Run from project root."
  exit 1
fi

info "Running Flyway migration..."
mvn flyway:migrate -Pflyway \
  -DDATABASE_HOST="$HOST" \
  -DDATABASE_PORT="$PORT" \
  -DDATABASE_NAME="$DATABASE_NAME" \
  -DDATABASE_USERNAME="$DATABASE_USERNAME" \
  -DDATABASE_PASSWORD="$DATABASE_PASSWORD" \
  ${MAVEN_FLAGS:-}

info "Flyway migration completed."
