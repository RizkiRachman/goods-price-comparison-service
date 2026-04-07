#!/bin/bash

# Local CI/CD Stack Quick Start Script
# Usage: ./start.sh [jenkins|tekton|status|stop]

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

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

check_prerequisites() {
    log "Checking prerequisites..."
    
    if ! command -v docker &> /dev/null; then
        error "Docker is not installed. Please install Docker Desktop."
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        error "Docker Compose is not installed."
    fi
    
    if ! docker info &> /dev/null; then
        error "Docker is not running. Please start Docker Desktop."
    fi
    
    log "Prerequisites check passed!"
}

start_jenkins() {
    log "Starting Jenkins CI/CD stack..."
    
    docker-compose up -d
    
    log "Waiting for services..."
    sleep 10
    
    log "Services started!"
    echo ""
    echo "========================================"
    echo "Service URLs:"
    echo "  Application:     http://localhost:8080"
    echo "  Docker Registry: http://localhost:5000"
    echo "  Jenkins:         http://localhost:8082 (admin/admin123)"
    echo "  Kong Proxy:      http://localhost:8000"
    echo "  Kong Admin:      http://localhost:8001"
    echo "  Prometheus:      http://localhost:9090"
    echo "  Grafana:         http://localhost:3000 (admin/admin)"
    echo "========================================"
}

stop_stack() {
    log "Stopping CI/CD stack..."
    docker-compose down
    log "Stack stopped"
}

show_status() {
    log "Stack status:"
    docker-compose ps
}

check_kind_prerequisites() {
    log "Checking kind prerequisites..."
    
    if ! command -v kubectl &> /dev/null; then
        error "kubectl is not installed. Please install: brew install kubectl"
    fi
    
    if ! command -v kind &> /dev/null; then
        error "kind is not installed. Please install: brew install kind"
    fi
    
    if ! command -v tkn &> /dev/null; then
        warn "Tekton CLI (tkn) not found. Install: brew install tektoncd-cli"
    fi
    
    log "Kind prerequisites check passed!"
}

start_tekton() {
    log "Setting up Tekton CI/CD on kind cluster..."
    
    check_kind_prerequisites
    
    local cluster_name="goods-price-ci"
    
    # Check if cluster exists
    if kind get clusters | grep -q "^${cluster_name}$"; then
        log "Kind cluster '${cluster_name}' already exists"
        log "Switching to cluster context..."
        kubectl config use-context "kind-${cluster_name}"
    else
        log "Creating kind cluster '${cluster_name}'..."
        kind create cluster --name "${cluster_name}" --config=- <<EOF
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
  - role: control-plane
    extraPortMappings:
      - containerPort: 80
        hostPort: 8080
        protocol: TCP
      - containerPort: 443
        hostPort: 8443
        protocol: TCP
EOF
        log "Kind cluster created successfully!"
    fi
    
    # Install Tekton Pipelines
    log "Installing Tekton Pipelines..."
    kubectl apply --filename https://storage.googleapis.com/tekton-releases/pipeline/latest/release.yaml
    
    # Wait for Tekton to be ready
    log "Waiting for Tekton Pipelines to be ready..."
    kubectl wait --for=condition=ready --timeout=300s pod -l app.kubernetes.io/part-of=tekton-pipelines -n tekton-pipelines 2>/dev/null || \
        warn "Tekton pods still starting, continue anyway..."
    
    # Install Tekton Triggers (optional but useful)
    log "Installing Tekton Triggers..."
    kubectl apply --filename https://storage.googleapis.com/tekton-releases/triggers/latest/release.yaml
    kubectl apply --filename https://storage.googleapis.com/tekton-releases/triggers/latest/interceptors.yaml
    
    # Create namespace and PVCs
    log "Setting up Tekton resources..."
    kubectl create namespace goods-price-ci --dry-run=client -o yaml | kubectl apply -f -
    kubectl apply -f tekton/maven-cache-pvc.yaml
    
    # Apply the pipeline
    log "Applying Tekton pipeline..."
    kubectl apply -f tekton/pipeline.yaml
    
    log "Tekton setup complete!"
    echo ""
    echo "========================================"
    echo "Tekton CI/CD is ready!"
    echo ""
    echo "Next steps:"
    echo "1. Install Tekton Dashboard:"
    echo "   ./tekton/install-dashboard.sh"
    echo ""
    echo "2. Run pipeline:"
    echo "   tkn pipeline start goods-price-service-pipeline \\"
    echo "     --showlog \\"
    echo "     -n goods-price-ci \\"
    echo "     -w name=shared-workspace,volumeClaimTemplateFile=tekton/workspace-template.yaml \\"
    echo "     -w name=maven-cache,claimName=maven-cache-pvc"
    echo ""
    echo "3. Or use the provided PipelineRun:"
    echo "   kubectl create -f tekton/pipeline-run.yaml"
    echo ""
    echo "Cluster: kind-${cluster_name}"
    echo "Namespace: goods-price-ci"
    echo "========================================"
}

stop_tekton() {
    log "Stopping Tekton kind cluster..."
    
    local cluster_name="goods-price-ci"
    
    if kind get clusters | grep -q "^${cluster_name}$"; then
        kind delete cluster --name "${cluster_name}"
        log "Kind cluster '${cluster_name}' deleted"
    else
        warn "Kind cluster '${cluster_name}' not found"
    fi
}

show_help() {
    echo "Local CI/CD Stack - Quick Start"
    echo ""
    echo "Usage: $0 [command]"
    echo ""
    echo "Commands:"
    echo "  jenkins       Start Jenkins-based CI/CD stack (Docker Compose)"
    echo "  tekton        Setup Tekton on kind cluster (Kubernetes-native CI/CD)"
    echo "  tekton-stop   Stop and delete kind cluster for Tekton"
    echo "  status        Show stack status"
    echo "  stop          Stop the stack"
    echo "  logs          View logs"
    echo ""
    echo "Tekton Dashboard Access:"
    echo "  After 'tekton' command completes, run:"
    echo "  kubectl port-forward -n tekton-pipelines service/tekton-dashboard 9097:9097"
    echo "  Then open: http://localhost:9097"
    echo ""
}

case "${1:-}" in
    jenkins)
        check_prerequisites
        start_jenkins
        ;;
    tekton)
        start_tekton
        ;;
    tekton-stop)
        stop_tekton
        ;;
    stop)
        stop_stack
        ;;
    status)
        show_status
        ;;
    logs)
        docker-compose logs -f
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        show_help
        exit 1
        ;;
esac
