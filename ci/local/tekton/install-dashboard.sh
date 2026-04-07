#!/bin/bash

# Tekton Dashboard Installation Script
# Usage: ./install-dashboard.sh [install|uninstall|status|proxy]

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
    exit 1
}

info() {
    echo -e "${BLUE}[$(date +'%Y-%m-%d %H:%M:%S')] INFO: $1${NC}"
}

DASHBOARD_VERSION="latest"
NAMESPACE="tekton-pipelines"

install_dashboard() {
    log "Installing Tekton Dashboard..."
    
    # Check if kubectl is available
    if ! command -v kubectl &> /dev/null; then
        error "kubectl is not installed. Please install kubectl first."
    fi
    
    # Check if Tekton Pipelines is installed
    if ! kubectl get namespace ${NAMESPACE} &> /dev/null; then
        error "Tekton Pipelines namespace not found. Please install Tekton Pipelines first:"
        echo "  kubectl apply --filename https://storage.googleapis.com/tekton-releases/pipeline/latest/release.yaml"
        exit 1
    fi
    
    log "Installing Tekton Dashboard ${DASHBOARD_VERSION}..."
    
    # Install Dashboard in read-only mode (safer for production)
    kubectl apply --filename https://infra.tekton.dev/tekton-releases/dashboard/${DASHBOARD_VERSION}/release.yaml
    
    # Wait for Dashboard to be ready
    log "Waiting for Tekton Dashboard to be ready..."
    kubectl wait --for=condition=ready --timeout=180s pod -l app.kubernetes.io/part-of=tekton-dashboard -n ${NAMESPACE} 2>/dev/null || \
        warn "Dashboard pods still starting, continuing anyway..."
    
    # Check if dashboard pod is running
    if kubectl get pods -n ${NAMESPACE} | grep -q "tekton-dashboard"; then
        log "Tekton Dashboard installed successfully!"
        echo ""
        echo "========================================"
        echo "Access the Dashboard:"
        echo ""
        echo "Option 1 - kubectl port-forward (Recommended for local):"
        echo "  kubectl port-forward -n ${NAMESPACE} service/tekton-dashboard 9097:9097"
        echo "  Then open: http://localhost:9097"
        echo ""
        echo "Option 2 - kubectl proxy:"
        echo "  kubectl proxy --port=8080"
        echo "  Then open: http://localhost:8080/api/v1/namespaces/${NAMESPACE}/services/tekton-dashboard:http/proxy/"
        echo ""
        echo "Option 3 - Access via Ingress (requires Ingress controller):"
        echo "  See: https://tekton.dev/docs/dashboard/install/#using-an-ingress-rule"
        echo ""
        echo "To uninstall: ./install-dashboard.sh uninstall"
        echo "========================================"
    else
        error "Dashboard installation failed. Check logs with: kubectl logs -n ${NAMESPACE} -l app.kubernetes.io/part-of=tekton-dashboard"
    fi
}

install_readwrite() {
    log "Installing Tekton Dashboard in READ/WRITE mode..."
    
    if ! command -v kubectl &> /dev/null; then
        error "kubectl is not installed. Please install kubectl first."
    fi
    
    if ! kubectl get namespace ${NAMESPACE} &> /dev/null; then
        error "Tekton Pipelines namespace not found. Please install Tekton Pipelines first."
        exit 1
    fi
    
    log "Installing Tekton Dashboard ${DASHBOARD_VERSION} (read/write mode)..."
    
    # Install Dashboard in read/write mode
    kubectl apply --filename https://infra.tekton.dev/tekton-releases/dashboard/${DASHBOARD_VERSION}/release-full.yaml
    
    log "Waiting for Tekton Dashboard to be ready..."
    kubectl wait --for=condition=ready --timeout=180s pod -l app.kubernetes.io/part-of=tekton-dashboard -n ${NAMESPACE} 2>/dev/null || \
        warn "Dashboard pods still starting, continuing anyway..."
    
    log "Tekton Dashboard (read/write) installed successfully!"
    echo ""
    echo "========================================"
    echo "Access the Dashboard:"
    echo "  kubectl port-forward -n ${NAMESPACE} service/tekton-dashboard 9097:9097"
    echo "  Then open: http://localhost:9097"
    echo ""
    echo "WARNING: Read/write mode allows creating, editing, and deleting resources!"
    echo "========================================"
}

