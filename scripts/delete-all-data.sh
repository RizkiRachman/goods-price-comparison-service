#!/bin/bash
# =============================================
# Delete all data from goods-price-comparison-service
# Tables are deleted in FK dependency order
# Includes flyway_schema_history for full reset
# =============================================

set -e

DB_HOST="${DATABASE_HOST:-localhost}"
DB_PORT="${DATABASE_PORT:-5432}"
DB_NAME="${DATABASE_NAME:-goods-price-service}"
DB_USER="${DATABASE_USERNAME:-goods-price-service-user}"
export PGPASSWORD="${DATABASE_PASSWORD:-2b0a22d2a568f1b0fe5e15a3bb2ef71e}"

# Use docker exec if psql is not installed locally
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
  echo "Install PostgreSQL client or run the container first."
  exit 1
fi

echo "Deleting all data from ${DB_NAME}..."

$PSQL <<SQL
-- Child tables first (FK dependencies)
DELETE FROM receipt_items;
DELETE FROM prices;
DELETE FROM product_price_summaries;

-- Parent tables
DELETE FROM receipts;
DELETE FROM products;
DELETE FROM stores;

-- Flyway migration history (resets migration state)
DELETE FROM flyway_schema_history;
SQL

echo "Done. All data deleted."
