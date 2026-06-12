#!/usr/bin/env bash
# =============================================
# PostgreSQL Database Health Analyzer
# =============================================
# Usage: ./scripts/db-analyze.sh
#
# Environment variables (in priority order):
#   DATABASE_URL      - Full connection URI (postgresql://...)
#   DATABASE_HOST     - PostgreSQL host (default: localhost)
#   DATABASE_PORT     - PostgreSQL port (default: 5432)
#   DATABASE_NAME     - Database name
#   DATABASE_USERNAME - Database user
#   DATABASE_PASSWORD - Database password
# =============================================

set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'
BOLD='\033[1m'

err()  { echo -e "${RED}[ERROR]${NC} $*" >&2; }
info() { echo -e "${GREEN}[INFO]${NC} $*"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $*"; }

# ── Connection Setup ──────────────────────────────────────

setup_connection() {
  if [ -n "${DATABASE_URL:-}" ]; then
    PSQL_CMD=(psql -q -X -d "$DATABASE_URL")
  else
    REQUIRED_VARS=(DATABASE_NAME DATABASE_USERNAME DATABASE_PASSWORD)
    MISSING=false
    for var in "${REQUIRED_VARS[@]}"; do
      if [ -z "${!var:-}" ]; then
        err "$var is not set"
        MISSING=true
      fi
    done
    if [ "$MISSING" = true ]; then
      err "Set DATABASE_URL or all of DATABASE_NAME, DATABASE_USERNAME, DATABASE_PASSWORD."
      exit 1
    fi
    export PGHOST="${DATABASE_HOST:-localhost}"
    export PGPORT="${DATABASE_PORT:-5432}"
    export PGDATABASE="$DATABASE_NAME"
    export PGUSER="$DATABASE_USERNAME"
    export PGPASSWORD="$DATABASE_PASSWORD"
    PSQL_CMD=(psql -q -X)
  fi
}

run_psql() {
  "${PSQL_CMD[@]}" -c "$1"
}

run_psql_tuples() {
  "${PSQL_CMD[@]}" -t -A -c "$1" 2>/dev/null | grep -v '^$' || true
}

check_connection() {
  if ! run_psql "SELECT 1" >/dev/null 2>&1; then
    err "Cannot connect to PostgreSQL."
    if [ -n "${DATABASE_URL:-}" ]; then
      err "  DATABASE_URL=$DATABASE_URL"
    else
      err "  ${PGHOST}:${PGPORT}/${PGDATABASE} as ${PGUSER}"
    fi
    exit 1
  fi
}

# ── Section Helpers ───────────────────────────────────────

section_header() {
  echo ""
  echo -e "${CYAN}══════════════════════════════════════════════════════════════${NC}"
  echo -e "${CYAN}  $1${NC}"
  echo -e "${CYAN}══════════════════════════════════════════════════════════════${NC}"
  echo ""
}

print_table() {
  local output
  output=$(run_psql "$1" 2>/dev/null)
  if echo "$output" | grep -qE '\(0 rows\)'; then
    local row_count
    row_count=$(echo "$output" | tail -1 | grep -oE '^\([0-9]+ rows?\)' || echo "(0 rows)")
    if [ "$row_count" = "(0 rows)" ]; then
      echo "  ${YELLOW}None found${NC}"
      return
    fi
  fi
  echo "$output"
}

# ── Section 1: Quick Health ───────────────────────────────

section_quick_health() {
  section_header "1. Quick Health"

  local version
  version=$(run_psql_tuples "SELECT version();")
  echo -e "  ${CYAN}Version:${NC}     ${version:-unknown}"

  local db_size
  db_size=$(run_psql_tuples "SELECT pg_size_pretty(pg_database_size(current_database()));")
  echo -e "  ${CYAN}DB Size:${NC}     ${db_size:-unknown}"

  local active_conn
  active_conn=$(run_psql_tuples "SELECT count(*) FROM pg_stat_activity WHERE state = 'active';")
  local total_conn
  total_conn=$(run_psql_tuples "SELECT count(*) FROM pg_stat_activity;")

  local conn_color="$GREEN"
  if [ "${active_conn:-0}" -gt 50 ]; then
    conn_color="$RED"
  elif [ "${active_conn:-0}" -gt 20 ]; then
    conn_color="$YELLOW"
  fi
  echo -e "  ${CYAN}Connections:${NC}  ${conn_color}${active_conn} active${NC} / ${total_conn} total"

  local uptime
  uptime=$(run_psql_tuples "SELECT pg_postmaster_start_time();")
  echo -e "  ${CYAN}Uptime:${NC}      ${uptime:-unknown}"
}

# ── Section 2: Table Sizes ────────────────────────────────

section_table_sizes() {
  section_header "2. Table Sizes"

  local query
  query=$(cat <<'SQL'
SELECT
  relname AS "Table",
  n_live_tup AS "Row Est",
  pg_size_pretty(pg_relation_size(relid)) AS "Data Size",
  pg_size_pretty(pg_indexes_size(relid)) AS "Index Size",
  pg_size_pretty(pg_total_relation_size(relid)) AS "Total Size"
FROM pg_stat_user_tables
ORDER BY pg_total_relation_size(relid) DESC;
SQL
)
  print_table "$query"
}

# ── Section 3: Bloat Check ────────────────────────────────

section_bloat_check() {
  section_header "3. Bloat Check (Tables with Dead Tuples)"

  local query
  query=$(cat <<'SQL'
SELECT
  relname AS "Table",
  n_dead_tup AS "Dead Tuples",
  CASE
    WHEN n_live_tup + n_dead_tup = 0 THEN 0
    ELSE round(100.0 * n_dead_tup / (n_live_tup + n_dead_tup), 2)
  END AS "Dead %",
  COALESCE(last_autovacuum::text, 'never') AS "Last Autovacuum",
  COALESCE(last_autoanalyze::text, 'never') AS "Last Analyze"
FROM pg_stat_user_tables
WHERE n_dead_tup > 0
ORDER BY n_dead_tup DESC;
SQL
)

  local output
  output=$(run_psql "$query" 2>/dev/null)
  if echo "$output" | tail -1 | grep -q '(0 rows)'; then
    echo -e "  ${GREEN}No bloat detected.${NC}"
  else
    echo "$output"
  fi
}

# ── Section 4: Index Usage ────────────────────────────────

section_index_usage() {
  section_header "4. Index Usage (Unused Indexes — 0 Scans)"

  local query
  query=$(cat <<'SQL'
SELECT
  schemaname AS "Schema",
  tablename AS "Table",
  indexname AS "Index",
  idx_scan AS "Scans"
FROM pg_stat_user_indexes
WHERE idx_scan = 0
  AND indexname NOT LIKE 'flyway\_%'
ORDER BY tablename, indexname;
SQL
)

  local output
  output=$(run_psql "$query" 2>/dev/null)
  if echo "$output" | tail -1 | grep -q '(0 rows)'; then
    echo -e "  ${GREEN}All indexes are in use.${NC}"
  else
    echo "$output"
    echo ""
    warn "Consider reviewing unused indexes above. Dropping them saves write overhead and storage."
  fi
}

# ── Section 5: Vacuum Status ──────────────────────────────

section_vacuum_status() {
  section_header "5. Vacuum Status (Tables Needing VACUUM > 50 Dead Tuples)"

  local query
  query=$(cat <<'SQL'
SELECT
  relname AS "Table",
  n_dead_tup AS "Dead Tuples",
  COALESCE(last_autovacuum::text, 'never') AS "Last Autovacuum",
  COALESCE(last_autoanalyze::text, 'never') AS "Last Analyze"
FROM pg_stat_user_tables
WHERE n_dead_tup > 50
ORDER BY n_dead_tup DESC;
SQL
)

  local output
  output=$(run_psql "$query" 2>/dev/null)
  if echo "$output" | tail -1 | grep -q '(0 rows)'; then
    echo -e "  ${GREEN}No vacuum pressure.${NC}"
  else
    echo "$output"
    echo ""
    warn "Tables with > 50 dead tuples should be vacuumed. Consider autovacuum tuning."
  fi
}

# ── Section 6: FK Integrity ───────────────────────────────

section_fk_integrity() {
  section_header "6. FK Integrity"

  local query
  query=$(cat <<'SQL'
SELECT
  con.conname AS "FK Name",
  con.conrelid::regclass::text AS "Source Table",
  con.confrelid::regclass::text AS "Target Table",
  CASE
    WHEN EXISTS (
      SELECT 1 FROM pg_index idx
      WHERE idx.indrelid = con.conrelid
        AND idx.indkey::text LIKE (
          SELECT string_agg(att.attnum::text, ' ') || '%'
          FROM unnest(con.conkey) WITH ORDINALITY AS ck(attnum, ord)
          JOIN pg_attribute att ON att.attrelid = con.conrelid AND att.attnum = ck.attnum
        )
    ) THEN 'INDEXED'
    ELSE 'MISSING INDEX'
  END AS "Index Status"
FROM pg_constraint con
WHERE con.contype = 'f'
  AND con.connamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public')
ORDER BY "Source Table", "FK Name";
SQL
)

  local output
  output=$(run_psql "$query" 2>/dev/null)
  if echo "$output" | tail -1 | grep -q '(0 rows)'; then
    echo -e "  ${GREEN}No foreign key constraints defined.${NC}"
    return
  fi

  echo "$output"

  # Highlight missing indexes
  local missing
  missing=$(run_psql_tuples "$(printf "SELECT count(*) FROM pg_constraint con WHERE con.contype = 'f' AND con.connamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public') AND NOT EXISTS (SELECT 1 FROM pg_index idx WHERE idx.indrelid = con.conrelid AND idx.indkey::text LIKE (SELECT string_agg(att.attnum::text, ' ') || '%%' FROM unnest(con.conkey) WITH ORDINALITY AS ck(attnum, ord) JOIN pg_attribute att ON att.attrelid = con.conrelid AND att.attnum = ck.attnum));")")

  if [ "${missing:-0}" -gt 0 ]; then
    echo ""
    warn "$missing FK constraint(s) are missing indexes on source columns. Add indexes for DML performance."
  else
    echo ""
    echo -e "  ${GREEN}All FK columns are indexed.${NC}"
  fi
}

# ── Section 7: Schema Anti-Patterns ───────────────────────

section_schema_antipatterns() {
  section_header "7. Schema Anti-Patterns"

  # VARCHAR columns with 'date' or 'time' in name
  local query1
  query1=$(cat <<'SQL'
SELECT
  table_name AS "Table",
  column_name AS "Column",
  data_type AS "Type",
  character_maximum_length AS "Len"
FROM information_schema.columns
WHERE table_schema = 'public'
  AND data_type IN ('character varying', 'character', 'text')
  AND (LOWER(column_name) LIKE '%date%' OR LOWER(column_name) LIKE '%time%')
ORDER BY table_name, column_name;
SQL
)

  echo -e "  ${BOLD}VARCHAR columns named 'date'/'time' (use proper date/time type):${NC}"
  local output1
  output1=$(run_psql "$query1" 2>/dev/null)
  if echo "$output1" | tail -1 | grep -q '(0 rows)'; then
    echo -e "    ${GREEN}None found${NC}"
  else
    echo "$output1"
  fi

  # FLOAT columns (potential money anti-pattern)
  local query2
  query2=$(cat <<'SQL'
SELECT
  table_name AS "Table",
  column_name AS "Column",
  data_type AS "Type",
  numeric_precision AS "Precision"
FROM information_schema.columns
WHERE table_schema = 'public'
  AND data_type IN ('real', 'double precision')
ORDER BY table_name, column_name;
SQL
)

  echo ""
  echo -e "  ${BOLD}FLOAT/DOUBLE PRECISION columns (use NUMERIC for money):${NC}"
  local output2
  output2=$(run_psql "$query2" 2>/dev/null)
  if echo "$output2" | tail -1 | grep -q '(0 rows)'; then
    echo -e "    ${GREEN}None found${NC}"
  else
    echo "$output2"
    echo ""
    warn "FLOAT types lose precision. Use NUMERIC(p,s) for monetary columns."
  fi
}

# ── Section 8: Sequence Health ────────────────────────────

section_sequence_health() {
  section_header "8. Sequence Health (Exhaustion Risk)"

  local query
  query=$(cat <<'SQL'
SELECT
  sequence_name AS "Sequence",
  data_type AS "Type",
  start_value AS "Start",
  last_value AS "Last Value",
  maximum_value AS "Max",
  CASE
    WHEN maximum_value::numeric - start_value::numeric = 0 THEN 0
    ELSE round(100.0 * (last_value::numeric - start_value::numeric) / nullif(maximum_value::numeric - start_value::numeric, 0), 4)
  END AS "% Used"
FROM information_schema.sequences s
WHERE sequence_schema = 'public'
ORDER BY "% Used" DESC;
SQL
)

  local output
  output=$(run_psql "$query" 2>/dev/null)
  if echo "$output" | tail -1 | grep -q '(0 rows)'; then
    echo -e "  ${YELLOW}No sequences found (unexpected for this schema).${NC}"
    return
  fi

  echo "$output"

  # Check for sequences above 80% usage
  while IFS='|' read -r seq _ _ last_val max_val pct; do
    pct_clean="${pct// /}"
    if [ -n "$pct_clean" ] && [ "${pct_clean%.*}" -ge 80 ] 2>/dev/null; then
      echo ""
      warn "Sequence '$seq' is at ${pct_clean}% (last value: ${last_val}, max: ${max_val}). Risk of exhaustion!"
    fi
  done < <(run_psql_tuples "$query" 2>/dev/null | grep -vE '^\(' || true)
}

# ── Section 9: Summary ────────────────────────────────────

section_summary() {
  section_header "9. Summary"

  local fk_count
  fk_count=$(run_psql_tuples "SELECT count(*) FROM pg_constraint WHERE contype = 'f' AND connamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public');")

  local unused_idx_count
  unused_idx_count=$(run_psql_tuples "SELECT count(*) FROM pg_stat_user_indexes WHERE idx_scan = 0 AND indexname NOT LIKE 'flyway\_%';")

  local vacuum_count
  vacuum_count=$(run_psql_tuples "SELECT count(*) FROM pg_stat_user_tables WHERE n_dead_tup > 50;")

  local bloat_count
  bloat_count=$(run_psql_tuples "SELECT count(*) FROM pg_stat_user_tables WHERE n_dead_tup > 0;")

  local db_size
  db_size=$(run_psql_tuples "SELECT pg_size_pretty(pg_database_size(current_database()));")

  local table_count
  table_count=$(run_psql_tuples "SELECT count(*) FROM pg_stat_user_tables;")

  echo -e "  ${CYAN}Database Size:${NC}     ${db_size:-0}"
  echo -e "  ${CYAN}User Tables:${NC}       ${table_count:-0}"
  echo -e "  ${CYAN}FK Constraints:${NC}    ${fk_count:-0}"
  echo -e "  ${CYAN}Unused Indexes:${NC}    $(colorize_count "${unused_idx_count:-0}" 1 5)"
  echo -e "  ${CYAN}Tables w/ Bloat:${NC}   $(colorize_count "${bloat_count:-0}" 1 5)"
  echo -e "  ${CYAN}Tables Needing VACUUM:${NC} $(colorize_count "${vacuum_count:-0}" 1 3)"

  echo ""
  if [ "${unused_idx_count:-0}" -eq 0 ] && [ "${vacuum_count:-0}" -eq 0 ]; then
    echo -e "  ${GREEN}✔ Database looks healthy.${NC}"
  else
    echo -e "  ${YELLOW}⚠ Review items above (unused indexes, vacuum needs).${NC}"
  fi
}

colorize_count() {
  local val="$1"
  local warn_threshold="$2"
  local crit_threshold="$3"
  if [ "$val" -eq 0 ]; then
    echo -e "${GREEN}${val}${NC}"
  elif [ "$val" -le "$warn_threshold" ]; then
    echo -e "${GREEN}${val}${NC}"
  elif [ "$val" -le "$crit_threshold" ]; then
    echo -e "${YELLOW}${val}${NC}"
  else
    echo -e "${RED}${val}${NC}"
  fi
}

# ── Main ──────────────────────────────────────────────────

main() {
  setup_connection
  check_connection

  local db_name
  if [ -n "${DATABASE_URL:-}" ]; then
    db_name="$DATABASE_URL"
  else
    db_name="${DATABASE_NAME}"
  fi

  echo ""
  echo -e "${BOLD}${CYAN}╔══════════════════════════════════════════════════════════════╗${NC}"
  echo -e "${BOLD}${CYAN}║   PostgreSQL Health Report — ${db_name}${NC}"
  echo -e "${BOLD}${CYAN}╚══════════════════════════════════════════════════════════════╝${NC}"
  echo ""

  section_quick_health
  section_table_sizes
  section_bloat_check
  section_index_usage
  section_vacuum_status
  section_fk_integrity
  section_schema_antipatterns
  section_sequence_health
  section_summary

  echo ""
  echo -e "${CYAN}══════════════════════════════════════════════════════════════${NC}"
  echo -e "${CYAN}  Report complete.                                        ${NC}"
  echo -e "${CYAN}══════════════════════════════════════════════════════════════${NC}"
  echo ""
}

main
