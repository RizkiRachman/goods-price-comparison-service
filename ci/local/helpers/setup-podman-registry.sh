#!/bin/bash
# Setup Podman as the local registry for Kind cluster
# This makes Podman the single source of truth for images

set -e

REGISTRY_NAME="local-registry"
REGISTRY_PORT="5000"
HOST_IP=$(ip route get 1 2>/dev/null | awk '{print $7; exit}' || echo "host.docker.internal")

echo "🐳 Setting up Podman Registry for Kind..."

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

# 1. Start Podman registry
start_registry() {
    log "Starting Podman registry on port ${REGISTRY_PORT}..."
    
    # Check if already running
    if podman ps --format "{{.Names}}" | grep -q "^${REGISTRY_NAME}$"; then
        log "Registry already running"
    else
        # Remove if exists but stopped
        podman rm -f "${REGISTRY_NAME}" 2>/dev/null || true
        
        # Start new registry
        podman run -d \
            -p "${REGISTRY_PORT}:${REGISTRY_PORT}" \
            --name "${REGISTRY_NAME}" \
            --restart always \
            registry:2
        
        log "✅ Registry started at localhost:${REGISTRY_PORT}"
    fi
    
    # Test registry
    sleep 2
    if curl -s "http://localhost:${REGISTRY_PORT}/v2/_catalog" > /dev/null 2>&1; then
        log "✅ Registry responding"
    else
        warn "Registry may not be ready yet"
    fi
}

# 2. Configure Kind to use Podman registry
configure_kind() {
    log "Configuring Kind cluster for Podman registry..."
    
    # For macOS/Linux, use host.docker.internal or host IP
    local registry_endpoint
    if [[ "$OSTYPE" == "darwin"* ]]; then
        # macOS
        registry_endpoint="host.docker.internal:${REGISTRY_PORT}"
    else
        # Linux - use host IP
        registry_endpoint="${HOST_IP}:${REGISTRY_PORT}"
    fi
    
    info "Registry endpoint for Kind: ${registry_endpoint}"
    
    # Get Kind control plane container name
    local kind_node=$(docker ps --filter "name=control-plane" --format "{{.Names}}" | head -1)
    if [ -z "$kind_node" ]; then
        error "Kind cluster not found. Create it first with: ./setup/start.sh"
        exit 1
    fi
    
    log "Configuring Kind node: ${kind_node}"
    
    # Configure containerd to use insecure registry
    docker exec "${kind_node}" mkdir -p /etc/containerd/certs.d/"${registry_endpoint}"
    docker exec "${kind_node}" bash -c "cat > /etc/containerd/certs.d/${registry_endpoint}/hosts.toml <<EOF
server = \"http://${registry_endpoint}\"
[host.\"http://${registry_endpoint}\"]
  capabilities = [\"pull\", \"resolve\", \"push\"]
  skip_verify = true
EOF"
    
    # Restart containerd
    docker exec "${kind_node}" systemctl restart containerd || \
        docker exec "${kind_node}" bash -c "kill -SIGHUP 1"  # Fallback restart
    
    log "✅ Kind configured for Podman registry"
}

# 3. Update pipeline config
update_pipeline_config() {
    log "Updating pipeline configuration..."
    
    local registry_url="${HOST_IP}:${REGISTRY_PORT}"
    
    # Update pipeline-run.yaml
    sed -i.bak "s|value: \"10.89.0.2:32242\"|value: \"${registry_url}\"|g" \
        "$(dirname "$0")/../tekton/pipeline-run.yaml" 2>/dev/null || true
    
    # Update helper scripts
    sed -i.bak "s|REGISTRY_URL=\"10.89.0.2:32242\"|REGISTRY_URL=\"${registry_url}\"|g" \
        "$(dirname "$0")/run-pipeline.sh" 2>/dev/null || true
    
    sed -i.bak "s|REGISTRY_URL=\"10.89.0.2:32242\"|REGISTRY_URL=\"${registry_url}\"|g" \
        "$(dirname "$0")/podman-registry.sh" 2>/dev/null || true
    
    rm -f "$(dirname "$0")"/*.bak 2>/dev/null || true
    
    log "✅ Pipeline configs updated to use: ${registry_url}"
}

# 4. Show summary
show_summary() {
    echo ""
    echo "═══════════════════════════════════════════════════"
    echo "  ✅ Podman Registry Setup Complete"
    echo "═══════════════════════════════════════════════════"
    echo ""
    echo "Registry URL: http://localhost:${REGISTRY_PORT}"
    echo "Kind Endpoint: http://${HOST_IP}:${REGISTRY_PORT}"
    echo ""
    echo "Next steps:"
    echo "  1. Re-apply pipeline: ./setup.sh tekton"
    echo "  2. Run pipeline: ./setup.sh run"
    echo "  3. Images will be in:"
    echo "     - Podman: podman images"
    echo "     - Registry: curl http://localhost:${REGISTRY_PORT}/v2/_catalog"
    echo ""
    echo "View registry images:"
    echo "  podman ps | grep ${REGISTRY_NAME}"
    echo "  curl http://localhost:${REGISTRY_PORT}/v2/_catalog"
    echo ""
}

# Main
case "${1:-setup}" in
    setup|start)
        start_registry
        configure_kind
        update_pipeline_config
        show_summary
        ;;
    stop)
        log "Stopping Podman registry..."
        podman stop "${REGISTRY_NAME}" 2>/dev/null || true
        log "✅ Registry stopped"
        ;;
    restart)
        podman restart "${REGISTRY_NAME}" 2>/dev/null || true
        configure_kind
        log "✅ Registry restarted"
        ;;
    status)
        if podman ps | grep -q "${REGISTRY_NAME}"; then
            log "Registry is running"
            curl -s "http://localhost:${REGISTRY_PORT}/v2/_catalog" | jq .
        else
            warn "Registry is not running"
        fi
        ;;
    delete|cleanup)
        warn "Removing registry and all images..."
        podman rm -f "${REGISTRY_NAME}" 2>/dev/null || true
        log "✅ Registry removed"
        ;;
    *)
        echo "Usage: $0 [setup|stop|restart|status|delete]"
        exit 1
        ;;
esac
