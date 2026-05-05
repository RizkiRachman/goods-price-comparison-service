#!/bin/bash
# =============================================
# Drop all tables from goods-price-comparison-service
# Then restart the app to recreate via Hibernate
# =============================================

set -e

DB_HOST="${DATABASE_HOST:-localhost}"
DB_PORT="${DATABASE_PORT:-5432}"
DB_NAME="${DATABASE_NAME:-goods-price-service}"
DB_USER="${DATABASE_USERNAME:-goods-price-service-user}"
export PGPASSWORD="${DATABASE_PASSWORD:-2b0a22d2a568f1b0fe5e15a3bb2ef71e}"


if command -v psql &> /dev/null; then
  PSQL="psql -h $DB_HOST -p $DB_PORT -U $DB_USER -d $DB_NAME"
elif command -v docker &> /dev/null; then
  CONTAINER=$(docker ps --filter "name=postgres" --format "{{.Names}}" 2>/dev/null | head -1)
  if [ -z "$CONTAINER" ]; then
    echo "Error: No PostgreSQL container found running."
    exit 1
  fi
  PSQL="docker exec -i -e PGPASSWORD=$PGPASSWORD $CONTAINER psql -U $DB_USER -d $DB_NAME"
else
  echo "Error: psql not found and Docker is not available."
  exit 1
fi

echo "Dropping all tables from ${DB_NAME}..."

$PSQL <<SQL
DROP TABLE IF EXISTS receipt_items CASCADE;
DROP TABLE IF EXISTS prices CASCADE;
DROP TABLE IF EXISTS product_price_summaries CASCADE;
DROP TABLE IF EXISTS receipts CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS stores CASCADE;
DROP TABLE IF EXISTS flyway_schema_history CASCADE;
SQL

echo "Done. All tables dropped. Restart the app to recreate schema."
