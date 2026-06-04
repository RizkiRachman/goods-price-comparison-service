#!/usr/bin/env bash
set -euo pipefail

# ─────────────────────────────────────────────────────────
# goods-price-comparison-service — Doctor Script
# Validates all prerequisites for local development.
# ─────────────────────────────────────────────────────────

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color
BOLD='\033[1m'

PASS=0
FAIL=0
WARN=0

check() {
  local label="$1"
  local cmd="$2"
  local hint="${3:-}"

  if eval "$cmd" >/dev/null 2>&1; then
    echo -e "  ${GREEN}✔${NC} ${label}"
    PASS=$((PASS + 1))
  else
    echo -e "  ${RED}✘${NC} ${label}"
    FAIL=$((FAIL + 1))
    if [ -n "$hint" ]; then
      echo -e "    ${YELLOW}→${NC} $hint"
    fi
  fi
}

check_warn() {
  local label="$1"
  local cmd="$2"
  local hint="${3:-}"

  if eval "$cmd" >/dev/null 2>&1; then
    echo -e "  ${GREEN}✔${NC} ${label}"
    PASS=$((PASS + 1))
  else
    echo -e "  ${YELLOW}⚠${NC} ${label} ${YELLOW}(optional)${NC}"
    WARN=$((WARN + 1))
    if [ -n "$hint" ]; then
      echo -e "    ${YELLOW}→${NC} $hint"
    fi
  fi
}

echo ""
echo -e "${BOLD}${CYAN}╔═══════════════════════════════════════════╗${NC}"
echo -e "${BOLD}${CYAN}║   Doctor — Dev Environment Check          ║${NC}"
echo -e "${BOLD}${CYAN}╚═══════════════════════════════════════════╝${NC}"
echo ""

# ── Java ──────────────────────────────────────────────────
echo -e "${BOLD}Java${NC}"
check "java available" "command -v java"
if command -v java >/dev/null 2>&1; then
  JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
  check "java version 21+" "test '$JAVA_VER' -ge 21" "Install JDK 21+ from https://adoptium.net/"
fi
check "javac available" "command -v javac" "Install JDK (not just JRE)"

# ── Maven ────────────────────────────────────────────────
echo -e "\n${BOLD}Maven${NC}"
check "mvn available" "command -v mvn" "Install Maven 3.9+ from https://maven.apache.org/download.cgi"
if command -v mvn >/dev/null 2>&1; then
  MVN_VER=$(mvn --version 2>&1 | head -1 | awk '{print $3}' | cut -d. -f1)
  check "mvn version 3.9+" "test '$MVN_VER' -ge 3" "Install Maven 3.9+"
fi

# ── Maven Settings (GitHub Packages) ─────────────────────
echo -e "\n${BOLD}Maven Settings${NC}"
check "~/.m2/settings.xml exists" "test -f ~/.m2/settings.xml" \
  "Create ~/.m2/settings.xml with GitHub token for goods-price-comparison-api dependency"
if [ -f ~/.m2/settings.xml ]; then
  TOKEN_COUNT=$(grep -c 'github' ~/.m2/settings.xml 2>/dev/null || true)
  check "settings.xml references github" "test '$TOKEN_COUNT' -gt 0" \
    "Add GitHub server config to ~/.m2/settings.xml"
fi

# ── Docker ────────────────────────────────────────────────
echo -e "\n${BOLD}Docker${NC}"
check_warn "docker available" "command -v docker" \
  "Install Docker Desktop from https://docker.com (needed for local PostgreSQL, etc.)"
if command -v docker >/dev/null 2>&1; then
  check_warn "docker daemon running" "docker info >/dev/null 2>&1" \
    "Start Docker Desktop or dockerd"
  if docker info >/dev/null 2>&1; then
    check_warn "docker compose available" "docker compose version >/dev/null 2>&1" \
      "Docker Compose is included in Docker Desktop"
  fi
fi

# ── Local Ports ──────────────────────────────────────────
echo -e "\n${BOLD}Port Availability${NC}"
check "port 8080 available" "! lsof -i :8080 -sTCP:LISTEN >/dev/null 2>&1" \
  "Something is already running on port 8080. Stop it first (lsof -i :8080)."
check_warn "postgres port 5432 available" \
  "! lsof -i :5432 -sTCP:LISTEN >/dev/null 2>&1 || docker compose ps postgres 2>/dev/null | grep -q Up" \
  "PostgreSQL on 5432: either Docker service or local install fine"

# ── Project Structure ────────────────────────────────────
echo -e "\n${BOLD}Project${NC}"
check "pom.xml exists" "test -f pom.xml"
check "opencode.json exists" "test -f opencode.json"
check_warn ".env not committed" "test ! -f .env" \
  "Create .env from .env.example if needed"
check_warn "mvn dependency:resolve" "mvn dependency:resolve -q 2>/dev/null" \
  "Run 'mvn dependency:resolve' to download dependencies"

# ── Summary ──────────────────────────────────────────────
echo -e "\n${BOLD}${CYAN}╔═══════════════════════════════════════════╗${NC}"
echo -e "${BOLD}${CYAN}║   Results                                 ║${NC}"
echo -e "${BOLD}${CYAN}╚═══════════════════════════════════════════╝${NC}"
echo ""
echo -e "  ${GREEN}${PASS} passed${NC}"
if [ "$FAIL" -gt 0 ]; then
  echo -e "  ${RED}${FAIL} failed${NC}"
fi
if [ "$WARN" -gt 0 ]; then
  echo -e "  ${YELLOW}${WARN} warnings${NC}"
fi
echo ""

if [ "$FAIL" -gt 0 ]; then
  echo -e "  ${RED}✘ Fix the failed checks above before running 'mvn spring-boot:run'${NC}"
  exit 1
else
  echo -e "  ${GREEN}✔ Environment looks good. Run 'mvn spring-boot:run' to start.${NC}"
fi
echo ""
