#!/bin/bash
# Apply all Tekton resources in the correct order

set -e

NAMESPACE="goods-price-ci"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

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
    exit 1
}

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    error "kubectl is not installed"
fi

# Ensure namespace exists
log "Ensuring namespace ${NAMESPACE} exists..."
kubectl create namespace "${NAMESPACE}" --dry-run=client -o yaml | kubectl apply -f - 2>/dev/null || true

# Apply Tasks first (Pipeline depends on them)
log "Applying Tasks..."
kubectl apply -f "${SCRIPT_DIR}/tasks/git-clone.yaml" -n "${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/tasks/maven-build.yaml" -n "${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/tasks/docker-build-push.yaml" -n "${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/tasks/deploy-to-local.yaml" -n "${NAMESPACE}"

# Apply Pipeline
log "Applying Pipeline..."
kubectl apply -f "${SCRIPT_DIR}/pipeline.yaml" -n "${NAMESPACE}"

# Apply supporting resources
log "Applying supporting resources..."

# Maven settings ConfigMap
if [ -f "${SCRIPT_DIR}/maven-settings-configmap.yaml" ]; then
    kubectl apply -f "${SCRIPT_DIR}/maven-settings-configmap.yaml" -n "${NAMESPACE}"
fi

# RBAC
if [ -f "${SCRIPT_DIR}/rbac.yaml" ]; then
    kubectl apply -f "${SCRIPT_DIR}/rbac.yaml" -n "${NAMESPACE}"
fi

# Registry
if [ -f "${SCRIPT_DIR}/registry.yaml" ]; then
    kubectl apply -f "${SCRIPT_DIR}/registry.yaml" -n "${NAMESPACE}"
fi

# Trigger Template (optional)
if [ -f "${SCRIPT_DIR}/triggers/trigger-template.yaml" ]; then
    log "Applying Trigger Template..."
    kubectl apply -f "${SCRIPT_DIR}/triggers/trigger-template.yaml" -n "${NAMESPACE}" 2>/dev/null || warn "Trigger template may require Tekton Triggers to be installed"
fi

log ""
log "✅ All resources applied successfully!"
log ""
log "To run the pipeline:"
log "  ./run-pipeline.sh run"
