#!/bin/bash
# Kubernetes Dashboard - Install, get token, and start

set -e

GREEN='\033[0;32m'; YELLOW='\033[1;33m'; RED='\033[0;31m'; NC='\033[0m'
log()   { echo -e "${GREEN}[INFO]${NC} $1"; }
warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

show_help() {
    cat << EOF
Kubernetes Dashboard Management Script

Usage:
  $0 <command>

Commands:
  install     Install Kubernetes Dashboard
  token       Get login token
  start       Start dashboard port-forward
  help        Show this help message

Examples:
  $0 install    # Install dashboard
  $0 token      # Get login token
  $0 start      # Start dashboard at https://localhost:8443
EOF
}

COMMAND="${1:-}"

if [ -z "$COMMAND" ] || [ "$COMMAND" = "help" ] || [ "$COMMAND" = "-h" ]; then
    show_help
    exit 0
fi

case "$COMMAND" in
    install)
        log "Installing Kubernetes Dashboard..."
        kubectl apply -f https://raw.githubusercontent.com/kubernetes/dashboard/v2.7.0/aio/deploy/recommended.yaml
        kubectl create serviceaccount dashboard-admin -n kubernetes-dashboard
        kubectl create clusterrolebinding dashboard-admin --clusterrole=cluster-admin --serviceaccount=kubernetes-dashboard:dashboard-admin
        log "✅ Kubernetes Dashboard installed successfully!"
        log ""
        log "Next steps:"
        log "  $0 token  # Get login token"
        log "  $0 start  # Start dashboard"
        ;;
    token)
        if ! kubectl get namespace kubernetes-dashboard &>/dev/null; then
            error "Kubernetes Dashboard not found. Install it first: $0 install"
        fi
        log "Generating dashboard token..."
        kubectl -n kubernetes-dashboard create token dashboard-admin
        ;;
    start)
        if ! kubectl get namespace kubernetes-dashboard &>/dev/null; then
            error "Kubernetes Dashboard not found. Install it first: $0 install"
        fi
        log "Starting Kubernetes Dashboard port-forward..."
        log "Dashboard will be available at: https://localhost:8443"
        log ""
        log "To get the login token, run in another terminal:"
        log "  $0 token"
        log ""
        log "Press Ctrl+C to stop the port-forward"
        log ""
        kubectl port-forward -n kubernetes-dashboard svc/kubernetes-dashboard 8443:443
        ;;
    *)
        error "Unknown command: $COMMAND. Use 'help' to see available commands."
        ;;
esac
