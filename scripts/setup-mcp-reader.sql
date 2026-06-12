-- =============================================
-- Create a read-only role for the Postgres MCP server
-- Run this against your PostgreSQL database:
--   psql -h localhost -U postgres -d goods-price-service -f scripts/setup-mcp-reader.sql
-- =============================================

-- Create read-only role
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'mcp_reader') THEN
        CREATE ROLE mcp_reader LOGIN PASSWORD 'change-me-to-something-secure';
    END IF;
END
$$;

-- Set safe defaults
ALTER ROLE mcp_reader SET statement_timeout = '30s';
ALTER ROLE mcp_reader CONNECTION LIMIT 3;
ALTER ROLE mcp_reader SET log_statement = 'all';

-- Grant minimum access
GRANT CONNECT ON DATABASE goods-price-service TO mcp_reader;
GRANT USAGE ON SCHEMA public TO mcp_reader;

-- PostgreSQL 14+ grants read-only access to all current and future tables
GRANT pg_read_all_data TO mcp_reader;

-- For PostgreSQL < 14, uncomment these instead:
-- GRANT SELECT ON ALL TABLES IN SCHEMA public TO mcp_reader;
-- ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO mcp_reader;
