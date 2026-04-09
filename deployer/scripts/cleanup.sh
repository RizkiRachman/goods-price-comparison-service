#!/bin/bash
# Clean up Tekton resources

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DEPLOYER_DIR="$(dirname "$SCRIPT_DIR")"

RED='\033[0;31m'; YELLOW='\033[1;33m'; GREEN='\033[0;32m'; NC='\033[0m'
warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }
log()   { echo -e "${GREEN}[INFO]${NC} $1"; }

# Load environment
if [ -f "$DEPLOYER_DIR/.env" ]; then
    export $(grep -v '^#' "$DEPLOYER_DIR/.env" | xargs)
fi

PIPELINE_NAMESPACE="${PIPELINE_NAMESPACE:-goods-price-ci}"

warn "This will delete all Tekton resources in namespace: ${PIPELINE_NAMESPACE}"
read -p "Are you sure? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "Aborted."
    exit 0
fi

log "Deleting PipelineRuns..."
kubectl delete pipelineruns --all -n "$PIPELINE_NAMESPACE" 2>/dev/null || true

log "Deleting TaskRuns..."
kubectl delete taskruns --all -n "$PIPELINE_NAMESPACE" 2>/dev/null || true

log "Deleting Pipelines..."
kubectl delete pipelines --all -n "$PIPELINE_NAMESPACE" 2>/dev/null || true

log "Deleting Tasks..."
kubectl delete tasks --all -n "$PIPELINE_NAMESPACE" 2>/dev/null || true

log "Deleting TriggerBindings..."
kubectl delete triggerbindings --all -n "$PIPELINE_NAMESPACE" 2>/dev/null || true

log "Deleting Secrets..."
kubectl delete secret registry-credentials -n "$PIPELINE_NAMESPACE" 2>/dev/null || true

log "Deleting RoleBindings..."
kubectl delete rolebinding tekton-triggers-rolebinding -n "$PIPELINE_NAMESPACE" 2>/dev/null || true
kubectl delete clusterrolebinding tekton-triggers-${PIPELINE_NAMESPACE}-cluster-binding 2>/dev/null || true

log "Deleting ClusterRoleBinding..."
kubectl delete clusterrolebinding ${PIPELINE_SERVICE_ACCOUNT:-tekton-sa}-binding 2>/dev/null || true

log "Deleting ClusterRole..."
kubectl delete clusterrole ${PIPELINE_SERVICE_ACCOUNT:-tekton-sa}-role 2>/dev/null || true

log "Deleting ServiceAccounts..."
kubectl delete serviceaccount tekton-triggers-sa -n "$PIPELINE_NAMESPACE" 2>/dev/null || true
kubectl delete serviceaccount ${PIPELINE_SERVICE_ACCOUNT:-tekton-sa} -n "$PIPELINE_NAMESPACE" 2>/dev/null || true

log "Deleting Namespace..."
kubectl delete namespace "$PIPELINE_NAMESPACE" 2>/dev/null || true

log "✅ Cleanup complete!"