uninstall_dashboard() {
    log "Uninstalling Tekton Dashboard..."
    
    if ! command -v kubectl &> /dev/null; then
        error "kubectl is not installed."
    fi
    
    # Try to delete both read-only and read-write releases
    kubectl delete --filename https://infra.tekton.dev/tekton-releases/dashboard/${DASHBOARD_VERSION}/release.yaml 2>/dev/null || true
    kubectl delete --filename https://infra.tekton.dev/tekton-releases/dashboard/${DASHBOARD_VERSION}/release-full.yaml 2>/dev/null || true
    
    log "Tekton Dashboard uninstalled successfully!"
}

show_status() {
    log "Tekton Dashboard Status:"
    
    if ! command -v kubectl &> /dev/null; then
        error "kubectl is not installed."
    fi
    
    echo ""
    echo "Pods:"
    kubectl get pods -n ${NAMESPACE} -l app.kubernetes.io/part-of=tekton-dashboard 2>/dev/null || echo "  No dashboard pods found"
    
    echo ""
    echo "Service:"
    kubectl get service -n ${NAMESPACE} tekton-dashboard 2>/dev/null || echo "  No dashboard service found"
}

start_proxy() {
    local port=${1:-9097}
    log "Starting kubectl proxy for Tekton Dashboard..."
    log "Access at: http://localhost:${port}/api/v1/namespaces/tekton-pipelines/services/tekton-dashboard:http/proxy/"
    log "Press Ctrl+C to stop"
    echo ""
    
    kubectl proxy --port=${port}
}

start_proxy_always_run() {
    local port=${1:-9097}
    log "Starting kubectl proxy for Tekton Dashboard (auto-restart mode)..."
    log "Access at: http://localhost:${port}/api/v1/namespaces/tekton-pipelines/services/tekton-dashboard:http/proxy/"
    log "Press Ctrl+C to stop"
    echo ""
    
    # Loop to auto-restart on connection failure
    while true; do
        log "Starting proxy on port ${port}..."
        kubectl proxy --port=${port}
        
        log "Connection lost, restarting in 2 seconds..."
        sleep 2
    done
}

show_help() {
    echo "Tekton Dashboard Installation Script"
    echo ""
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  install       Install Tekton Dashboard (read-only mode, recommended)"
    echo "  install-rw    Install Tekton Dashboard (read/write mode)"
    echo "  uninstall     Uninstall Tekton Dashboard"
    echo "  status        Show dashboard status"
    echo "  proxy         Start kubectl proxy for dashboard access (default port 9097)"
    echo "  proxy-always  Start proxy with auto-restart on connection loss"
    echo "  proxy <port>  Start proxy on custom port (e.g., $0 proxy 8081)"
    echo "  help          Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 install              # Install dashboard"
    echo "  $0 proxy                # Access dashboard at http://localhost:9097/api/v1/namespaces/tekton-pipelines/services/tekton-dashboard:http/proxy/"
    echo "  $0 proxy 8081           # Use custom port 8081"
    echo "  $0 proxy-always 8081    # Auto-restart with custom port"
    echo ""
    echo "Prerequisites:"
    echo "  - kubectl installed and configured"
    echo "  - Tekton Pipelines installed"
    echo ""
    echo "For more information: https://tekton.dev/docs/dashboard/install/"
}

case "${1:-}" in
    install)
        install_dashboard
        ;;
    install-rw|install-readwrite)
        install_readwrite
        ;;
    uninstall|delete|remove)
        uninstall_dashboard
        ;;
    status)
        show_status
        ;;
    proxy|port-forward)
        start_proxy "${2:-}"
        ;;
    proxy-always|port-forward-always)
        start_proxy_always_run "${2:-}"
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        show_help
        exit 1
        ;;
esac
