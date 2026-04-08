#!/bin/bash
# Master setup script for local CI/CD environment
# Usage: ./setup.sh [command]

set -e

NAMESPACE="goods-price-ci"
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log() { echo -e "${GREEN}[INFO]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

show_help() {
    cat << 'EOF'
Local CI/CD Setup Script

Usage: ./setup.sh [command]

Commands:
  all              Full setup: K8s + Tekton + Configs (default)
  k8s              Apply Kubernetes resources only
  tekton           Apply Tekton pipeline resources only
  config           Apply supporting configs (maven, rbac, registry)
  run              Run the pipeline
  status           Show status
  logs             View app logs
  delete           Delete all resources
  help             Show this help

Examples:
  ./setup.sh all      # Full setup
  ./setup.sh k8s      # Apply K8s resources
  ./setup.sh run      # Run pipeline
EOF
}

apply_k8s() {
    log "Applying Kubernetes resources..."
    kubectl kustomize k8s/ | kubectl apply -f -
    log "✅ K8s resources applied"
}

apply_tekton() {
    log "Applying Tekton pipeline resources..."
    kubectl kustomize tekton/ | kubectl apply -f -
    log "✅ Tekton resources applied"
}

apply_config() {
    log "Applying supporting configs..."
    if [ -f "tekton/config/maven-settings-configmap.yaml" ]; then
        kubectl apply -f tekton/config/maven-settings-configmap.yaml -n "$NAMESPACE"
    fi
    if [ -f "tekton/config/maven-cache-pvc.yaml" ]; then
        kubectl apply -f tekton/config/maven-cache-pvc.yaml -n "$NAMESPACE"
    fi
    log "✅ Configs applied"
}

run_pipeline() {
    log "Running pipeline..."
    ./helpers/run-pipeline.sh run
}

show_status() {
    log "Status:"
    kubectl get pods -n "$NAMESPACE"
}

show_logs() {
    log "App logs:"
    kubectl logs -n "$NAMESPACE" -l app=goods-price-service --tail=50
}

delete_all() {
    warn "Deleting all resources..."
    kubectl delete -k tekton/ 2>/dev/null || true
    kubectl delete -k k8s/ 2>/dev/null || true
    log "✅ Resources deleted"
}

# Main
case "${1:-all}" in
    all)
        apply_k8s
        apply_tekton
        apply_config
        log ""
        log "✅ Setup complete! Run: ./setup.sh run"
        log ""
        log "Quick commands:"
        log "  ./setup.sh status  - Check status"
        log "  ./setup.sh logs    - View logs"
        ;;
    k8s) apply_k8s ;;
    tekton) apply_tekton ;;
    config) apply_config ;;
    run) run_pipeline ;;
    status) show_status ;;
    logs) show_logs ;;
    delete) delete_all ;;
    help|--help|-h) show_help ;;
    *) error "Unknown command: $1. Run './setup.sh help'" ;;
esac
