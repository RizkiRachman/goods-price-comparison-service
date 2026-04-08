#!/bin/bash
# Setup Project Quay as local container registry
# Provides UI, image scanning, and advanced features

set -e

QUAY_VERSION="v3.10.0"
QUAY_PORT="8080"
QUAY_CONFIG_PORT="8081"
POSTGRES_PASSWORD="quaypassword"
REDIS_PASSWORD="redispassword"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }
info() { echo -e "${BLUE}[NOTE]${NC} $1"; }

show_help() {
    cat << 'EOF'
Quay Local Registry Setup

Usage: ./setup-quay.sh [command]

Commands:
  setup       Start Quay with PostgreSQL and Redis
  config      Open Quay config editor (port 8081)
  status      Check Quay status
  stop        Stop all Quay containers
  restart     Restart Quay
  delete      Remove Quay and all data
  logs        View Quay logs

First Time Setup:
  1. Run: ./setup-quay.sh setup
  2. Open: http://localhost:8081 (config editor)
  3. Download config, save as config.yaml
  4. Validate and shutdown config editor
  5. Quay will start at http://localhost:8080

Default Credentials:
  - UI: http://localhost:8080
  - Config: http://localhost:8081
  - Initial: Create superuser on first login

EOF
}

# Create required directories
create_dirs() {
    mkdir -p quay/{config,storage}
    chmod 777 quay/storage
    log "✅ Created Quay directories"
}

# Start PostgreSQL
start_postgres() {
    log "Starting PostgreSQL for Quay..."
    
    if podman ps --format "{{.Names}}" | grep -q "^quay-postgres$"; then
        log "PostgreSQL already running"
        return
    fi
    
    podman run -d \
        --name quay-postgres \
        --restart always \
        -e POSTGRES_USER=quayuser \
        -e POSTGRES_PASSWORD="${POSTGRES_PASSWORD}" \
        -e POSTGRES_DB=quay \
        -v "$(pwd)/quay/postgres-data:/var/lib/postgresql/data:Z" \
        docker.io/library/postgres:13-alpine
    
    sleep 5
    log "✅ PostgreSQL started"
}

# Start Redis
start_redis() {
    log "Starting Redis for Quay..."
    
    if podman ps --format "{{.Names}}" | grep -q "^quay-redis$"; then
        log "Redis already running"
        return
    fi
    
    podman run -d \
        --name quay-redis \
        --restart always \
        -e REDIS_PASSWORD="${REDIS_PASSWORD}" \
        docker.io/library/redis:6-alpine \
        redis-server --requirepass "${REDIS_PASSWORD}"
    
    log "✅ Redis started"
}

# Start Quay Config Editor
start_config() {
    log "Starting Quay Config Editor on port ${QUAY_CONFIG_PORT}..."
    
    if podman ps --format "{{.Names}}" | grep -q "^quay-config$"; then
        podman rm -f quay-config 2>/dev/null || true
    fi
    
    podman run -d \
        --name quay-config \
        -p "${QUAY_CONFIG_PORT}:8080" \
        -v "$(pwd)/quay/config:/conf/stack:Z" \
        "quay.io/projectquay/quay:${QUAY_VERSION}" \
        config secret
    
    log "✅ Config editor started"
    info "Open: http://localhost:${QUAY_CONFIG_PORT}"
    info "Download config, save as quay/config/config.yaml"
    info "Then run: ./setup-quay.sh restart"
}

# Start Quay Registry
start_quay() {
    # Check if config exists
    if [ ! -f "quay/config/config.yaml" ]; then
        warn "Quay config not found at quay/config/config.yaml"
        info "Starting config editor for initial setup..."
        start_postgres
        start_redis
        start_config
        return
    fi
    
    log "Starting Quay Registry on port ${QUAY_PORT}..."
    
    if podman ps --format "{{.Names}}" | grep -q "^quay-registry$"; then
        log "Quay already running"
        return
    fi
    
    start_postgres
    start_redis
    
    podman run -d \
        --name quay-registry \
        --restart always \
        -p "${QUAY_PORT}:8080" \
        -v "$(pwd)/quay/config:/conf/stack:Z" \
        -v "$(pwd)/quay/storage:/datastorage:Z" \
        --privileged \
        "quay.io/projectquay/quay:${QUAY_VERSION}"
    
    sleep 5
    
    if podman ps | grep -q "quay-registry"; then
        log "✅ Quay Registry started"
        info "UI: http://localhost:${QUAY_PORT}"
        info "Create superuser on first login"
    else
        error "Failed to start Quay"
    fi
}

# Show status
show_status() {
    echo "📊 Quay Status:"
    echo ""
    echo "Containers:"
    podman ps --filter "name=quay" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    echo ""
    
    if curl -s "http://localhost:${QUAY_PORT}/health/instance" > /dev/null 2>&1; then
        log "Quay is healthy"
    else
        warn "Quay may not be fully ready"
    fi
}

# View logs
show_logs() {
    podman logs -f quay-registry 2>/dev/null || error "Quay not running"
}

# Stop all
stop_all() {
    log "Stopping Quay components..."
    podman stop quay-registry quay-config quay-redis quay-postgres 2>/dev/null || true
    log "✅ Stopped"
}

# Restart
restart_all() {
    stop_all
    sleep 2
    start_quay
}

# Delete everything
delete_all() {
    warn "Removing all Quay data..."
    podman rm -f quay-registry quay-config quay-redis quay-postgres 2>/dev/null || true
    rm -rf quay/
    log "✅ Quay removed completely"
}

# Configure Kind for Quay
configure_kind() {
    log "Configuring Kind for Quay registry..."
    
    local kind_node=$(docker ps --filter "name=control-plane" --format "{{.Names}}" | head -1)
    if [ -z "$kind_node" ]; then
        error "Kind cluster not found"
        return 1
    fi
    
    local host_ip=$(ip route get 1 2>/dev/null | awk '{print $7; exit}' || echo "host.docker.internal")
    local quay_endpoint="${host_ip}:${QUAY_PORT}"
    
    # Configure containerd
    docker exec "${kind_node}" mkdir -p /etc/containerd/certs.d/"${quay_endpoint}"
    docker exec "${kind_node}" bash -c "cat > /etc/containerd/certs.d/${quay_endpoint}/hosts.toml <<EOF
server = \"http://${quay_endpoint}\"
[host.\"http://${quay_endpoint}\"]
  capabilities = [\"pull\", \"resolve\", \"push\"]
  skip_verify = true
EOF"
    
    docker exec "${kind_node}" systemctl restart containerd 2>/dev/null || true
    
    log "✅ Kind configured for Quay at ${quay_endpoint}"
}

# Main
case "${1:-setup}" in
    setup|start)
        create_dirs
        start_quay
        ;;
    config)
        start_config
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs
        ;;
    stop)
        stop_all
        ;;
    restart)
        restart_all
        ;;
    delete|cleanup)
        delete_all
        ;;
    kind-config)
        configure_kind
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        error "Unknown command: $1"
        show_help
        exit 1
        ;;
esac
