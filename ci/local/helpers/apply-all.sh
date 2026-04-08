#!/bin/bash
# Apply all Tekton resources in the correct order

set -e

NAMESPACE="goods-price-ci"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../tekton" && pwd)"

GREEN='\033[0;32m'; YELLOW='\033[1;33m'; RED='\033[0;31m'; NC='\033[0m'
log()   { echo -e "${GREEN}[INFO]${NC} $1"; }
warn()  { echo -e "${YELLOW}[WARN]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; exit 1; }

command -v kubectl &>/dev/null || error "kubectl not found"

log "Ensuring namespace ${NAMESPACE} exists..."
kubectl create namespace "${NAMESPACE}" --dry-run=client -o yaml | kubectl apply -f -

log "Applying Tasks..."
kubectl apply -f "${SCRIPT_DIR}/01-tasks/git-clone.yaml"          -n "${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/01-tasks/maven-build.yaml"        -n "${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/01-tasks/maven-test.yaml"         -n "${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/01-tasks/kaniko-build.yaml"       -n "${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/01-tasks/podman-push.yaml"        -n "${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/01-tasks/deploy.yaml"             -n "${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/01-tasks/deploy-from-registry.yaml" -n "${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/01-tasks/docker-build-quay.yaml"  -n "${NAMESPACE}"

log "Applying Pipelines..."
kubectl apply -f "${SCRIPT_DIR}/pipeline.yaml"             -n "${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/pipeline-quay.yaml"        -n "${NAMESPACE}"
kubectl apply -f "${SCRIPT_DIR}/pipeline-deploy-only.yaml" -n "${NAMESPACE}"

log "Applying supporting configs..."
[ -f "${SCRIPT_DIR}/config/maven-settings-configmap.yaml" ] && \
  kubectl apply -f "${SCRIPT_DIR}/config/maven-settings-configmap.yaml" -n "${NAMESPACE}"
[ -f "${SCRIPT_DIR}/config/rbac.yaml" ] && \
  kubectl apply -f "${SCRIPT_DIR}/config/rbac.yaml" -n "${NAMESPACE}"
[ -f "${SCRIPT_DIR}/config/maven-cache-pvc.yaml" ] && \
  kubectl apply -f "${SCRIPT_DIR}/config/maven-cache-pvc.yaml" -n "${NAMESPACE}"

log "✅ All resources applied. Run: ./helpers/run-pipeline.sh run"
