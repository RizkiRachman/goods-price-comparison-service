#!/bin/bash
# Podman helper script for Tekton pipeline local development
# Provides commands to configure Kind + Podman + Registry

set -e

NAMESPACE="goods-price-ci"
REGISTRY_NODE_PORT="32242"
KIND_NODE_NAME="goods-price-ci-control-plane"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Get registry URL
get_registry_url() {
    local node_ip
    node_ip=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[?(@.type=="InternalIP")].address}' 2>/dev/null)
    if [ -n "$node_ip" ]; then
        echo "${node_ip}:${REGISTRY_NODE_PORT}"
    else
        echo "localhost:${REGISTRY_NODE_PORT}"
    fi
}

# Configure Kind node containerd for insecure registry
configure_kind_registry() {
    log "Configuring Kind node for insecure registry access..."
    
    local registry_url
    registry_url=$(get_registry_url)
    local node_ip=${registry_url%:*}
    
    log "Registry URL: $registry_url"
    log "Node IP: $node_ip"
    
    # Check if using Podman or Docker
    if command -v podman &> /dev/null && podman ps | grep -q "$KIND_NODE_NAME"; then
        log "Using Podman..."
        podman exec "$KIND_NODE_NAME" bash -c "
            mkdir -p /etc/containerd/certs.d/$registry_url
            cat > /etc/containerd/certs.d/$registry_url/hosts.toml <<'INNEREOF'
server = \"http://$registry_url\"
[host.\"http://$registry_url\"]
  capabilities = [\"pull\", \"resolve\"]
  skip_verify = true
INNEREOF
        "
        podman exec "$KIND_NODE_NAME" systemctl restart containerd
        log "✓ Kind node configured (Podman)"
    elif command -v docker &> /dev/null && docker ps | grep -q "$KIND_NODE_NAME"; then
        log "Using Docker..."
        docker exec "$KIND_NODE_NAME" bash -c "
            mkdir -p /etc/containerd/certs.d/$registry_url
            cat > /etc/containerd/certs.d/$registry_url/hosts.toml <<'INNEREOF'
server = \"http://$registry_url\"
[host.\"http://$registry_url\"]
  capabilities = [\"pull\", \"resolve\"]
  skip_verify = true
INNEREOF
        "
        docker exec "$KIND_NODE_NAME" systemctl restart containerd
        log "✓ Kind node configured (Docker)"
    else
        error "Neither Podman nor Docker found, or Kind node '$KIND_NODE_NAME' not running"
        exit 1
    fi
    
    log "Waiting for containerd to restart..."
    sleep 5
    
    log "✅ Kind node registry configuration complete!"
}

# Configure local Podman/Docker for insecure registry
configure_local_registry() {
    log "Configuring local container runtime for insecure registry..."
    
    local registry_url
    registry_url=$(get_registry_url)
    local node_ip=${registry_url%:*}
    
    # Podman configuration
    if command -v podman &> /dev/null; then
        local registries_conf="${HOME}/.config/containers/registries.conf"
        mkdir -p "$(dirname "$registries_conf")"
        
        # Check if already configured
        if grep -q "location.*=.*\"$node_ip:$REGISTRY_NODE_PORT\"" "$registries_conf" 2>/dev/null; then
            log "Podman registry config already exists"
        else
            cat >> "$registries_conf" <<EOF

[[registry]]
location = "$node_ip:$REGISTRY_NODE_PORT"
insecure = true
EOF
            log "✓ Podman configured for insecure registry"
        fi
    fi
    
    # Docker configuration (if using Docker desktop)
    if command -v docker &> /dev/null && [ -f "${HOME}/.docker/daemon.json" ]; then
        warn "Docker desktop detected. You may need to add insecure-registries in Docker Desktop settings GUI"
        warn "Add: $node_ip:$REGISTRY_NODE_PORT to insecure registries"
    fi
    
    log "✅ Local container runtime configured!"
}

# Full setup - configure everything
setup_all() {
    log "Setting up registry access for Kind + Podman/Docker..."
    
    configure_kind_registry
    configure_local_registry
    
    log ""
    log "✅ Setup complete! You can now pull/push images from the registry."
    log ""
    log "Test the registry:"
    log "  curl http://$(get_registry_url)/v2/_catalog"
    log ""
    log "Restart your deployment if it was failing:"
    log "  kubectl rollout restart deployment/goods-price-service -n $NAMESPACE"
}

# Check registry status
status() {
    local registry_url
    registry_url=$(get_registry_url)
    
    log "Registry Status"
    log "==============="
    log "Registry URL: $registry_url"
    log ""
    
    # Check if registry pod is running
    if kubectl get pods -n "$NAMESPACE" -l app=registry 2>/dev/null | grep -q Running; then
        log "✓ Registry pod is running"
    else
        warn "✗ Registry pod is not running"
    fi
    
    # Check if service exists
    if kubectl get svc registry -n "$NAMESPACE" &>/dev/null; then
        log "✓ Registry service exists"
    else
        warn "✗ Registry service not found"
    fi
    
    # Test catalog endpoint
    log ""
    log "Testing registry catalog..."
    if curl -s "http://$registry_url/v2/_catalog" 2>/dev/null; then
        log "✓ Registry is accessible"
    else
        warn "✗ Cannot reach registry catalog"
    fi
    
    # Check Kind node containerd config
    log ""
    log "Kind node containerd config:"
    if podman exec "$KIND_NODE_NAME" cat "/etc/containerd/certs.d/$registry_url/hosts.toml" 2>/dev/null || \
       docker exec "$KIND_NODE_NAME" cat "/etc/containerd/certs.d/$registry_url/hosts.toml" 2>/dev/null; then
        log "✓ Kind node has registry config"
    else
        warn "✗ Kind node missing registry config - run: $0 setup"
    fi
}

# Show help
help() {
    cat << 'EOF'
Podman Helpers for Tekton Local Development

Usage: ./podman-helpers.sh [command]

Commands:
  setup         Configure Kind node and local Podman for insecure registry
  kind          Configure only the Kind node containerd
  local         Configure only local Podman/Docker settings
  status        Check registry and configuration status
  url           Print the registry URL
  help          Show this help

Examples:
  ./podman-helpers.sh setup     # Full setup (run this first)
  ./podman-helpers.sh status    # Check if everything is configured
  ./podman-helpers.sh url       # Get registry URL for pipeline

Notes:
  - This script auto-detects Podman vs Docker
  - The Kind node name is expected to be: goods-price-ci-control-plane
  - Registry runs on NodePort 32242 by default

EOF
}

# Main
case "${1:-help}" in
    setup|all)
        setup_all
        ;;
    kind|node)
        configure_kind_registry
        ;;
    local|host)
        configure_local_registry
        ;;
    status|check)
        status
        ;;
    url|registry-url)
        get_registry_url
        ;;
    help|--help|-h)
        help
        ;;
    *)
        error "Unknown command: $1"
        help
        exit 1
        ;;
esac
