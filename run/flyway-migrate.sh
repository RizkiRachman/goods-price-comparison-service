#!/bin/bash

# Flyway Migration Script for Goods Price Comparison Service
# Usage: ./run/flyway-migrate.sh

echo "=================================="
echo "Running Flyway Migrations"
echo "=================================="
echo ""

# Database configuration
DB_HOST="${DATABASE_HOST:-localhost}"
DB_PORT="${DATABASE_PORT:-5432}"
DB_NAME="${DATABASE_NAME:-goods-price-service}"
DB_USER="${DATABASE_USERNAME:-goods-price-service-user}"
DB_PASS="${DATABASE_PASSWORD:-2b0a22d2a568f1b0fe5e15a3bb2ef71e}"

echo "Database: $DB_HOST:$DB_PORT/$DB_NAME"
echo "User: $DB_USER"
echo ""

# Check if database exists, create if not
echo "Checking database..."
if ! docker exec postgres psql -U "$DB_USER" -lqt | cut -d \| -f 1 | grep -qw "$DB_NAME"; then
    echo "Database $DB_NAME does not exist. Creating..."
    docker exec postgres psql -U "$DB_USER" -c "CREATE DATABASE $DB_NAME;"
    echo "Database created."
else
    echo "Database $DB_NAME exists."
fi
echo ""

# Run migrations
echo "Running Flyway migrations..."
mvn flyway:migrate -Pflyway \
    -Dflyway.url="jdbc:postgresql://$DB_HOST:$DB_PORT/$DB_NAME" \
    -Dflyway.user="$DB_USER" \
    -Dflyway.password="$DB_PASS"

if [ $? -eq 0 ]; then
    echo ""
    echo "Migrations completed successfully!"
    echo ""
    echo "=================================="
    echo "Database is ready for use!"
    echo "=================================="
else
    echo ""
    echo "=================================="
    echo "Migration failed!"
    echo "=================================="
    exit 1
fi